package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.NavigationTab
import com.example.ui.theme.*

private data class TabItem(
    val tab: NavigationTab,
    val titleBn: String,
    val titleEn: String,
    val icon: ImageVector,
    val tag: String
)

@Composable
fun ZukiBottomBar(
    currentTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        TabItem(NavigationTab.HOME, "হোম", "Home", Icons.Default.Home, "bottom_tab_home"),
        TabItem(NavigationTab.FOOD, "খাবার", "Food", Icons.Default.Restaurant, "bottom_tab_food"),
        TabItem(NavigationTab.WORKOUT, "ব্যায়াম", "Workout", Icons.Default.FitnessCenter, "bottom_tab_workout"),
        TabItem(NavigationTab.PROGRESS, "প্রগতি", "Progress", Icons.AutoMirrored.Filled.TrendingUp, "bottom_tab_progress"),
        TabItem(NavigationTab.PROFILE, "প্রোফাইল", "Profile", Icons.Default.Person, "bottom_tab_profile")
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = DeepForest,
        tonalElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ForestBorder),
        shadowElevation = 16.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 6.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { item ->
                val isSelected = currentTab == item.tab
                val iconTint by animateColorAsState(
                    targetValue = if (isSelected) Emerald else TextTertiary,
                    animationSpec = tween(durationMillis = 180),
                    label = "iconTint"
                )
                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) ForestCard else Color.Transparent,
                    animationSpec = tween(durationMillis = 180),
                    label = "bgColor"
                )

                Column(
                    modifier = Modifier
                        .height(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(bgColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabSelected(item.tab) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag(item.tag),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = if (language == AppLanguage.BN) item.titleBn else item.titleEn,
                        tint = iconTint,
                        modifier = Modifier.size(23.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (language == AppLanguage.BN) item.titleBn else item.titleEn,
                        color = if (isSelected) Emerald else TextTertiary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                    )
                }
            }
        }
    }
}
