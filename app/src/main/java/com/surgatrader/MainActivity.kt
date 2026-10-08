package com.surgatrader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.surgatrader.core.theme.AuraBgDark
import com.surgatrader.core.theme.SurgaTraderTheme
import com.surgatrader.feature.aura.presentation.AuraQuantumScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SurgaTraderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AuraBgDark
                ) {
                    AuraQuantumScreen()
                }
            }
        }
    }
}
