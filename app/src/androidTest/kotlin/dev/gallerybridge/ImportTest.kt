package dev.gallerybridge

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.UUID
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

internal class ImportTest(private val context: Context, private val instrumentation: android.app.Instrumentation) {
    fun selectVerifyDeduplicateResumePublishPreserveHistoricalAndExportSummary() {
        val id = UUID.randomUUID().toString()
        val name = "integration-$id.db"
        val source = File(context.cacheDir, "fixture-$id.zip")
        val fixture = jpeg(id, Color.BLUE)
        val second = jpeg(id, Color.RED)
        val published = mutableListOf<Uri>()
        writeZip(source, fixture, second)
        val originalZipHash = source.inputStream().use { digest(it).sha }
        try {
            Ledger(context, name).use { ledger ->
                ledger.addSource(Uri.fromFile(source).toString())
                var stop = false
                try {
                    Work(context, ledger).ingest(0, { if (stop) throw BridgeFailure(Reason.PAUSED) },
                        progress = { stop = true })
                    error("Cancellation was expected")
                } catch (failure: BridgeFailure) { assertEquals(Reason.PAUSED, failure.reason) }
                assertEquals(1, ledger.nextOrdinal(ledger.sources().single().id))
                assertEquals(0L, ledger.summary().getLong("archives_verified"))
            }
            Ledger(context, name).use { ledger ->
                val work = Work(context, ledger)
                work.ingest(0, {})
                assertEquals(1L, ledger.summary().getLong("archives_verified"))
                assertEquals(3L, ledger.summary().getLong("media_unique"))
                assertTrue(ledger.plan(10, 0).isEmpty())
                val items = ledger.plan(10, 1_000_000)
                assertEquals(2, items.size)
                assertTrue(items.any { ledger.getSidecar(it.sha)?.contains("1767225600") == true })
                ledger.recordHistoricalPending()
                val selection = items.first()
                assertTrue(ledger.albumPaths(selection.sha).isNotEmpty())
                val batch = ledger.createBatch(items)
                simulateCrashBeforeUriReceipt(context, ledger, batch)?.let(published::add)
            }
            Ledger(context, name).use { ledger ->
                val work = Work(context, ledger)
                val active = requireNotNull(ledger.activeBatch())
                work.stage(active, 0, {})
                published.addAll(ledger.published().map { Uri.parse(it.publication) })
                val summary = ledger.summary()
                assertEquals(2L, summary.getLong("media_store_receipts"))
                assertEquals(10L, summary.getLong("historical_cloud_pending"))
                assertFalse(summary.getBoolean("free_up_eligible"))
                assertEquals("UNSUPPORTED", summary.getString("cloud_evidence"))
                assertEquals(2, published.distinct().size)
                assertTrue(ledger.plan(10, 1_000_000).isEmpty())
                val indexed = ledger.published().single { ledger.getSidecar(it.sha) != null }
                assertEquals(1767225600000L, publicationDate(indexed, ledger.getSidecar(indexed.sha)))
                assertNotNull(indexed.publication)
                work.audit {}
                assertEquals(originalZipHash, source.inputStream().use { digest(it).sha })
            }
        } finally {
            // This test owns synthetic rows only. Production has no delete route.
            published.distinct().forEach { context.contentResolver.delete(it, null, null) }
            context.deleteDatabase(name)
            source.delete()
        }
    }

    fun upgradeKeepsExistingCatalog() {
        val name = "upgrade-${UUID.randomUUID()}.db"
        try {
            context.openOrCreateDatabase(name, Context.MODE_PRIVATE, null).use {
                it.execSQL("CREATE TABLE sources(id INTEGER PRIMARY KEY, uri TEXT, sha TEXT, state TEXT)")
                it.execSQL("CREATE TABLE entries(source INTEGER, directory TEXT, name TEXT)")
                it.execSQL("CREATE TABLE publication(sha TEXT PRIMARY KEY)")
                it.execSQL("INSERT INTO sources VALUES(7,'content://synthetic/zip','digest','VERIFIED')")
                it.version = 1
            }
            Ledger(context, name).use {
                assertEquals(7L, it.sources().single().id)
                assertEquals("VERIFIED", it.sources().single().state)
                assertTrue(it.pendingFavorites().isEmpty())
                assertEquals(4, it.readableDatabase.version)
            }
        } finally {
            context.deleteDatabase(name)
        }
    }

    fun derivativesAndHistoricalFavorites() {
        val id = UUID.randomUUID().toString()
        val name = "reconcile-$id.db"
        val file = File(context.cacheDir, "fixture-$id.zip")
        val fixture = jpeg(id, Color.BLUE).dropLast(id.length).toByteArray()
        val other = jpeg(id, Color.RED)
        writeZip(file, fixture, other)
        val original = file.inputStream().use { digest(it) }
        val created = mutableListOf<Uri>()
        var activity: android.app.Activity? = null
        try {
            Ledger(context, name).use { ledger ->
                ledger.addSource(Uri.fromFile(file).toString())
                val work = Work(context, ledger)
                work.ingest(0, {})
                val candidate = ledger.plan(10, 1_000_000).single { ledger.getSidecar(it.sha) != null }
                assertEquals(1, ledger.favoriteSources {}.size)
                val provenance = ledger.favoriteSourceEvidence(candidate.sha).single()
                assertEquals(candidate.source.id, provenance.part)
                assertEquals(setOf(true), provenance.flags)
                assertEquals("SyntheticPhotoKey_123", provenance.photosKey)
                assertTrue(ledger.favoriteEvidence(candidate.sha).isEmpty())
                val favorites = FavoriteReconciliation(context, ledger)
                assertEquals(FavoriteState.NOT_STAGED, favorites.preview {}.single().state)
                assertTrue(ledger.pendingFavorites().isEmpty())
                ledger.saveEdit(candidate, PreparationEdit(Field.CAPTURE_TIME, "2020-05-01T18:47:11-03:00"))
                ledger.saveEdit(candidate, PreparationEdit(Field.LOCATION, "-3.7319,-38.5267"))
                ledger.saveEdit(candidate, PreparationEdit(Field.DESCRIPTION, "Synthetic historical caption"))
                ledger.saveEdit(candidate, PreparationEdit(Field.ORIENTATION, "6"))
                ledger.saveEdit(candidate, PreparationEdit(Field.ALBUM, "Synthetic Gallery album"))
                work.stage(ledger.createBatch(listOf(candidate)), 0, {})
                val indexed = ledger.published().single()
                val uri = Uri.parse(indexed.publication).also(created::add)
                assertTrue(indexed.outputSha != null && indexed.outputSha != indexed.sha)
                val stream = requireNotNull(context.contentResolver.openInputStream(uri))
                stream.use {
                    val tags = androidx.exifinterface.media.ExifInterface(it)
                    assertEquals("2020:05:01 18:47:11", tags.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_DATETIME_ORIGINAL))
                    assertEquals("-03:00", tags.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_OFFSET_TIME_ORIGINAL))
                    assertEquals("Synthetic historical caption", tags.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_IMAGE_DESCRIPTION))
                    assertEquals("6", tags.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION))
                    val coordinates = requireNotNull(tags.latLong)
                    assertTrue(kotlin.math.abs(coordinates[0] - -3.7319) < 0.00001)
                    assertTrue(kotlin.math.abs(coordinates[1] - -38.5267) < 0.00001)
                }
                assertEquals("Pictures/${indexed.album}/Synthetic Gallery album/",
                    context.contentResolver.query(uri, arrayOf(MediaStore.MediaColumns.RELATIVE_PATH), null, null, null)
                        ?.use { if (it.moveToFirst()) it.getString(0) else null })
                assertEquals(original, file.inputStream().use { digest(it) })
                val first = favorites.preview {}.single()
                assertEquals(FavoriteState.READY, first.state)
                assertTrue(ledger.pendingFavorites().isEmpty())
                assertEquals(0, platformFavorite(uri))
                val rejected = runCatching { favorites.prepare(listOf(FavoriteChange(indexed.sha,
                    "content://media/external_primary/images/media/9", true))) {} }
                assertTrue(rejected.isFailure)
                assertTrue(ledger.pendingFavorites().isEmpty())
                val request = favorites.prepare(listOf(requireNotNull(first.change))) {}
                assertEquals(0, platformFavorite(uri))
                assertEquals(1, ledger.pendingFavorites().size)
                activity = instrumentation.startActivitySync(android.content.Intent(context, MainActivity::class.java)
                    .addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK))
                instrumentation.runOnMainSync {
                    requireNotNull(activity).startIntentSenderForResult(request.intentSender, 341, null, 0, 0, 0)
                }
                acceptPlatformFavorite(uri)
                assertEquals(1, favorites.finish(true) {})
                assertEquals(FavoriteState.LOCAL_VERIFIED, favorites.preview {}.single().state)
                assertEquals(0L, ledger.summary().getLong("favorite_remote_verified"))
                assertEquals(1L, ledger.summary().getLong("favorite_local_verified"))
                assertEquals("FAVORITED", ledger.favoriteEvidence(indexed.sha)[FavoriteObservationLevel.LOCAL_STATE])
                ledger.recordFavoriteEvidence(indexed.sha, FavoriteObservationLevel.SDK_CACHE, SdkFavorite.CLEAN_FAVORITE.name)
                ledger.recordFavoriteEvidence(indexed.sha, FavoriteObservationLevel.REMOTE_VISUAL, "FAVORITED")
                assertTrue(runCatching {
                    ledger.recordFavoriteEvidence(indexed.sha, FavoriteObservationLevel.REMOTE_INDEPENDENT, "FAVORITED")
                }.isFailure)
                assertEquals(0L, ledger.summary().getLong("favorite_remote_verified"))
                assertEquals(1L, ledger.summary().getLong("favorite_sdk_clean_positive"))
                assertEquals(1L, ledger.summary().getLong("favorite_remote_visual_positive"))
                assertFalse(ledger.summary().getBoolean("free_up_eligible"))
                work.audit {}
                assertEquals(1, ledger.published().size)
            }
        } finally {
            activity?.let { instrumentation.runOnMainSync(it::finish) }
            created.forEach { context.contentResolver.delete(it, null, null) }
            context.deleteDatabase(name)
            file.delete()
        }
    }

    fun corruptSelectedMemberCannotPublishAndResumesWithOneReceipt() {
        val id = UUID.randomUUID().toString()
        val name = "corrupt-$id.db"
        val source = File(context.cacheDir, "fixture-$id.zip")
        val payload = jpeg(id, Color.GREEN)
        val member = "JPEG/p.jpg"
        ZipOutputStream(source.outputStream()).use { zip ->
            zip.putNextEntry(ZipEntry(member).apply {
                method = ZipEntry.STORED
                size = payload.size.toLong()
                crc = CRC32().apply { update(payload) }.value
            })
            zip.write(payload)
            zip.closeEntry()
        }
        val original = source.readBytes()
        val byteToChange = original.toString(Charsets.ISO_8859_1).indexOf(id)
        check(byteToChange >= 0)
        val altered = original.copyOf()
        altered[byteToChange] = (altered[byteToChange].toInt() xor 1).toByte()
        val created = mutableSetOf<Uri>()
        try {
            Ledger(context, name).use { ledger ->
                ledger.addSource(Uri.fromFile(source).toString())
                val work = Work(context, ledger)
                work.ingest(0, {})
                val batch = ledger.createBatch(ledger.plan(1, 1_000_000))
                source.writeBytes(altered)
                var checkpoints = 0
                val failure = runCatching {
                    work.stage(batch, 0, { check(++checkpoints < 1000) { "The input stopped making progress" } })
                }.exceptionOrNull()
                assertEquals(Reason.INTEGRITY, (failure as? BridgeFailure)?.reason)
                val pending = ledger.pending(batch).single()
                val uri = Uri.parse(requireNotNull(pending.publication)).also(created::add)
                val columns = arrayOf(MediaStore.MediaColumns.IS_PENDING)
                val pendingFlag = context.contentResolver.query(uri, columns, null, null, null)?.use {
                    if (it.moveToFirst()) it.getInt(0) else null
                }
                assertEquals(1, pendingFlag)
                assertTrue(ledger.published().isEmpty())
            }
            source.writeBytes(original)
            Ledger(context, name).use { ledger ->
                val work = Work(context, ledger)
                val batch = requireNotNull(ledger.activeBatch())
                work.stage(batch, 0, {})
                val published = ledger.published().single()
                assertEquals(created.single().toString(), published.publication)
                assertEquals(payload.toList(), context.contentResolver.openInputStream(created.single())?.use { it.readBytes().toList() })
                work.stage(batch, 0, {})
                assertEquals(1, ledger.published().size)
            }
        } finally {
            created.forEach { context.contentResolver.delete(it, null, null) }
            context.deleteDatabase(name)
            source.delete()
        }
    }

    fun localRematchAfterNativeRestoreDoesNotRestage() {
        val id = UUID.randomUUID().toString()
        val name = "restore-$id.db"
        val source = File(context.cacheDir, "fixture-$id.zip")
        val photo = jpeg(id, Color.CYAN)
        val sha = digest(photo.inputStream()).sha
        writeZip(source, photo, jpeg(id, Color.RED))
        val resolver = context.contentResolver
        val rows = mutableSetOf<Uri>()
        try {
            Ledger(context, name).use { ledger ->
                val work = Work(context, ledger)
                val favorites = FavoriteReconciliation(context, ledger)
                ledger.addSource(Uri.fromFile(source).toString())
                work.ingest(0, {})
                val original = requireNotNull(ledger.item(sha))
                work.stage(ledger.createBatch(listOf(original)), 0, {})
                val prior = Uri.parse(ledger.published().single().publication).also(rows::add)
                // Simulate local loss of an app-owned synthetic row. This does not exercise Samsung Free Up.
                assertEquals(1, resolver.delete(prior, null, null))
                rows.remove(prior)
                work.audit {}
                assertEquals("LOCAL_ABSENT", ledger.item(sha)?.state)
                assertEquals(FavoriteState.NEEDS_NATIVE_RESTORE, favorites.preview {}.single().state)
                val restored = requireNotNull(resolver.insert(
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                    values(MediaStore.MediaColumns.DISPLAY_NAME to "restored-$id.jpg",
                        MediaStore.MediaColumns.MIME_TYPE to "image/jpeg",
                        MediaStore.MediaColumns.RELATIVE_PATH to "Pictures/GalleryBridgeFixture/",
                        MediaStore.MediaColumns.IS_PENDING to 1)
                )).also(rows::add)
                requireNotNull(resolver.openOutputStream(restored)).use { it.write(photo) }
                assertEquals(1, resolver.update(restored, values(MediaStore.MediaColumns.IS_PENDING to 0), null, null))
                assertEquals(1, work.scanLocal(listOf(restored)) {})
                val reviewed = favorites.preview {}.single()
                assertEquals(FavoriteState.READY, reviewed.state)
                assertEquals(restored.toString(), reviewed.change?.uri)
                assertTrue(ledger.plan(10, 1_000_000).none { it.sha == sha })
                assertEquals(1, ledger.published().size)
                assertFalse(ledger.summary().getBoolean("free_up_eligible"))
            }
        } finally {
            rows.forEach { resolver.delete(it, null, null) }
            context.deleteDatabase(name)
            source.delete()
        }
    }

    private fun platformFavorite(uri: Uri): Int? = context.contentResolver.query(uri,
        arrayOf(MediaStore.MediaColumns.IS_FAVORITE), null, null, null)?.use {
        if (it.moveToFirst() && !it.isNull(0)) it.getInt(0) else null
    }

    private fun acceptPlatformFavorite(uri: Uri) {
        val automation = instrumentation.uiAutomation
        repeat(60) {
            if (platformFavorite(uri) == 1) return
            val root = automation.rootInActiveWindow
            val owner = root?.packageName?.toString().orEmpty()
            if (owner.contains("providers.media")) {
                approve(root)
            }
            Thread.sleep(1_000)
        }
        error("Platform favorite consent did not update the fixture")
    }

    fun motionRecomposeReviewTrashAndRestore() {
        val db = "motion-${UUID.randomUUID()}.db"
        val zip = File(context.cacheDir, "$db.zip")
        instrumentation.context.assets.open("motion-test.zip").use { input ->
            zip.outputStream().use { input.copyTo(it) }
        }
        val created = mutableListOf<Uri>()
        var activity: MainActivity? = null
        var staged: File? = null
        try {
            Ledger(context, db).use { ledger ->
                ledger.addSource(Uri.fromFile(zip).toString())
                Work(context, ledger).ingest(0, {})
                val groups = ledger.motionGroups(24, 0)
                assertEquals(1, groups.size)
                val group = groups.single()
                assertEquals(2, group.size)
                val photo = requireNotNull(ledger.item(group.single { !it.video }.sha))
                val export = MotionExport(context, ledger)
                val prepared = export.stage(photo, 0, {})
                staged = prepared.file
                assertEquals(prepared.file, export.stage(photo, 0, {}).file)
                val native = MotionNative(context, ledger, export)
                val resolver = context.contentResolver
                val pictures = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                // Model death after the provider insert and before the app received its URI.
                val unreconciled = requireNotNull(resolver.insert(pictures, values(
                    MediaStore.MediaColumns.DISPLAY_NAME to "GB_Motion_${prepared.key}.jpg",
                    MediaStore.MediaColumns.MIME_TYPE to "image/jpeg",
                    MediaStore.MediaColumns.RELATIVE_PATH to "Pictures/GalleryBridge_Motion/",
                    MediaStore.MediaColumns.IS_PENDING to 1
                ))).also(created::add)
                val winner = native.publish(photo, 0, {})
                assertEquals(unreconciled, winner)
                assertEquals(winner, native.publish(photo, 0, {}))
                val date = resolver.query(winner, arrayOf(MediaStore.MediaColumns.DATE_TAKEN), null, null, null)
                    ?.use { if (it.moveToFirst() && !it.isNull(0)) it.getLong(0) else null }
                val still = requireNotNull(resolver.insert(pictures, values(
                    MediaStore.MediaColumns.DISPLAY_NAME to "$db.jpg", MediaStore.MediaColumns.MIME_TYPE to "image/jpeg",
                    MediaStore.MediaColumns.RELATIVE_PATH to "Pictures/GalleryBridge_Fixture/",
                    MediaStore.MediaColumns.DATE_TAKEN to date, MediaStore.MediaColumns.IS_PENDING to 1
                ))).also(created::add)
                java.util.zip.ZipFile(zip).use { source ->
                    source.getInputStream(source.getEntry("Takeout/fixture/still.jpg")).use { input ->
                        requireNotNull(resolver.openOutputStream(still)).use { input.copyTo(it) }
                    }
                }
                resolver.update(still, values(MediaStore.MediaColumns.IS_PENDING to 0), null, null)
                val plan = native.compare(winner, still, {})
                assertEquals(MotionDecision.REVIEW_STATIC, plan.decision)
                assertTrue(plan.eligible)
                assertEquals(null, ledger.motionOperation())
                val incomplete = MotionAttestation(true, true, false)
                assertTrue(runCatching { native.requestTrash(plan, incomplete, 0, {}) }.isFailure)
                assertEquals(null, ledger.motionOperation())
                val consent = MotionAttestation(true, true, true)
                val move = native.moveForReview(plan, consent, 0, {})
                assertEquals("MOVED_LOCAL", move.state)
                assertTrue(runCatching { native.requestTrash(plan, consent, 0, {}) }.isFailure)
                assertEquals("RESTORED_PATH", native.undoMove {}.state)
                val fresh = native.compare(winner, still, {})
                assertEquals("Pictures/GalleryBridge_Fixture/", fresh.candidate.path)
                native.requestTrash(fresh, consent, 0, {})
                assertEquals("CANCELLED", native.finish(false, {})?.state)
                assertEquals(0, trashFlag(still))
                activity = instrumentation.startActivitySync(android.content.Intent(context, MainActivity::class.java)
                    .addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)) as MainActivity
                val active = requireNotNull(activity)
                val trash = native.requestTrash(fresh, consent, 0, {})
                instrumentation.runOnMainSync { active.startIntentSenderForResult(trash.intentSender, 91, null, 0, 0, 0) }
                waitForTrashFlag(still, 1)
                assertEquals("REMOVAL_OBSERVED", native.finish(true, {})?.state)
                assertEquals(0, trashFlag(winner))
                val restore = native.requestRestore {}
                instrumentation.runOnMainSync { active.startIntentSenderForResult(restore.intentSender, 92, null, 0, 0, 0) }
                waitForTrashFlag(still, 0)
                assertEquals("RESTORED_LOCAL", native.finish(true, {})?.state)
                assertEquals(photo.sha, native.compare(winner, still, {}).candidate.sha)
            }
        } finally {
            activity?.let { instrumentation.runOnMainSync(it::finish) }
            created.forEach { context.contentResolver.delete(it, null, null) }
            staged?.delete()
            zip.delete()
            context.deleteDatabase(db)
        }
    }

    @android.annotation.TargetApi(30)
    private fun trashFlag(uri: Uri): Int? {
        val args = android.os.Bundle().apply {
            putInt(MediaStore.QUERY_ARG_MATCH_TRASHED, MediaStore.MATCH_INCLUDE)
        }
        return context.contentResolver.query(uri, arrayOf(MediaStore.MediaColumns.IS_TRASHED), args, null)
            ?.use { if (it.moveToFirst()) it.getInt(0) else null }
    }

    private fun waitForTrashFlag(uri: Uri, expected: Int) {
        val automation = instrumentation.uiAutomation
        automation.serviceInfo = automation.serviceInfo.apply {
            flags = flags or android.accessibilityservice.AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                android.accessibilityservice.AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
        }
        val observed = linkedSetOf<String>()
        val deadline = android.os.SystemClock.elapsedRealtime() + 50_000
        while (android.os.SystemClock.elapsedRealtime() < deadline) {
            if (trashFlag(uri) == expected) return
            val roots = listOfNotNull(automation.rootInActiveWindow) + automation.windows.mapNotNull { it.root }
            for (root in roots) {
                val owner = root.packageName?.toString()
                observed.add(owner.orEmpty())
                if (owner?.contains("providers.media") == true) {
                    describeButtons(root, observed)
                    if (approve(root)) break
                }
            }
            Thread.sleep(500)
        }
        error("Expected trash flag $expected; actual ${trashFlag(uri)}; windows and buttons: $observed")
    }

    private fun describeButtons(node: android.view.accessibility.AccessibilityNodeInfo?, labels: MutableSet<String>) {
        if (node == null) return
        if (node.isClickable) labels.add("${node.viewIdResourceName} ${node.text}")
        repeat(node.childCount) { describeButtons(node.getChild(it), labels) }
    }

    private fun approve(node: android.view.accessibility.AccessibilityNodeInfo?): Boolean {
        if (node == null) return false
        val label = node.text?.toString()?.trim()?.lowercase(java.util.Locale.US)
        if (node.isClickable && (node.viewIdResourceName == "android:id/button1" ||
                label in setOf("allow", "favorite", "add to favorites", "move to trash", "move to bin", "trash", "restore", "ok"))) {
            return node.performAction(android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK)
        }
        return (0 until node.childCount).any { approve(node.getChild(it)) }
    }

    private fun simulateCrashBeforeUriReceipt(context: Context, ledger: Ledger, batch: Long): Uri? {
        val item = ledger.pending(batch).first()
        return context.contentResolver.insert(MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
            values(MediaStore.MediaColumns.DISPLAY_NAME to token(item), MediaStore.MediaColumns.MIME_TYPE to item.mime,
                MediaStore.MediaColumns.RELATIVE_PATH to "Pictures/${item.album}/", MediaStore.MediaColumns.IS_PENDING to 1))
            ?.also { uri -> context.contentResolver.openOutputStream(uri)?.use { it.write(byteArrayOf(0, 1)) } }
    }

    private fun jpeg(id: String, color: Int): ByteArray {
        val image = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888)
        image.eraseColor(color)
        val bytes = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        image.recycle()
        return bytes.toByteArray() + id.toByteArray()
    }

    private fun writeZip(file: File, first: ByteArray, second: ByteArray) {
        ZipOutputStream(file.outputStream()).use { zip ->
            listOf(
                "Year/a.jpg" to first,
                "Year/a.jpg.supplemental-metadata.json" to """{"title":"a.jpg","photoTakenTime":{"timestamp":"1767225600"},"favorited":true,"url":"https://photos.google.com/photo/SyntheticPhotoKey_123?authuser=3&ignored=yes"}""".toByteArray(),
                "Album/copy.jpg" to first,
                "Album/a.jpg" to second,
                "Private/.nomedia" to byteArrayOf(),
                "Private/held.jpg" to jpeg(file.name, Color.YELLOW)
            ).forEach { (path, bytes) ->
                zip.putNextEntry(ZipEntry(path))
                zip.write(bytes)
                zip.closeEntry()
            }
        }
    }
}

private fun assertEquals(expected: Any?, actual: Any?) {
    check(expected == actual) { "Expected $expected, got $actual" }
}
private fun assertTrue(value: Boolean) { check(value) }
private fun assertFalse(value: Boolean) { check(!value) }
private fun assertNotNull(value: Any?) { check(value != null) }
