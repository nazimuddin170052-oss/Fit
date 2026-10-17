package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.datasource.FitnessDataSource
import com.example.data.model.AppLanguage
import com.example.data.model.WorkoutCategory
import com.example.data.model.WorkoutPlan
import com.example.ui.theme.*

@Composable
fun WorkoutScreen(
    language: AppLanguage,
    onStartWorkout: (WorkoutPlan) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(WorkoutCategory.ALL) }

    val filteredWorkouts = remember(selectedCategory) {
        if (selectedCategory == WorkoutCategory.ALL) {
            FitnessDataSource.workouts
        } else {
            FitnessDataSource.workouts.filter { it.category == selectedCategory }
        }
    }

    val featuredWorkout = FitnessDataSource.workouts.firstOrNull { it.isFeatured } ?: FitnessDataSource.workouts.first()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(WorkoutCategory.entries) { category ->
                    val isSelected = selectedCategory == category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) DeepForest else SurfaceCard)
                            .border(1.dp, if (isSelected) ForestBorder else BorderLight, RoundedCornerShape(20.dp))
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (language == AppLanguage.BN) category.labelBn else category.labelEn,
                            color = if (isSelected) NeonLime else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Featured Workout Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = DeepForest),
                border = androidx.compose.foundation.BorderStroke(1.dp, ForestBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
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
                                text = if (language == AppLanguage.BN) "ফিচার্ড ফ্যাট বার্নিং" else "Featured Workout",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonLime,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Text(
                            text = if (language == AppLanguage.BN) featuredWorkout.levelBn else featuredWorkout.levelEn,
                            fontSize = 11.sp,
                            color = Emerald,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (language == AppLanguage.BN) featuredWorkout.titleBn else featuredWorkout.titleEn,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(imageVector = Icons.Default.AccessTime, contentDescription = null, tint = MintSoft, modifier = Modifier.size(15.dp))
                            Text(text = "${featuredWorkout.durationMinutes} min", color = MintSoft, fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(imageVector = Icons.Default.LocalFireDepartment, contentDescription = null, tint = StreakFire, modifier = Modifier.size(15.dp))
                            Text(text = "${featuredWorkout.caloriesBurned} kcal", color = MintSoft, fontSize = 12.sp)
                        }
                        Text(
                            text = "• ${featuredWorkout.exercises.size} ${if (language == AppLanguage.BN) "ব্যায়াম" else "moves"}",
                            color = TextTertiary,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (language == AppLanguage.BN) featuredWorkout.descriptionBn else featuredWorkout.descriptionEn,
                        fontSize = 12.sp,
                        color = TextTertiary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onStartWorkout(featuredWorkout) },
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("workout_start_featured")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = DeepForest)
                            Text(
                                text = if (language == AppLanguage.BN) "ওয়ার্কআউট শুরু করুন" else "Start Workout",
                                fontWeight = FontWeight.ExtraBold,
                                color = DeepForest,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Section Title
        item {
            Column {
                Text(
                    text = if (language == AppLanguage.BN) "ওয়ার্কআউট লাইব্রেরি" else "Workout Library",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepForest
                )
                Text(
                    text = if (language == AppLanguage.BN) "ঘরে বসে করার উপযোগী বিভিন্ন ফিটনেস প্রোগ্রাম" else "Home-friendly programs for every level",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        items(filteredWorkouts.filter { it.id != featuredWorkout.id }) { workout ->
            WorkoutItemCard(
                workout = workout,
                language = language,
                onStart = { onStartWorkout(workout) }
            )
        }
    }
}

@Composable
private fun WorkoutItemCard(
    workout: WorkoutPlan,
    language: AppLanguage,
    onStart: () -> Unit
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
                .padding(16.dp),
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
                        .clip(RoundedCornerShape(14.dp))
                        .background(MintGlow),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = DeepForest,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = if (language == AppLanguage.BN) workout.titleBn else workout.titleEn,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${workout.durationMinutes} min • ${workout.caloriesBurned} kcal • ${workout.exercises.size} moves",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (language == AppLanguage.BN) workout.levelBn else workout.levelEn,
                        fontSize = 11.sp,
                        color = EmeraldDark,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Button(
                onClick = onStart,
                colors = ButtonDefaults.buttonColors(containerColor = DeepForest),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (language == AppLanguage.BN) "শুরু" else "Start",
                    color = NeonLime,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
