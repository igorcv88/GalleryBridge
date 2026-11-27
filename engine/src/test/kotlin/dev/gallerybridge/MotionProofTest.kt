package dev.gallerybridge

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MotionProofTest {
    private val still get() = fixture("still.jpg")
    private val movie get() = fixture("component.mov")

    @Test fun recompositionProvesExactStillIncludingMakerNoteAndVideoWithAudio() {
        val output = compose(still, movie)
        inFile(output) {
            val proof = inspectMotionJpeg(it)
            assertEquals(identity(still), proof.still)
            assertEquals(identity(movie), proof.video)
            assertTrue(proof.motionOnlyXmp)
            assertTrue((proof.videoStart ?: 0) > still.size)
        }
        assertTrue(output.takeLast(movie.size).toByteArray().contentEquals(movie))
        assertEquals("89cafe12-2456-4a89-9abc-0123456789ab",
            inspectQuickTime(movie.inputStream(), movie.size.toLong()).contentId)
    }

    @Test fun filenameDoesNotChangeJpegAndBmffNeverClaimsJpeg() {
        inFile(still, ".heic") {
            assertEquals(identity(still), inspectMotionJpeg(it).still)
            assertNull(inspectMotionJpeg(it).video)
        }
        val fakeHeic = byteArrayOf(0, 0, 0, 24) + "ftypheic".toByteArray() + ByteArray(12)
        for (bytes in listOf(movie, fakeHeic)) inFile(bytes, ".jpg") {
            assertEquals(Reason.UNSUPPORTED, assertFailsWith<BridgeFailure> { inspectMotionJpeg(it) }.reason)
        }
    }

    @Test fun truncatedAndDuplicatedPayloadsRejectMotionRange() {
        val valid = compose(still, movie)
        for (broken in listOf(valid.copyOf(valid.size - 1), valid + movie, still + movie)) inFile(broken) {
            assertFailsWith<BridgeFailure> { inspectMotionJpeg(it) }
        }
        val malformed = movie.copyOf().also { it[0] = 0x7f }
        inFile(compose(still, malformed)) { assertFailsWith<BridgeFailure> { inspectMotionJpeg(it) } }
    }

    @Test fun evenSmallDifferencesInStillOrAudioChangeProof() {
        val videoChange = movie.copyOf().also { bytes ->
            val mdat = bytes.toString(Charsets.ISO_8859_1).indexOf("mdat")
            check(mdat > 0)
            bytes[mdat + 12] = (bytes[mdat + 12].toInt() xor 1).toByte()
        }
        val stillChange = still.copyOf().also { bytes ->
            val metadata = bytes.toString(Charsets.ISO_8859_1).indexOf("Apple iOS")
            check(metadata > 0)
            bytes[metadata] = 'Z'.code.toByte()
        }
        inFile(compose(stillChange, videoChange)) {
            val proof = inspectMotionJpeg(it)
            assertNotEquals(identity(still).sha, proof.still.sha)
            assertNotEquals(identity(movie).sha, proof.video?.sha)
        }
        assertFailsWith<BridgeFailure> {
            writeJpegMotionPhoto(still.inputStream(), identity(still), videoChange.inputStream(),
                identity(movie), "video/quicktime", ByteArrayOutputStream())
        }
    }

    @Test fun samsungSefRequiresAnUnambiguousDirectoryAndNeverUsesPrefixSearch() {
        val name = "MotionPhoto_Data".toByteArray()
        val payload = ByteArrayOutputStream().apply {
            DataOutputStream(this).use {
                it.writeInt(0x0000300a)
                it.writeInt(Integer.reverseBytes(name.size))
                it.write(name)
                it.write(movie)
            }
        }.toByteArray()
        val sef = ByteArrayOutputStream().apply {
            DataOutputStream(this).use {
                it.write(still)
                it.write(payload)
                it.writeBytes("SEFH")
                it.writeInt(Integer.reverseBytes(101))
                it.writeInt(Integer.reverseBytes(1))
                it.writeInt(0x0000300a)
                it.writeInt(Integer.reverseBytes(payload.size))
                it.writeInt(Integer.reverseBytes(payload.size))
                it.writeInt(Integer.reverseBytes(24))
                it.writeBytes("SEFT")
            }
        }.toByteArray()
        inFile(sef) {
            val proof = inspectMotionJpeg(it)
            assertEquals(identity(still), proof.still)
            assertEquals(identity(movie), proof.video)
            assertEquals((still.size + 8 + name.size).toLong(), proof.videoStart)
        }
        inFile(sef.copyOf().also { it[it.size - 24] = 2 }) {
            assertFailsWith<BridgeFailure> { inspectMotionJpeg(it) }
        }
    }

    @Test fun localReviewDifferentiatesHistoricalStaticComponentAndIndependentVideo() {
        val survivor = MotionCandidate("historical", "container", "still", "video", MotionRole.HISTORICAL_MOTION)
        val other = survivor.copy(id = "new", role = MotionRole.RECOMPOSED_MOTION)
        val stillObject = MotionCandidate("camera", "still", null, null, MotionRole.STILL)
        val component = MotionCandidate("video", "video", null, null, MotionRole.VIDEO_COMPONENT)
        assertEquals(MotionDecision.KEEP_HISTORICAL, reconcileMotion(survivor, other, true, false))
        assertEquals(MotionDecision.HOLD, reconcileMotion(survivor, other, true, false, historicalHintsOnly = true))
        assertEquals(MotionDecision.KEEP_BOTH, reconcileMotion(survivor, other.copy(videoSha = "other"), true, false))
        assertEquals(MotionDecision.REVIEW_STATIC, reconcileMotion(survivor, stillObject, true, false))
        assertEquals(MotionDecision.HOLD, reconcileMotion(survivor, stillObject, false, false))
        assertEquals(MotionDecision.REVIEW_VIDEO, reconcileMotion(survivor, component, true, true))
        assertEquals(MotionDecision.HOLD, reconcileMotion(survivor, component, true, false))
        assertEquals(MotionDecision.KEEP_BOTH,
            reconcileMotion(survivor, component.copy(role = MotionRole.STANDALONE_VIDEO), true, true))
        assertEquals(MotionDecision.HOLD, reconcileMotion(survivor, survivor, true, true))
        assertNotEquals(motionDigest("one", "twothree"), motionDigest("onetwo", "three"))
    }

    @Test fun readyItemsProceedWhileAnotherWorkerIsWaiting() {
        val releaseFirst = java.util.concurrent.CountDownLatch(1)
        val completed = mutableSetOf<Int>()
        val ready = (0 until 95).iterator()
        runMotionQueue(ready, 2, { true }, { index ->
            if (index == 0) {
                check(releaseFirst.await(5, java.util.concurrent.TimeUnit.SECONDS))
            }
            index
        }, { result ->
            if (result == 2) releaseFirst.countDown()
            completed.add(result)
        })
        assertEquals((0 until 95).toSet(), completed)
    }

    @Test fun pausingDrainsStartedWorkWithoutDispatchingAnother() {
        var continueDispatching = true
        val results = mutableListOf<Int>()
        runMotionQueue((1..65).iterator(), 2, { continueDispatching }, { it }, {
            results.add(it)
            continueDispatching = false
        })
        assertEquals(setOf(1, 2), results.toSet())
    }

    private fun fixture(name: String): ByteArray =
        requireNotNull(javaClass.getResourceAsStream("/motion/$name")).use { it.readBytes() }

    private fun identity(bytes: ByteArray) = digest(bytes.inputStream())

    private fun compose(image: ByteArray, video: ByteArray): ByteArray = ByteArrayOutputStream().use {
        writeJpegMotionPhoto(image.inputStream(), identity(image), video.inputStream(), identity(video),
            "video/quicktime", it)
        it.toByteArray()
    }

    private fun inFile(bytes: ByteArray, suffix: String = ".jpg", check: (File) -> Unit) {
        val file = File.createTempFile("motion-proof-", suffix)
        try {
            file.writeBytes(bytes)
            check(file)
        } finally { file.delete() }
    }
}
