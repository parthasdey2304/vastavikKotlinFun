package com.vastavik.computer.data.api

import android.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HmacUtil {
    /**
     * Mirror of backend requireApiKey HMAC:
     * payload = "$timestamp.$keyId.$nonce"  (nonce = x-request-id)
     * hmac = HMAC-SHA256(secret, payload)  -> base64url (no padding)
     */
    fun hmacBase64Url(secret: String, payload: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(secret.toByteArray(Charsets.UTF_8), "HmacSHA256"))
        val raw = mac.doFinal(payload.toByteArray(Charsets.UTF_8))
        // base64url without padding, matches Node's digest("base64url")
        return Base64.encodeToString(raw, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
    }

    /** Extract 11-char YouTube videoId — same regex as backend */
    private val ytRegex = Regex("""(?:youtube\.com/watch\?v=|youtu\.be/|youtube\.com/embed/)([A-Za-z0-9_-]{11})""")
    fun extractVideoId(url: String): String? = ytRegex.find(url)?.groupValues?.get(1)

    fun isYouTubeUrl(url: String): Boolean = extractVideoId(url) != null
}
