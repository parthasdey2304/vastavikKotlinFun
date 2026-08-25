package com.vastavik.computer.data.api

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.util.UUID

/**
 * Adds Vastavik backend auth headers to every backend request:
 * - Authorization: Bearer Firebase ID token
 * - x-api-key: keyId.hmac_base64url  where hmac = HMAC(secret, ts.keyId.nonce)
 * - x-api-timestamp: epoch seconds
 * - x-request-id: uuid nonce (also used in HMAC)
 *
 * Backend verifies with timingSafeEqual; timestamp window 5 min.
 * For dev fallback we also support plain legacy check, but we always send HMAC.
 */
class AuthInterceptor(
    private val apiKeyId: String = ApiConfig.API_KEY_ID,
    private val apiSecret: String = ApiConfig.API_KEY_SECRET
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()

        // Only sign api/admin paths; leave /health etc untouched if needed — but signing all is harmless
        val nonce = original.header("x-request-id") ?: UUID.randomUUID().toString()
        val ts = (System.currentTimeMillis() / 1000).toString()
        val payload = "$ts.$apiKeyId.$nonce"
        val hmac = HmacUtil.hmacBase64Url(apiSecret, payload)

        builder.header("x-request-id", nonce)
        builder.header("x-api-timestamp", ts)
        builder.header("x-api-key", "$apiKeyId.$hmac")

        // Firebase ID token — synchronous fetch via runBlocking (fast if cached)
        val token: String? = try {
            runBlocking {
                FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.await()?.token
            }
        } catch (_: Exception) { null }

        if (token != null) {
            builder.header("Authorization", "Bearer $token")
        }

        builder.header("Accept", "application/json")
        return chain.proceed(builder.build())
    }
}
