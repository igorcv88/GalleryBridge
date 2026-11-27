package dev.gallerybridge

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.nio.file.Files
import java.time.Instant
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.apache.commons.compress.archivers.zip.Zip64Mode
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream

class TakeoutTest {
    @Test fun corpusUsesExactBytesAcrossFortyFourZip64Parts() {
        val seen = HashSet<String>()
        var members = 0
        repeat(44) { part ->
            val zip = Files.createTempFile("takeout-fixture", ".zip")
            try {
                ZipArchiveOutputStream(zip.toFile()).use { out ->
                    out.setUseZip64(Zip64Mode.Always)
                    val first = part * 49_045 / 44
                    val last = (part + 1) * 49_045 / 44
                    for (id in first until last) {
                        val name = "Takeout/Google Fotos/2026/${id % 300}.jpg"
                        // The repeated path within each ZIP deliberately exercises ordinal identity.
                        for ((entryPath, payload) in listOf(
                            name to "fixture-media-$id",
                            "$name.supplemental-metadata.json" to
                                """{"title":"${id % 300}.jpg","photoTakenTime":{"timestamp":"1767225600"}}"""
                        )) {
                            out.putArchiveEntry(ZipArchiveEntry(entryPath))
                            out.write(payload.toByteArray())
                            out.closeArchiveEntry()
                        }
                    }
                    out.putArchiveEntry(ZipArchiveEntry("Takeout/Google Fotos/Álbum/prova.jpg"))
                    out.write("fixture-media-0".toByteArray())
                    out.closeArchiveEntry()
                }
                Files.newByteChannel(zip).use { channel ->
                    Archive(channel).use { archive ->
                        repeat(archive.count) { i ->
                            val entry = archive.member(i)
                            val bytes = archive.verify(entry)
                            members++
                            if (entry.mime != null) seen.add(bytes.sha)
                            if (entry.sidecar) assertTrue(entry.name.endsWith("metadata.json"))
                        }
                    }
                }
            } finally { Files.deleteIfExists(zip) }
        }
        assertEquals(49_045, seen.size)
        assertEquals(98_134, members)
    }

    @Test fun traversalSymlinksAndDirectoryPrivacy() {
        archive(mapOf("../escape.jpg" to "payload")) { path ->
            Files.newByteChannel(path).use { assertFails { Archive(it) } }
        }
        archive(mapOf("a/.nomedia" to "", "a/sub/p.jpg" to "hidden", "other/p.jpg" to "public")) { path ->
            Files.newByteChannel(path).use { channel ->
                Archive(channel).use {
                    assertTrue(it.member(1).restricted)
                    assertFalse(it.member(2).restricted)
                    assertNotEquals(it.verify(it.member(1)).sha, it.verify(it.member(2)).sha)
                }
            }
        }
        val zip = Files.createTempFile("symlink-zip", ".zip")
        try {
            ZipArchiveOutputStream(zip.toFile()).use { out ->
                val link = ZipArchiveEntry("link.jpg").apply { unixMode = 0xa1ff }
                out.putArchiveEntry(link)
                out.write("elsewhere".toByteArray())
                out.closeArchiveEntry()
            }
            Files.newByteChannel(zip).use { assertFails { Archive(it) } }
        } finally { Files.deleteIfExists(zip) }
    }

    @Test fun corruptPayloadTruncationAndPauseRequireRetry() {
        archive(mapOf("image.jpg" to "a distinct fixture payload"), stored = true) { path ->
            Files.newByteChannel(path).use { channel ->
                Archive(channel).use { zip ->
                    assertFails { zip.verify(zip.member(0), checkpoint = { throw BridgeFailure(Reason.PAUSED) }) }
                    assertEquals(26L, zip.verify(zip.member(0)).bytes)
                }
            }
            val source = Files.readAllBytes(path)
            val start = source.toString(Charsets.ISO_8859_1).indexOf("a distinct fixture payload")
            source[start] = 'x'.code.toByte()
            Files.write(path, source)
            Files.newByteChannel(path).use { channel ->
                Archive(channel).use { assertFails { it.verify(it.member(0)) } }
            }
            Files.write(path, source.copyOf(source.size - 12))
            Files.newByteChannel(path).use { assertFails { Archive(it) } }
        }
    }

    @Test fun wallTimeNeverBecomesAnInstantAndConflictRequiresComparableDestination() {
        val key = cameraTime("2019:10:22 17:41:47-0300", null, "Keys:CreationDate")
        val wall = cameraTime("2019:10:22 17:41:47", null, "ExifIFD:DateTimeOriginal")
        assertEquals(Instant.parse("2019-10-22T20:41:47Z"), key.instant)
        assertEquals(TimeBasis.LOCAL_WALL_TIME, wall.basis)
        assertNull(wall.instant)
        assertEquals(TimeConflict.CONTEXT_REQUIRED, compareTime(key, wall))
        assertEquals(TimeConflict.NO_CONFLICT, compareTime(key, key.copy(instant = key.instant?.plusSeconds(2))))
        assertEquals(TimeConflict.DIFFERENT, compareTime(key, key.copy(instant = key.instant?.plusSeconds(26))))
        assertEquals(TimeConflict.DIFFERENT_DAY_RANGE, compareTime(key, key.copy(instant = key.instant?.plusSeconds(102_749))))
    }

    @Test fun structuredQuickTimeMetadataAndLivePairUseIdentifiers() {
        val contentId = "01234567-89ab-cdef-0123-456789abcdef"
        val keyNames = listOf("com.apple.quicktime.creationdate", "com.apple.quicktime.content.identifier")
        val keyData = buffer {
            writeInt(0)
            writeInt(keyNames.size)
            for (key in keyNames) {
                writeInt(key.length + 8)
                writeBytes("mdta")
                writeBytes(key)
            }
        }
        val values = listOf("2019-10-22T17:41:47-03:00", contentId).mapIndexed { i, value ->
            val data = atom("data", buffer { writeInt(1); writeInt(0); writeBytes(value) })
            buffer { writeInt(data.size + 8); writeInt(i + 1); write(data) }
        }.reduce(ByteArray::plus)
        val meta = atom("meta", byteArrayOf(0, 0, 0, 0) + atom("keys", keyData) + atom("ilst", values))
        val bytes = atom("moov", atom("mvhd", buffer {
            writeInt(0); writeInt(3_780_000_000L.toInt()); writeInt(0)
        }) + meta)
        val parsed = inspectQuickTime(bytes.inputStream(), bytes.size.toLong())
        assertEquals(contentId, parsed.contentId)
        assertEquals(Instant.parse("2019-10-22T20:41:47Z"), parsed.keys?.instant)
        assertTrue(parsed.container?.instant != parsed.keys?.instant)
        val video = MediaId("video", true, parsed.contentId)
        val image = MediaId("photo", false, contentId.uppercase())
        val unrelated = (1..3001).map { MediaId("same-name-$it", false, null) }
        assertEquals(image, livePair(video, unrelated + listOf(image, video)))
        assertNull(livePair(video, listOf(video, image, image.copy(sha = "collision"))))
    }

    @Test fun favoritesAndGpsKeepUncertainty() {
        val parsed = parseSidecar("""
            {"title":"a.jpg","favorited":true,"description":"fixture",
             "photoTakenTime":{"timestamp":"1767225600"},"geoData":{"latitude":0,"longitude":0},
             "geoDataExif":{"latitude":-3.7,"longitude":-38.5}}
        """.trimIndent())
        assertEquals(true, parsed?.favorite)
        assertEquals("fixture", parsed?.description)
        assertEquals(listOf(Location(-3.7, -38.5, "Takeout:geoDataExif")), parsed?.locations)
        assertNull(parseSidecar("""{"favorited":true,"isFavorite":false}""")?.favorite)
        assertNull(parseSidecar("""{"title":"nome do álbum"}"""))
    }

    @Test fun safetyGatesArePerBatchAndUnknownRemainsBlocked() {
        val verified = listOf(VerifiedQueueEntry(42, "old/item", 6, 3))
        assertEquals(QueueScope.VERIFIED_EXTERNAL, scopeFor(4, QueueObservation(42, "old/item", 6), verified))
        assertEquals(QueueScope.UNKNOWN, scopeFor(4, QueueObservation(42, "moved/item", 6), verified))
        assertEquals(QueueScope.UNKNOWN, scopeFor(4, QueueObservation(42, "old/item", 7), verified))
        assertFalse(freeUpAllowed(setOf("a", "b"), setOf("a"), true, emptyList(), true))
        assertFalse(freeUpAllowed(setOf("a"), setOf("a"), true, emptyList(), false))
        assertEquals(LocalPresence.LOCAL_ABSENCE_UNKNOWN, localPresence(false, false))
        assertEquals(LocalPresence.MISSING_WITH_SDK_LINK, localPresence(false, true))
        val wifi = Conditions(true, 25, 90, true, false, 0, 0.2f, false)
        assertNull(blockReason(wifi))
        assertEquals(Reason.WIFI_REQUIRED, blockReason(wifi.copy(wifi = false)))
        assertEquals(Reason.THERMAL, blockReason(wifi.copy(thermal = 3)))
        assertEquals(Reason.THERMAL, blockReason(wifi.copy(headroom = 0.95f)))
        assertEquals(Reason.BATTERY, blockReason(wifi.copy(battery = 17)))
    }

    private fun atom(name: String, data: ByteArray) =
        buffer { writeInt(data.size + 8); writeBytes(name); write(data) }

    private fun buffer(write: DataOutputStream.() -> Unit): ByteArray {
        val bytes = ByteArrayOutputStream()
        DataOutputStream(bytes).use(write)
        return bytes.toByteArray()
    }

    private fun archive(entries: Map<String, String>, stored: Boolean = false, test: (java.nio.file.Path) -> Unit) {
        val path = Files.createTempFile("archive-fixture", ".zip")
        try {
            ZipOutputStream(Files.newOutputStream(path)).use { out ->
                entries.forEach { (name, content) ->
                    val bytes = content.toByteArray()
                    val entry = ZipEntry(name)
                    if (stored) {
                        entry.method = ZipEntry.STORED
                        entry.size = bytes.size.toLong()
                        entry.crc = java.util.zip.CRC32().apply { update(bytes) }.value
                    }
                    out.putNextEntry(entry)
                    out.write(bytes)
                    out.closeEntry()
                }
            }
            test(path)
        } finally { Files.deleteIfExists(path) }
    }
}
