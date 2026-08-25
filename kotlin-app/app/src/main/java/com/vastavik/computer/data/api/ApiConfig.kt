package com.vastavik.computer.data.api

import com.vastavik.computer.BuildConfig

object ApiConfig {
    val BASE_URL: String = BuildConfig.BACKEND_BASE_URL.trimEnd('/') + "/"
    const val CONNECT_TIMEOUT_SEC = 15L
    const val READ_TIMEOUT_SEC = 20L
    const val WRITE_TIMEOUT_SEC = 20L

    val API_KEY_ID: String = BuildConfig.API_KEY_ID
    val API_KEY_SECRET: String = BuildConfig.API_KEY_SECRET

    // Keep Firestore fallback toggle — when backend unreachable, UI can fallback to direct Firestore
    const val USE_BACKEND_PRIMARY = true
}
