package com.surgatrader.feature.aura.domain.model

import com.google.common.truth.Truth.assertThat
import com.surgatrader.core.security.DataMode
import org.junit.Test

class AuraStateSecurityTest {

    @Test
    fun `default AuraState is in DEMO mode`() {
        val state = AuraState()
        assertThat(state.dataMode).isEqualTo(DataMode.DEMO)
        assertThat(state.isDemoMode).isTrue()
    }

    @Test
    fun `AuraState when switched to LIVE mode updates isDemoMode flag`() {
        val state = AuraState(dataMode = DataMode.LIVE)
        assertThat(state.dataMode).isEqualTo(DataMode.LIVE)
        assertThat(state.isDemoMode).isFalse()
    }

    @Test
    fun `AuraState contains no hardcoded account login or private server`() {
        val state = AuraState()
        assertThat(state.mt5AccountLogin).isEqualTo(0L)
        assertThat(state.mt5Server).isEqualTo("Offline")
        assertThat(state.isMt5Connected).isFalse()
    }
}
