package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.datasource.FitnessDataSource
import com.example.data.model.*
import com.example.ui.components.CalorieProgressCard
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    userProfile: UserProfile,
    loggedMeals: List<MealLog>,
    waterLiters: Double,
    steps: Int,
    progressHistory: List<ProgressEntry>,
    language: AppLanguage,
    onNavigateTab: (NavigationTab) -> Unit,
    onOpenAiCoach: () -> Unit,
    onAddMealClick: () -> Unit,
    onLogWeightClick: () -> Unit,
    onAddWater: (Double) -> Unit,
    onResetWater: () -> Unit,
    onAddSteps: (Int) -> Unit,
    onStartWorkout: (WorkoutPlan) -> Unit,
    onRemoveMeal: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalCalories = loggedMeals.sumOf { it.calories }
    val totalProtein = loggedMeals.sumOf { it.protein }
    val totalCarbs = loggedMeals.sumOf { it.carbs }
    val totalFat = loggedMeals.sumOf { it.fat }
    val remainingWeight = (userProfile.currentWeightKg - userProfile.goalWeightKg).coerceAtLeast(0.0)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Personalized Hero Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_hero_card"),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = DeepForest),
                border = androidx.compose.foundation.BorderStroke(1.dp, ForestBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Profile greeting & avatar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(ForestCard)
                                    .border(1.5.dp, Emerald, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = userProfile.name.take(1),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NeonLime
                                )
                                // Online ring
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .align(Alignment.BottomEnd)
                                        .clip(CircleShape)
                                        .background(Emerald)
                                        .border(1.5.dp, DeepForest, CircleShape)
                                )
                            }

                            Column {
                                Text(
                                    text = if (language == AppLanguage.BN) "শুভ সকাল, ${userProfile.name}! 👋" else "Good Morning, ${userProfile.name}! 👋",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (language == AppLanguage.BN) "আজকের লক্ষ্যমাত্রায় মনোনিবেশ করুন" else "Focus on today's sustainable goal",
                                    fontSize = 12.sp,
                                    color = MintSoft
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Weight Progress Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ForestCard, RoundedCornerShape(18.dp))
                            .border(1.dp, ForestBorder, RoundedCornerShape(18.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (language == AppLanguage.BN) "বর্তমান ওজন" else "Current",
                                fontSize = 11.sp,
                                color = TextTertiary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "%.1f kg".format(userProfile.currentWeightKg),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (language == AppLanguage.BN) "বাকি আছে" else "To Goal",
                                fontSize = 11.sp,
                                color = MintSoft
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "%.1f kg".format(remainingWeight),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NeonLime
                            )
                            Text(
                                text = if (language == AppLanguage.BN) "${userProfile.targetMonths} মাস টার্গেট" else "${userProfile.targetMonths} mos plan",
                                fontSize = 10.sp,
                                color = TextTertiary
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (language == AppLanguage.BN) "লক্ষ্যমাত্রা" else "Goal",
                                fontSize = 11.sp,
                                color = TextTertiary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "%.1f kg".format(userProfile.goalWeightKg),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // 2. Contextual Quick Actions Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionPill(
                    icon = Icons.Default.Restaurant,
                    labelBn = "খাবার যোগ",
                    labelEn = "Add Food",
                    tint = Emerald,
                    language = language,
                    modifier = Modifier.weight(1f),
                    onClick = onAddMealClick
                )
                QuickActionPill(
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    labelBn = "ওজন লগ",
                    labelEn = "Log Weight",
                    tint = ProteinBlue,
                    language = language,
                    modifier = Modifier.weight(1f),
                    onClick = onLogWeightClick
                )
                QuickActionPill(
                    icon = Icons.Default.PlayArrow,
                    labelBn = "ব্যায়াম শুরু",
                    labelEn = "Start Workout",
                    tint = NeonLime,
                    language = language,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val plan = FitnessDataSource.workouts.firstOrNull { it.isFeatured } ?: FitnessDataSource.workouts.first()
                        onStartWorkout(plan)
                    }
                )
                QuickActionPill(
                    icon = Icons.Default.AutoAwesome,
                    labelBn = "AI কোচ",
                    labelEn = "Ask AI",
                    tint = StreakFire,
                    language = language,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenAiCoach
                )
            }
        }

        // 3. Large Animated-Style Calorie Progress Ring with Macros
        item {
            CalorieProgressCard(
                currentCalories = totalCalories,
                goalCalories = userProfile.dailyCalorieGoal,
                proteinGrams = totalProtein,
                carbsGrams = totalCarbs,
                fatGrams = totalFat,
                language = language
            )
        }

        // 4. Distinctive Visually Rich AI Coach Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenAiCoach() }
                    .testTag("home_ai_coach_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = ForestCardElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, ForestBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(DeepForest)
                                .border(1.dp, Emerald, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = NeonLime,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (language == AppLanguage.BN) "ZUKI AI পরামর্শদাতা" else "ZUKI AI Coach Insight",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Box(
                                    modifier = Modifier
                                        .background(Emerald.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Online",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Emerald
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = if (language == AppLanguage.BN)
                                    "\"আজকে আপনার ৭৮০ kcal বাজেট বাকি আছে। রাতে ১টি রুটি ও বাটি ডাল খেতে পারেন।\""
                                else
                                    "\"You have ~780 kcal left today. A whole wheat roti & lentil dal fits perfectly.\"",
                                fontSize = 11.sp,
                                color = MintSoft,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open Coach",
                        tint = NeonLime,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 5. Water Tracker & Weight Trend Mini-Chart
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Water Intake Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = null,
                                    tint = WaterCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (language == AppLanguage.BN) "পানি পান" else "Water",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            IconButton(onClick = onResetWater, modifier = Modifier.size(24.dp)) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset water", tint = TextTertiary, modifier = Modifier.size(14.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "%.1f / %.1f L".format(waterLiters, userProfile.dailyWaterGoalLiters),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = WaterCyan
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { onAddWater(0.25) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = WaterCyan.copy(alpha = 0.15f)),
                            contentPadding = PaddingValues(0.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "+250ml",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = WaterCyan
                            )
                        }
                    }
                }

                // Weight Trend Mini-Chart Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateTab(NavigationTab.PROGRESS) },
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = Emerald,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (language == AppLanguage.BN) "ওজন ট্রেন্ড" else "Weight Trend",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "74.0 kg",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DeepForest
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = MintGlow,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (language == AppLanguage.BN) "↓ ২.৫ কেজি কমেছে" else "↓ -2.5 kg total",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // 6. Featured Workout Card with Illustration
        item {
            val featured = FitnessDataSource.workouts.firstOrNull { it.isFeatured } ?: FitnessDataSource.workouts.first()
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DeepForest),
                border = androidx.compose.foundation.BorderStroke(1.dp, ForestBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = ForestCard,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (language == AppLanguage.BN) "আজকের ওয়ার্কআউট" else "Daily Workout",
                                fontSize = 10.sp,
                                color = NeonLime,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Text(
                            text = "${featured.durationMinutes} min • ${featured.caloriesBurned} kcal",
                            fontSize = 12.sp,
                            color = MintSoft,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (language == AppLanguage.BN) featured.titleBn else featured.titleEn,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (language == AppLanguage.BN) featured.descriptionBn else featured.descriptionEn,
                        fontSize = 12.sp,
                        color = TextTertiary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onStartWorkout(featured) },
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("home_start_workout_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = DeepForest)
                            Text(
                                text = if (language == AppLanguage.BN) "ব্যায়াম শুরু করুন" else "Start Workout",
                                fontWeight = FontWeight.Bold,
                                color = DeepForest,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // 7. Today's Meal Timeline Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (language == AppLanguage.BN) "আজকের খাবারের টাইমলাইন" else "Today's Meal Timeline",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepForest
                    )
                    Text(
                        text = if (language == AppLanguage.BN) "দেশি খাবারের পুষ্টিকর হিসাব" else "Bangladeshi nutrient log",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                TextButton(onClick = onAddMealClick) {
                    Text(
                        text = if (language == AppLanguage.BN) "+ খাবার যোগ" else "+ Add Meal",
                        color = EmeraldDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Meal Timeline Items
        items(loggedMeals) { meal ->
            TimelineMealItem(meal = meal, language = language, onRemove = { onRemoveMeal(meal.id) })
        }
    }
}

@Composable
private fun QuickActionPill(
    icon: ImageVector,
    labelBn: String,
    labelEn: String,
    tint: Color,
    language: AppLanguage,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
        shadowElevation = 1.dp,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (language == AppLanguage.BN) labelBn else labelEn,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
    }
}

@Composable
fun TimelineMealItem(
    meal: MealLog,
    language: AppLanguage,
    onRemove: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Emoji icon container
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MintGlow),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = meal.emoji, fontSize = 22.sp)
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = if (language == AppLanguage.BN) meal.foodNameBn else meal.foodNameEn,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${if (language == AppLanguage.BN) meal.servingDescBn else meal.servingDescEn} • ${meal.timestamp}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "P: %.1fg • C: %.1fg • F: %.1fg".format(meal.protein, meal.carbs, meal.fat),
                        fontSize = 10.sp,
                        color = TextTertiary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "${meal.calories} kcal",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DeepForest
                )
                if (onRemove != null) {
                    IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Remove", tint = TextTertiary, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
