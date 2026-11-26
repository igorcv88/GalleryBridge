package dev.gallerybridge

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.time.OffsetDateTime
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class SyncReviewTest {
    private val start = 1_800_000_000_000L

    @Test fun reportsDrainingRateForLargeBacklog() {
        val before = SyncSample(start, 33_342, 12, 2, null)
        val current = before.copy(observedAt = start + 30_000, commits = 33_024)
        assertEquals(SyncAssessment(SyncHealth.ACTIVE_DRAINING, 636.0), assessSync(before, current))
    }

    @Test fun requiresCompleteUnchangedRetryIdentityBeforeDiagnosingStall() {
        val rows = (1L..6L).map { SyncRetry(it, it + 1, 70, 7) }
        val before = SyncSample(start, 6, 6, 20, null, completeRetrySet = rows)
        val after = before.copy(observedAt = start + 60_000, completeRetrySet = rows.map { it.copy(retries = 8) })
        assertEquals(SyncHealth.STALLED_RETRY, assessSync(before, after).health)
        assertEquals(SyncHealth.BACKOFF_SCHEDULED, assessSync(before, after.copy(completeRetrySet = rows.drop(1))).health)
        assertEquals(SyncHealth.BACKOFF_SCHEDULED, assessSync(before,
            after.copy(completeRetrySet = rows.map { it.copy(generation = 71) })).health)
    }

    @Test fun observedPauseWinsOverOldSuccessAndEmptyQueueIsNotCompletion() {
        val before = SyncSample(start, 0, 0, 20, start - 300_000)
        assertEquals(SyncHealth.RECONCILING, assessSync(null, before).health)
        val current = before.copy(observedAt = start + 40_000,
            cycle = SyncCycle.PAUSED_TRANSIENT, cycleAt = start + 35_000)
        assertEquals(SyncHealth.TRANSIENT_PAUSE, assessSync(before, current).health)
        assertEquals(SyncHealth.RECONCILING, assessSync(before, current.copy(cycleAt = start - 300_000)).health)
        val completed = current.copy(cycle = SyncCycle.COMPLETED)
        assertEquals(SyncHealth.COMPLETED_OBSERVED, assessSync(before, completed).health)
        assertEquals(SyncHealth.LAST_SUCCESS_ADVANCED,
            assessSync(before, completed.copy(lastSuccess = start + 36_000)).health)
    }

    @Test fun groupsMatchBytesAndProtectSurvivorsAndDifferentDates() {
        val protected = DuplicateFile("b", "sha", 100, true, 11)
        val duplicate = protected.copy(id = "a", protected = false)
        val different = duplicate.copy(id = "c", hash = "same size only")
        val wrongDate = duplicate.copy(id = "d", capture = 12)
        val secondFavorite = protected.copy(id = "z")
        val group = exactDuplicateGroups(listOf(duplicate, protected, different, wrongDate, secondFavorite, duplicate)).single()
        assertEquals(protected, group.keeper)
        assertEquals(listOf(duplicate), group.candidates)
        assertNull(exactDuplicateGroups(listOf(duplicate, duplicate.copy(hash = "changed"))).singleOrNull())
    }

    @Test fun fixedCapturePatchSupportsLeAndBeWithoutChangingImageBytes() {
        for (order in listOf(ByteOrder.LITTLE_ENDIAN, ByteOrder.BIG_ENDIAN)) {
            val input = jpeg(order)
            val date = OffsetDateTime.parse("2020-10-11T15:39:09.127-03:00")
            val output = patchExifCapture(input, date)
            assertEquals(input.size, output.size)
            assertContentEquals("2020:10:11 15:39:09\u0000-03:00\u0000".toByteArray(),
                output.copyOfRange(12 + 68, 12 + 95))
            assertContentEquals(input.copyOfRange(12 + 95, input.size), output.copyOfRange(12 + 95, output.size))
            assertFailsWith<BridgeFailure> { patchExifCapture(input, date.withNano(127_000_001)) }
            val overlap = input.copyOf()
            ByteBuffer.wrap(overlap).order(order).putInt(12 + 48, 68)
            assertFailsWith<BridgeFailure> { patchExifCapture(overlap, date) }
        }
    }

    private fun jpeg(order: ByteOrder): ByteArray {
        val tiff = ByteBuffer.allocate(95).order(order)
        tiff.put(if (order == ByteOrder.LITTLE_ENDIAN) "II".toByteArray() else "MM".toByteArray())
        tiff.putShort(42)
        tiff.putInt(8)
        tiff.putShort(1).putShort(0x8769.toShort()).putShort(4).putInt(1).putInt(26).putInt(0)
        tiff.putShort(3)
        tiff.putShort(0x9003.toShort()).putShort(2).putInt(20).putInt(68)
        tiff.putShort(0x9011.toShort()).putShort(2).putInt(7).putInt(88)
        tiff.putShort(0x9291.toShort()).putShort(2).putInt(4).put("127\u0000".toByteArray())
        tiff.putInt(0)
        tiff.put("1971:11:28 18:39:09\u0000+00:00\u0000".toByteArray())
        val header = byteArrayOf(-1, -40, -1, -31, 0, 103, 69, 120, 105, 102, 0, 0)
        // APP1 begins at offset 2; TIFF begins at byte 12.
        return header + tiff.array() + byteArrayOf(-1, -38, 0, 4, 0, 0, 72, -1, -39)
    }
}
