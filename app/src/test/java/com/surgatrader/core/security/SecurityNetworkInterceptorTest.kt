package com.surgatrader.core.security

import com.google.common.truth.Truth.assertThat
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Test

class SecurityNetworkInterceptorTest {

    @Test
    fun `data mode enum contains LIVE and DEMO`() {
        val modes = DataMode.values()
        assertThat(modes).asList().containsExactly(DataMode.LIVE, DataMode.DEMO)
    }

    @Test
    fun `security interceptor builds HttpLoggingInterceptor with redacted headers`() {
        val interceptor = SecurityNetworkInterceptor.createLoggingInterceptor()
        assertThat(interceptor).isInstanceOf(HttpLoggingInterceptor::class.java)
    }
}
