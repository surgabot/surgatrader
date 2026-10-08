package com.surgatrader.core.security

import com.surgatrader.BuildConfig
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor

object SecurityNetworkInterceptor {

    /**
     * Membuat HttpLoggingInterceptor yang HANYA aktif di DEBUG build
     * dan menyamarkan header Authorization serta sesi MCP secara ketat.
     */
    fun createLoggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.HEADERS
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
            redactHeader("Authorization")
            redactHeader("Mcp-Session-Id")
            redactHeader("x-session-id")
        }
    }
}
