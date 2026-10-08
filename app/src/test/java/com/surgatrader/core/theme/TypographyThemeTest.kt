package com.surgatrader.core.theme

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TypographyThemeTest {

    @Test
    fun `typography font families match Aura Quantum design specification`() {
        assertThat(Typography.displayLarge.fontFamily).isEqualTo(FontFamilyOrbitron)
        assertThat(Typography.bodyLarge.fontFamily).isEqualTo(FontFamilyRajdhani)
        assertThat(Typography.labelLarge.fontFamily).isEqualTo(FontFamilyShareTechMono)
    }

    @Test
    fun `color palette maintains cyber gold and dark space values`() {
        assertThat(AuraBgDark.value).isNotEqualTo(0UL)
        assertThat(AuraGoldPrimary.value).isNotEqualTo(0UL)
        assertThat(AuraCyan.value).isNotEqualTo(0UL)
        assertThat(AuraGreenBull.value).isNotEqualTo(0UL)
        assertThat(AuraRedBear.value).isNotEqualTo(0UL)
    }
}
