package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.ui.theme.*

@Composable
fun CalorieProgressCard(
    currentCalories: Int,
    goalCalories: Int,
    proteinGrams: Double,
    carbsGrams: Double,
    fatGrams: Double,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val progressFraction = if (goalCalories > 0) {
        (currentCalories.toFloat() / goalCalories.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val percentage = (progressFraction * 100).toInt()
    val remaining = (goalCalories - currentCalories).coerceAtLeast(0)

    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = tween(durationMillis = 900),
        label = "calorieProgress"
    )

    // Standard daily macro targets based on 2,200 kcal budget
    val proteinGoal = 110.0 // 20%
    val carbsGoal = 250.0   // 50%
    val fatGoal = 60.0      // 25%

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("calorie_summary_card"),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = ForestCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, ForestBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (language == AppLanguage.BN) "দৈনিক ক্যালরি অগ্রগতি" else "Daily Calorie Energy",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (language == AppLanguage.BN) "লক্ষ্য: %,d kcal".format(goalCalories) else "Goal: %,d kcal".format(goalCalories),
                        fontSize = 12.sp,
                        color = MintSoft
                    )
                }

                // Remaining Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(DeepForest)
                        .border(1.dp, ForestBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (language == AppLanguage.BN) "বাকি %,d kcal".format(remaining) else "%,d kcal left".format(remaining),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonLime
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Center Ring + Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Large Glow Progress Ring
                Box(
                    modifier = Modifier.size(114.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(104.dp)) {
                        val strokeWidth = 11.dp.toPx()
                        // Track
                        drawArc(
                            color = DeepForest,
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                        // Gradient Progress Arc
                        drawArc(
                            brush = Brush.sweepGradient(
                                listOf(Emerald, NeonLime, Emerald)
                            ),
                            startAngle = -90f,
                            sweepAngle = animatedProgress * 360f,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$percentage%",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = if (language == AppLanguage.BN) "সম্পন্ন" else "burned",
                            fontSize = 10.sp,
                            color = MintSoft
                        )
                    }
                }

                // Consumed and Goal summary numbers
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatMetricLine(
                        labelBn = "গ্রহণ করা হয়েছে",
                        labelEn = "Consumed",
                        value = "%,d kcal".format(currentCalories),
                        valueColor = Emerald,
                        language = language
                    )
                    StatMetricLine(
                        labelBn = "নিরাপদ ঘাটতি লক্ষ্য",
                        labelEn = "Deficit Target",
                        value = "-400 kcal",
                        valueColor = NeonLime,
                        language = language
                    )
                    StatMetricLine(
                        labelBn = "মোট বাজেট",
                        labelEn = "Budget Total",
                        value = "%,d kcal".format(goalCalories),
                        valueColor = Color.White.copy(alpha = 0.8f),
                        language = language
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Macro Nutrients Breakdown with Progress Bars
            Text(
                text = if (language == AppLanguage.BN) "ম্যাক্রো-নিউট্রিয়েন্ট বিভাজন" else "Macronutrient Distribution",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MintSoft
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MacroProgressItem(
                    labelBn = "প্রোটিন",
                    labelEn = "Protein",
                    current = proteinGrams,
                    goal = proteinGoal,
                    unit = "g",
                    color = ProteinBlue,
                    language = language,
                    modifier = Modifier.weight(1f)
                )
                MacroProgressItem(
                    labelBn = "কার্বস",
                    labelEn = "Carbs",
                    current = carbsGrams,
                    goal = carbsGoal,
                    unit = "g",
                    color = CarbsAmber,
                    language = language,
                    modifier = Modifier.weight(1f)
                )
                MacroProgressItem(
                    labelBn = "ফ্যাট",
                    labelEn = "Fat",
                    current = fatGrams,
                    goal = fatGoal,
                    unit = "g",
                    color = FatViolet,
                    language = language,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatMetricLine(
    labelBn: String,
    labelEn: String,
    value: String,
    valueColor: Color,
    language: AppLanguage
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (language == AppLanguage.BN) labelBn else labelEn,
            fontSize = 12.sp,
            color = TextTertiary
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

@Composable
private fun MacroProgressItem(
    labelBn: String,
    labelEn: String,
    current: Double,
    goal: Double,
    unit: String,
    color: Color,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val fraction = (current / goal).toFloat().coerceIn(0f, 1f)
    Surface(
        color = DeepForest,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ForestBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == AppLanguage.BN) labelBn else labelEn,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextTertiary
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "%.0f%s".format(current, unit),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { fraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = color,
                trackColor = ForestBorder
            )
        }
    }
}
