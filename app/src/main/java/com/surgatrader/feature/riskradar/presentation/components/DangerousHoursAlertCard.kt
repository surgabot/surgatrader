package com.surgatrader.feature.riskradar.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.surgatrader.core.theme.DangerRuby
import com.surgatrader.core.theme.SafeEmerald
import com.surgatrader.core.theme.SlateBorder
import com.surgatrader.core.theme.SlateCard
import com.surgatrader.core.theme.TextPrimary
import com.surgatrader.core.theme.TextSecondary
import com.surgatrader.core.theme.WarnAmber
import com.surgatrader.core.util.DateTimeUtils
import com.surgatrader.feature.riskradar.domain.model.RiskAssessment

@Composable
fun DangerousHoursAlertCard(
    assessment: RiskAssessment,
    modifier: Modifier = Modifier
) {
    val activePeriod = assessment.activeDangerousPeriod
    val dailyWarning = assessment.dailyLossWarning

    Column(modifier = modifier.fillMaxWidth()) {
        // 1. Daily Loss Limit Alert Banner (jika kena batas)
        if (dailyWarning != null) {
            val isBreached = assessment.isDailyLossLimitBreached
            val bannerColor = if (isBreached) DangerRuby else WarnAmber

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bannerColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .border(1.dp, bannerColor, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = bannerColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = dailyWarning,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // 2. Kartu Jam Rawan Trading Emas (WIB)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SlateCard, RoundedCornerShape(14.dp))
                .border(1.dp, SlateBorder, RoundedCornerShape(14.dp))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (activePeriod != null) Icons.Default.Warning else Icons.Outlined.AccessTime,
                    contentDescription = "Clock",
                    tint = if (activePeriod != null) WarnAmber else SafeEmerald,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (activePeriod != null) {
                                "⚠️ SEDANG BERLANGSUNG: ${activePeriod.title}"
                            } else {
                                "🟢 Jam Trading Emas Normal (WIB)"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (activePeriod != null) WarnAmber else TextPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (activePeriod != null) {
                            "${String.format("%02d:%02d", activePeriod.startHourWib, activePeriod.startMinuteWib)} - ${String.format("%02d:%02d", activePeriod.endHourWib, activePeriod.endMinuteWib)} WIB: ${activePeriod.description}"
                        } else {
                            "Waspada jam rawan berikutnya: London Open (14:00-16:00 WIB) & US News/NY Open (19:15-21:30 WIB)"
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    )
                }
            }
        }
    }
}
