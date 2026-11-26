package dev.gallerybridge

import android.app.PendingIntent
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

internal data class FavoriteChange(val sha: String, val uri: String, val desired: Boolean)
internal data class FavoriteReview(
    val item: Item, val state: FavoriteState, val change: FavoriteChange? = null
)

internal class FavoriteReconciliation(private val context: Context, private val ledger: Ledger) {
    private val resolver = context.contentResolver

    // Preview reads only. A provider grant, exact bytes and a unique row are required.
    fun preview(checkpoint: () -> Unit): List<FavoriteReview> =
        ledger.favoriteSources(checkpoint).mapNotNull { (sha, source) ->
            if (source == HistoricalFavorite.EXPLICITLY_NOT_FAVORITED) return@mapNotNull null
            val item = ledger.item(sha) ?: return@mapNotNull null
            if (source == HistoricalFavorite.CONFLICT) FavoriteReview(item, FavoriteState.SOURCE_CONFLICT)
            else inspect(item, true, checkpoint)
        }

    fun inspect(item: Item, desired: Boolean, checkpoint: () -> Unit): FavoriteReview {
        checkpoint()
        if (ledger.restricted(item.sha) || ledger.excluded(item.sha)) {
            return FavoriteReview(item, FavoriteState.ACCESS_REQUIRED)
        }
        val targets = favoriteTargets(item, ledger.localTargets(item.sha))
        val target = targets.singleOrNull() ?: return FavoriteReview(item, when {
            targets.size > 1 -> FavoriteState.AMBIGUOUS_DESTINATION
            item.state == "LOCAL_ABSENT" || item.state == "E1" -> FavoriteState.NEEDS_NATIVE_RESTORE
            else -> FavoriteState.NOT_STAGED
        })
        val change = FavoriteChange(item.sha, target, desired)
        val observed = observe(item, Uri.parse(target), checkpoint)
        val prior = ledger.favoriteStatus(item.sha)
        val state = when {
            observed == null && item.state == "E1" && target == item.publication -> FavoriteState.NEEDS_NATIVE_RESTORE
            observed == null -> FavoriteState.ACCESS_REQUIRED
            observed == desired && prior == FavoriteState.LOCAL_VERIFIED -> FavoriteState.LOCAL_VERIFIED
            observed == desired -> FavoriteState.ALREADY_LOCAL
            else -> FavoriteState.READY
        }
        return FavoriteReview(item, state, change.takeIf { state == FavoriteState.READY })
    }

    fun currentFlag(item: Item, checkpoint: () -> Unit): Boolean? {
        if (ledger.restricted(item.sha) || ledger.excluded(item.sha)) return null
        val target = favoriteTargets(item, ledger.localTargets(item.sha)).singleOrNull() ?: return null
        return observe(item, Uri.parse(target), checkpoint)
    }

    fun prepare(changes: List<FavoriteChange>, checkpoint: () -> Unit): PendingIntent {
        demand(Build.VERSION.SDK_INT >= 30, Reason.DESTINATION_REVIEW)
        demand(changes.size in 1..100 && changes.distinctBy { it.uri }.size == changes.size &&
            changes.map { it.desired }.distinct().size == 1, Reason.DESTINATION_REVIEW)
        for (change in changes) {
            val item = ledger.item(change.sha) ?: throw BridgeFailure(Reason.SOURCE_CHANGED)
            demand(inspect(item, change.desired, checkpoint).change == change, Reason.DESTINATION_REVIEW)
        }
        val request = request(changes.map { Uri.parse(it.uri) }, changes.first().desired)
        ledger.favoriteRequests(changes)
        return request
    }

    @android.annotation.TargetApi(30)
    private fun request(uris: List<Uri>, desired: Boolean): PendingIntent =
        MediaStore.createFavoriteRequest(resolver, uris, desired)

    fun finish(approved: Boolean?, checkpoint: () -> Unit): Int {
        var verified = 0
        for (change in ledger.pendingFavorites()) {
            val item = ledger.item(change.sha)
            val observed = item?.let { observe(it, Uri.parse(change.uri), checkpoint) }
            val state = when {
                observed == change.desired && approved == true -> FavoriteState.LOCAL_VERIFIED
                observed == change.desired -> FavoriteState.ALREADY_LOCAL
                observed == null -> FavoriteState.ACCESS_REQUIRED
                else -> FavoriteState.NOT_APPLIED
            }
            ledger.favoriteReceipt(change, state, approved == true)
            if (state == FavoriteState.LOCAL_VERIFIED || state == FavoriteState.ALREADY_LOCAL) verified++
        }
        return verified
    }

    @Suppress("SwallowedException")
    private fun observe(item: Item, uri: Uri, checkpoint: () -> Unit): Boolean? {
        if (Build.VERSION.SDK_INT < 30 || !isMediaItem(uri)) return null
        val bound = maxOf(item.size, item.outputSize ?: 0)
        return try {
            val flag = resolver.query(uri, arrayOf(MediaStore.MediaColumns.IS_FAVORITE,
                MediaStore.MediaColumns.IS_PENDING, MediaStore.MediaColumns.IS_TRASHED), null, null, null)?.use {
                if (!it.moveToFirst() || it.isNull(0) || it.getInt(1) != 0 || it.getInt(2) != 0) return null
                it.getInt(0) == 1
            } ?: return null
            val observed = resolver.openInputStream(uri)?.use { digest(it, maxBytes = bound, checkpoint = checkpoint) }
            val original = observed?.sha == item.sha && observed.bytes == item.size
            val derivative = item.outputSha != null && observed?.sha == item.outputSha && observed.bytes == item.outputSize
            flag.takeIf { original || derivative }
        } catch (failure: BridgeFailure) {
            if (failure.reason != Reason.INTEGRITY) throw failure
            null
        } catch (_: java.io.IOException) {
            null
        } catch (_: SecurityException) {
            null
        } catch (_: IllegalArgumentException) {
            null
        }
    }
}

internal fun isMediaItem(uri: Uri): Boolean =
    uri.scheme == "content" && uri.authority == MediaStore.AUTHORITY && uri.pathSegments.size == 4 &&
        uri.pathSegments[1] in listOf("images", "video") && uri.pathSegments[2] == "media" &&
        (uri.lastPathSegment?.toLongOrNull() ?: 0) > 0

internal fun favoriteTargets(item: Item, scanned: List<String>): Set<String> = buildSet {
    if (item.state == "E1") item.publication?.let(::add)
    addAll(scanned)
}
