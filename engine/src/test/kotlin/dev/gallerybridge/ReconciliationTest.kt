package dev.gallerybridge

import java.io.ByteArrayOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReconciliationTest {
    @Test
    fun historicalFavoritesPreserve106IdentitiesAndConflicts() {
        val shaFlags = (1..106).associateWith { listOf(true, true) }
        assertEquals(106, shaFlags.values.count { historicalFavorite(it) == HistoricalFavorite.FAVORITED })
        assertEquals(HistoricalFavorite.CONFLICT, historicalFavorite(listOf(true, false)))
        assertEquals(HistoricalFavorite.EXPLICITLY_NOT_FAVORITED, historicalFavorite(listOf(false)))
        assertEquals(HistoricalFavorite.UNKNOWN, historicalFavorite(emptyList()))
        val row = requireNotNull(parseSidecar("""{"favorited":true,"isFavorite":false}"""))
        assertEquals(HistoricalFavorite.CONFLICT, historicalFavorite(row.favoriteObservations))
    }

    @Test
    fun sdkFavoriteRequiresExactlyOneCleanAndUnambiguousRow() {
        assertEquals(SdkFavorite.NO_RECORD, classifySdkFavorite(0, 0, 0, 0))
        assertEquals(SdkFavorite.AMBIGUOUS, classifySdkFavorite(2, 1, 1, 1))
        assertEquals(SdkFavorite.DIRTY, classifySdkFavorite(1, 0, 1, 0))
        assertEquals(SdkFavorite.CLEAN_UNKNOWN, classifySdkFavorite(1, 1, 1, 1))
        assertEquals(SdkFavorite.CLEAN_FAVORITE, classifySdkFavorite(1, 1, 1, 0))
        assertEquals(SdkFavorite.CLEAN_NOT_FAVORITE, classifySdkFavorite(1, 1, 0, 1))
    }

    @Test
    fun takeoutLinkUsesOnlyTheOriginalPhotosIdentity() {
        assertEquals("SyntheticPhotoKey", googlePhotosItemKey(
            "https://photos.google.com/photo/SyntheticPhotoKey?authuser=1&secret=ignore#no"))
        assertNull(googlePhotosItemKey("https://photos.google.com.evil.invalid/photo/SyntheticPhotoKey"))
        assertNull(googlePhotosItemKey("https://someone@photos.google.com/photo/SyntheticPhotoKey"))
        assertNull(googlePhotosItemKey("http://photos.google.com/photo/SyntheticPhotoKey"))
        assertNull(googlePhotosItemKey("https://photos.google.com/share/SyntheticPhotoKey"))
    }

    @Test
    fun streamWithZeroProgressFailsWithoutSkippingIntegrity() {
        val stalled = object : java.io.InputStream() {
            override fun read() = error("Unexpected single-byte read")
            override fun read(buffer: ByteArray, offset: Int, length: Int) = 0
        }
        assertEquals(Reason.INTEGRITY, assertFailsWith<BridgeFailure> { digest(stalled) }.reason)
    }

    @Test
    fun motionPayloadIsAppendedAndCorruptionFails() {
        val photo = byteArrayOf(0xff.toByte(), 0xd8.toByte(), 0xff.toByte(), 0xd9.toByte())
        val video = ByteArray(32) { it.toByte() }
        val original = digest(photo.inputStream())
        val motion = digest(video.inputStream())
        val output = ByteArrayOutputStream()
        writeJpegMotionPhoto(photo.inputStream(), original, video.inputStream(), motion, "video/mp4", output)
        val bytes = output.toByteArray()
        assertTrue(bytes.toString(Charsets.ISO_8859_1).contains("Item:Length=\"32\""))
        assertTrue(bytes.takeLast(32).toByteArray().contentEquals(video))
        assertTrue(bytes.take(2).toByteArray().contentEquals(photo.copyOf(2)))
        assertFails {
            writeJpegMotionPhoto(photo.inputStream(), original, video.copyOf(31).inputStream(),
                motion, "video/mp4", ByteArrayOutputStream())
        }
    }
}
