package nz.oldfolk.data

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.EOFException
import java.io.File
import java.io.FileOutputStream

/**
 * Keys of tracks already played on this phone, so the radio never repeats a
 * track until the selection is exhausted. Stored as an append-only file of
 * 64-bit keys (8 bytes per track).
 */
class PlayHistory(private val file: File) {
    private val played = HashSet<Long>()

    init {
        if (file.exists()) {
            try {
                DataInputStream(file.inputStream().buffered()).use { input ->
                    while (true) played += input.readLong()
                }
            } catch (_: EOFException) {
            } catch (_: Exception) {
                // A torn last write only loses that one entry.
            }
        }
    }

    @Synchronized
    fun contains(key: Long): Boolean = key in played

    @Synchronized
    fun size(): Int = played.size

    @Synchronized
    fun add(key: Long) {
        if (played.add(key)) {
            DataOutputStream(FileOutputStream(file, true)).use { it.writeLong(key) }
        }
    }

    /** Forget the given keys (used when a selection has been fully played). */
    @Synchronized
    fun forget(keys: Collection<Long>) {
        if (played.removeAll(keys.toSet())) rewrite()
    }

    @Synchronized
    fun clear() {
        played.clear()
        rewrite()
    }

    private fun rewrite() {
        val tmp = File(file.parentFile, file.name + ".tmp")
        DataOutputStream(tmp.outputStream().buffered()).use { out -> played.forEach { out.writeLong(it) } }
        tmp.renameTo(file)
    }
}
