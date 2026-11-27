package dev.gallerybridge

import android.content.Context
import android.net.Uri
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import org.json.JSONException

internal class Work(private val context: Context, val ledger: Ledger) {
    private val documents = Documents(context)
    private val publisher = MediaPublisher(context, ledger)

    @Suppress("SwallowedException") // Provider errors can include private paths and URLs.
    fun ingest(reserve: Long, check: () -> Unit, progress: (String) -> Unit = {}) {
        for (source in ledger.sources().filter { it.state != "VERIFIED" }) {
            check()
            try {
                documents.seekable(Uri.parse(source.uri), check, reserve) { channel ->
                    val fingerprint = fingerprint(channel, check)
                    ledger.prepareSource(source, fingerprint)
                    channel.position(0)
                    Archive(channel).use { zip ->
                        for (index in ledger.nextOrdinal(source.id) until zip.count) {
                            check()
                            val member = zip.member(index)
                            val result = zip.verify(member, checkpoint = check)
                            val sidecar = readSidecar(zip, member)
                            val metadata = if (member.restricted) null else captureMetadata(zip, member, check)
                            ledger.addEntry(source.id, member, result.sha, sidecar, metadata?.toString(),
                                metadata?.optString("live_id")?.let(::normalizeLiveId), metadata?.optInt("format_verified") ?: 0)
                            if (index % 50 == 0) progress("Parte ${source.id} · ${index + 1}/${zip.count} entradas")
                        }
                        // Archive closes the channel. Hash again before its entries become eligible.
                        val finalFingerprint = fingerprint(channel, check)
                        demand(fingerprint == finalFingerprint, Reason.SOURCE_CHANGED)
                        ledger.sourceState(source.id, "VERIFIED")
                    }
                }
            } catch (failure: SecurityException) {
                ledger.sourceState(source.id, "NEEDS_USER_ACTION")
            } catch (failure: BridgeFailure) {
                ledger.sourceState(source.id, failure.reason.name)
                if (failure.reason in stoppages) throw failure
            } catch (_: java.io.IOException) {
                ledger.sourceState(source.id, "SOURCE_INCOMPLETE")
            }
        }
    }

    private fun readSidecar(zip: Archive, m: Member): String? {
        if (m.restricted || !m.sidecar || m.size !in 1..262_144) return null
        return try {
            zip.open(m.ordinal).bufferedReader(Charsets.UTF_8).use { reader ->
                val buffer = CharArray(m.size.toInt())
                var offset = 0
                while (offset < buffer.size) {
                    val count = reader.read(buffer, offset, buffer.size - offset)
                    if (count < 0) break
                    demand(count > 0, Reason.INTEGRITY)
                    offset += count
                }
                demand(reader.read() == -1, Reason.INTEGRITY)
                val raw = String(buffer, 0, offset)
                val json = parseSidecar(raw) ?: return null
                if (json.title != null && json.title != sidecarBase(m.name)) return null
                raw
            }
        } catch (_: JSONException) {
            null
        }
    }

    fun stage(batch: Long, reserve: Long, check: () -> Unit, progress: (String) -> Unit = {}) {
        for ((source, items) in ledger.pending(batch).groupBy { it.source }) {
            documents.seekable(Uri.parse(source.uri), check, reserve) { channel ->
                Archive(channel).use { zip ->
                    for (item in items) {
                        check()
                        // The conservative budget includes this item even when a partial row exists.
                        demand(item.size <= context.filesDir.usableSpace - reserve, Reason.SPACE)
                        demand(!ledger.restricted(item.sha) && !ledger.excluded(item.sha), Reason.DESTINATION_REVIEW)
                        publisher.publish(item, zip, reserve, check)
                        progress("Lote $batch · cópia verificada e indexada")
                    }
                }
            }
        }
        ledger.finished(batch)
    }

    fun scanLocal(uris: List<Uri>, check: () -> Unit): Int {
        var matches = 0
        for (uri in uris) {
            val identity = documents.open(uri).use { digest(it, checkpoint = check) }
            val original = ledger.originalIdentity(identity)
            if (original != null && !ledger.restricted(original.sha) && !ledger.excluded(original.sha)) {
                ledger.localMatch(original.sha)
                val media = if (isMediaItem(uri)) uri else if (android.os.Build.VERSION.SDK_INT >= 29) {
                    runCatching { android.provider.MediaStore.getMediaUri(context, uri) }.getOrNull()
                } else null
                if (media != null && isMediaItem(media)) ledger.localTarget(original.sha, media.toString())
                matches++
            }
        }
        return matches
    }

    fun audit(check: () -> Unit) = ledger.published().forEach { publisher.audit(it, check) }

    private fun fingerprint(channel: FileChannel, check: () -> Unit): Digest {
        channel.position(0)
        return digest(Channels.newInputStream(channel), maxBytes = channel.size(), checkpoint = check)
            .also { demand(it.bytes == channel.size(), Reason.SOURCE_CHANGED) }
    }

    companion object {
        private val stoppages = setOf(Reason.WIFI_REQUIRED, Reason.BATTERY, Reason.THERMAL, Reason.PAUSED, Reason.SPACE)
    }
}
