package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.ui.theme.*

@Composable
fun ZukiTopBar(
    titleBn: String,
    titleEn: String,
    subtitleBn: String,
    subtitleEn: String,
    streakDays: Int,
    language: AppLanguage,
    onToggleLanguage: () -> Unit,
    onOpenAiCoach: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = DeepForest,
        tonalElevation = 6.dp,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, ForestBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Title & Subtitle
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (language == AppLanguage.BN) titleBn else titleEn,
                        color = Color.White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.3).sp,
                        modifier = Modifier.testTag("top_bar_title")
                    )

                    // Streak Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = ForestCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, ForestBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Streak",
                                tint = StreakFire,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "$streakDays ${if (language == AppLanguage.BN) "দিন" else "d"}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonLime
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (language == AppLanguage.BN) subtitleBn else subtitleEn,
                    color = MintSoft.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            // Right Action Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Language Switcher Pill
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = ForestCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ForestBorder),
                    modifier = Modifier
                        .clickable { onToggleLanguage() }
                        .testTag("language_switch_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Language Switcher",
                            tint = Emerald,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = if (language == AppLanguage.BN) "বাংলা" else "ENG",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // AI Coach Avatar button with glowing aura
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(ForestCard)
                        .border(1.5.dp, Emerald, CircleShape)
                        .clickable { onOpenAiCoach() }
                        .testTag("ai_coach_avatar_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Coach",
                        tint = NeonLime,
                        modifier = Modifier.size(20.dp)
                    )
                    // Active online pulse dot
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .align(Alignment.TopEnd)
                            .clip(CircleShape)
                            .background(Emerald)
                            .border(1.5.dp, DeepForest, CircleShape)
                    )
                }
            }
        }
    }
}
