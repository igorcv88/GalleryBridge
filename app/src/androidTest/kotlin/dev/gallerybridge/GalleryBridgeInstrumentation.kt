package dev.gallerybridge

import android.app.Activity
import android.app.Instrumentation
import android.os.Bundle

class GalleryBridgeInstrumentation : Instrumentation() {
    private var selectedTest: String? = null

    override fun onCreate(arguments: Bundle?) {
        selectedTest = arguments?.getString("test")
        super.onCreate(arguments)
        start()
    }

    override fun onStart() {
        val fixtures = ImportTest(targetContext, this)
        val tests = listOf(
            "catalogAndPublication" to fixtures::selectVerifyDeduplicateResumePublishPreserveHistoricalAndExportSummary,
            "upgradeKeepsExistingCatalog" to fixtures::upgradeKeepsExistingCatalog,
            "corruptSelectedMemberCannotPublishAndResumesWithOneReceipt" to fixtures::corruptSelectedMemberCannotPublishAndResumesWithOneReceipt,
            "localRematchAfterNativeRestoreDoesNotRestage" to fixtures::localRematchAfterNativeRestoreDoesNotRestage,
            "derivativesAndHistoricalFavorites" to fixtures::derivativesAndHistoricalFavorites
        ).filter { selectedTest == null || it.first == selectedTest }
        check(tests.isNotEmpty()) { "Unknown instrumentation test: $selectedTest" }
        var failures = 0
        tests.forEachIndexed { index, (name, test) ->
            val status = Bundle().apply {
                putString("id", "GalleryBridgeInstrumentation")
                putString("class", ImportTest::class.java.name)
                putString("test", name)
                putInt("current", index + 1)
                putInt("numtests", tests.size)
            }
            sendStatus(1, status)
            try {
                test()
                sendStatus(0, status)
            } catch (failure: Throwable) {
                failures++
                status.putString("stack", failure.stackTraceToString())
                sendStatus(-2, status)
            }
        }
        val result = if (failures == 0) "\nOK (${tests.size} tests)\n" else "\nFAILURES!!! ($failures failed)\n"
        finish(if (failures == 0) Activity.RESULT_OK else Activity.RESULT_CANCELED, Bundle().apply {
            putString("stream", result)
        })
    }
}
