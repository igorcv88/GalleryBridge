package dev.gallerybridge

import java.io.DataOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.io.SequenceInputStream

private const val XMP_PREFIX = "http://ns.adobe.com/xap/1.0/\u0000"

fun writeJpegMotionPhoto(
    photo: InputStream, photoIdentity: Digest, video: InputStream, videoIdentity: Digest,
    videoMime: String, output: OutputStream, checkpoint: () -> Unit = {}
) {
    demand(videoMime in setOf("video/mp4", "video/quicktime") && videoIdentity.bytes >= 16, Reason.INTEGRITY)
    demand(photoIdentity.bytes >= 4, Reason.INTEGRITY)
    val xmp = (XMP_PREFIX + """
        <x:xmpmeta xmlns:x="adobe:ns:meta/"><rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
        <rdf:Description xmlns:Camera="http://ns.google.com/photos/1.0/camera/"
         xmlns:Container="http://ns.google.com/photos/1.0/container/"
         xmlns:Item="http://ns.google.com/photos/1.0/container/item/"
         Camera:MotionPhoto="1" Camera:MotionPhotoVersion="1">
        <Container:Directory><rdf:Seq>
        <rdf:li rdf:parseType="Resource"><Container:Item Item:Mime="image/jpeg" Item:Semantic="Primary"/></rdf:li>
        <rdf:li rdf:parseType="Resource"><Container:Item Item:Mime="$videoMime" Item:Semantic="MotionPhoto"
         Item:Length="${videoIdentity.bytes}"/></rdf:li>
        </rdf:Seq></Container:Directory></rdf:Description></rdf:RDF></x:xmpmeta>
    """.trimIndent()).toByteArray(Charsets.UTF_8)
    val stream = DataOutputStream(output)
    demand(photo.read() == 0xff && photo.read() == 0xd8, Reason.INTEGRITY)
    stream.writeShort(0xffd8)
    stream.writeShort(0xffe1)
    stream.writeShort(xmp.size + 2)
    stream.write(xmp)
    val start = byteArrayOf(0xff.toByte(), 0xd8.toByte())
    var skip = start.size
    val body = object : OutputStream() {
        override fun write(b: Int) {
            if (skip > 0) skip-- else stream.write(b)
        }

        override fun write(b: ByteArray, off: Int, len: Int) {
            val ignored = minOf(skip, len)
            skip -= ignored
            stream.write(b, off + ignored, len - ignored)
        }
    }
    val still = digest(SequenceInputStream(start.inputStream(), photo), body, photoIdentity.bytes, checkpoint)
    demand(still == photoIdentity, Reason.INTEGRITY)
    val motion = digest(video, stream, videoIdentity.bytes, checkpoint)
    demand(motion == videoIdentity, Reason.INTEGRITY)
}
