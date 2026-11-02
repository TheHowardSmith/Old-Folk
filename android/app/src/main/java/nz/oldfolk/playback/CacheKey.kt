package nz.oldfolk.playback

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * The key that encrypts the playback cache.
 *
 * A random 128-bit key, stored only wrapped (AES-GCM) by a key that lives in
 * the Android Keystore and never leaves it. So the cached audio is unreadable
 * to anything but this app on this phone, even if the files are copied off.
 * If the key can't be unwrapped (Keystore reset, restored to a new phone), a
 * new one is made and [fresh] tells the cache to start empty.
 */
class CacheKey private constructor(val key: ByteArray, val fresh: Boolean) {
    companion object {
        private const val TAG = "CacheKey"
        private const val ALIAS = "oldfolk-cache-wrap"
        private const val PREF = "cache_key"

        fun load(context: Context): CacheKey {
            val prefs = context.getSharedPreferences("cache_key", Context.MODE_PRIVATE)
            prefs.getString(PREF, null)?.let { stored ->
                try {
                    return CacheKey(unwrap(Base64.decode(stored, Base64.NO_WRAP)), fresh = false)
                } catch (e: Exception) {
                    Log.w(TAG, "cache key unreadable, starting a new cache: ${e.message}")
                }
            }
            val key = ByteArray(16).also { SecureRandom().nextBytes(it) }
            try {
                prefs.edit().putString(PREF, Base64.encodeToString(wrap(key), Base64.NO_WRAP)).apply()
            } catch (e: Exception) {
                // No usable Keystore: the cache still works, with a key that lasts this session.
                Log.w(TAG, "Keystore unavailable: ${e.message}")
            }
            return CacheKey(key, fresh = true)
        }

        private fun wrappingKey(): SecretKey {
            val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
            (ks.getKey(ALIAS, null) as? SecretKey)?.let { return it }
            val gen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            gen.init(KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build())
            return gen.generateKey()
        }

        private fun wrap(key: ByteArray): ByteArray {
            val c = Cipher.getInstance("AES/GCM/NoPadding")
            c.init(Cipher.ENCRYPT_MODE, wrappingKey())
            return c.iv + c.doFinal(key)       // 12-byte IV, then ciphertext + tag
        }

        private fun unwrap(blob: ByteArray): ByteArray {
            val c = Cipher.getInstance("AES/GCM/NoPadding")
            c.init(Cipher.DECRYPT_MODE, wrappingKey(), GCMParameterSpec(128, blob, 0, 12))
            return c.doFinal(blob, 12, blob.size - 12)
        }
    }
}
