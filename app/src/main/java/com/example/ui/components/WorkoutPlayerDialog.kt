package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AppLanguage
import com.example.data.model.WorkoutPlan
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun WorkoutPlayerDialog(
    workout: WorkoutPlan,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onWorkoutCompleted: (caloriesBurned: Int) -> Unit
) {
    var currentExerciseIndex by remember { mutableStateOf(0) }
    var secondsRemaining by remember { mutableStateOf(30) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var isRestPhase by remember { mutableStateOf(false) }
    var restSecondsRemaining by remember { mutableStateOf(20) }
    var isCompleted by remember { mutableStateOf(false) }

    val exercises = workout.exercises
    val currentExercise = exercises.getOrNull(currentExerciseIndex)

    // Timer effect for active workout
    LaunchedEffect(isTimerRunning, secondsRemaining, isRestPhase) {
        if (!isRestPhase && isTimerRunning && secondsRemaining > 0) {
            delay(1000L)
            secondsRemaining--
        } else if (!isRestPhase && isTimerRunning && secondsRemaining == 0) {
            // Switch to Rest Phase
            isTimerRunning = false
            if (currentExerciseIndex < exercises.size - 1) {
                isRestPhase = true
                restSecondsRemaining = currentExercise?.restSeconds ?: 20
            } else {
                isCompleted = true
            }
        }
    }

    // Timer effect for rest phase
    LaunchedEffect(isRestPhase, restSecondsRemaining) {
        if (isRestPhase && restSecondsRemaining > 0) {
            delay(1000L)
            restSecondsRemaining--
        } else if (isRestPhase && restSecondsRemaining == 0) {
            // Next exercise
            isRestPhase = false
            currentExerciseIndex++
            secondsRemaining = 30
            isTimerRunning = false
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .wrapContentHeight()
                .padding(vertical = 20.dp),
            shape = RoundedCornerShape(26.dp),
            color = DeepForest,
            border = androidx.compose.foundation.BorderStroke(1.dp, ForestBorder),
            tonalElevation = 8.dp
        ) {
            if (isCompleted) {
                // Celebration Completion View
                Column(
                    modifier = Modifier.padding(26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(ForestCard)
                            .border(2.dp, NeonLime, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = NeonLime,
                            modifier = Modifier.size(46.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = if (language == AppLanguage.BN) "দারুণ কাজ! সেশন সম্পন্ন!" else "Awesome! Workout Completed!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (language == AppLanguage.BN)
                            "আপনি সফলভাবে ~${workout.caloriesBurned} ক্যালরি বার্ন করেছেন।"
                        else
                            "You burned ~${workout.caloriesBurned} kcal successfully.",
                        fontSize = 14.sp,
                        color = MintSoft
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Surface(
                        color = ForestCard,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(if (language == AppLanguage.BN) "মোট ব্যায়াম" else "Exercises", fontSize = 11.sp, color = TextTertiary)
                                Text("${exercises.size}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(if (language == AppLanguage.BN) "স্থায়িত্ব" else "Duration", fontSize = 11.sp, color = TextTertiary)
                                Text("${workout.durationMinutes} min", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeonLime)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(if (language == AppLanguage.BN) "ক্যালরি" else "Burned", fontSize = 11.sp, color = TextTertiary)
                                Text("${workout.caloriesBurned} kcal", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Emerald)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    Button(
                        onClick = {
                            onWorkoutCompleted(workout.caloriesBurned)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("finish_workout_button")
                    ) {
                        Text(
                            text = if (language == AppLanguage.BN) "রেকর্ড সংরক্ষণ করুন" else "Save Workout Record",
                            fontWeight = FontWeight.ExtraBold,
                            color = DeepForest,
                            fontSize = 14.sp
                        )
                    }
                }
            } else if (isRestPhase) {
                // REST TIMER PHASE
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (language == AppLanguage.BN) "বিশ্রাম পর্ব (Rest Time)" else "Rest Time",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonLime
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (language == AppLanguage.BN) "গভীর শ্বাস নিন এবং পানি পান করুন" else "Breathe deeply and prepare for next set",
                        fontSize = 12.sp,
                        color = MintSoft
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(ForestCard)
                            .border(3.dp, NeonLime, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${restSecondsRemaining}s",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Next Exercise Preview
                    val nextEx = exercises.getOrNull(currentExerciseIndex + 1)
                    if (nextEx != null) {
                        Surface(
                            color = ForestCard,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (language == AppLanguage.BN) "পরবর্তী ব্যায়াম:" else "Up next:",
                                    fontSize = 11.sp,
                                    color = TextTertiary
                                )
                                Text(
                                    text = if (language == AppLanguage.BN) nextEx.nameBn else nextEx.nameEn,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Skip Rest Button
                    OutlinedButton(
                        onClick = {
                            isRestPhase = false
                            currentExerciseIndex++
                            secondsRemaining = 30
                            isTimerRunning = false
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(if (language == AppLanguage.BN) "বিশ্রাম স্কিপ করুন" else "Skip Rest", color = Color.White)
                            Icon(imageVector = Icons.Default.SkipNext, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            } else if (currentExercise != null) {
                // ACTIVE EXERCISE PHASE
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header with progress indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(ForestCard, RoundedCornerShape(10.dp))
                                .border(1.dp, ForestBorder, RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${currentExerciseIndex + 1} / ${exercises.size}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonLime
                            )
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextTertiary)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (language == AppLanguage.BN) currentExercise.nameBn else currentExercise.nameEn,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${currentExercise.sets} সেটস • ${if (language == AppLanguage.BN) currentExercise.repsOrDurationBn else currentExercise.repsOrDurationEn}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Emerald
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Countdown Timer Display
                    Box(
                        modifier = Modifier
                            .size(118.dp)
                            .clip(CircleShape)
                            .background(ForestCard)
                            .border(3.dp, Emerald, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${secondsRemaining}s",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Timer Play / Pause / Reset Controls
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        IconButton(
                            onClick = { isTimerRunning = !isTimerRunning },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Emerald)
                        ) {
                            Icon(
                                imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = DeepForest
                            )
                        }

                        IconButton(
                            onClick = {
                                isTimerRunning = false
                                secondsRemaining = 30
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(ForestCard)
                                .border(1.dp, ForestBorder, CircleShape)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Posture & Safety Tip Card
                    Surface(
                        color = ForestCard,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ForestBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (language == AppLanguage.BN) "পরামর্শ ও সঠিক নিয়ম:" else "Form Instructions:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonLime
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (language == AppLanguage.BN) currentExercise.tipBn else currentExercise.tipEn,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Next / Complete Button
                    Button(
                        onClick = {
                            if (currentExerciseIndex < exercises.size - 1) {
                                isRestPhase = true
                                restSecondsRemaining = currentExercise.restSeconds
                            } else {
                                isCompleted = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("next_exercise_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (currentExerciseIndex < exercises.size - 1) {
                                    if (language == AppLanguage.BN) "সেট শেষ / বিশ্রাম" else "Complete Set / Rest"
                                } else {
                                    if (language == AppLanguage.BN) "ওয়ার্কআউট সম্পন্ন করুন" else "Complete Workout"
                                },
                                fontWeight = FontWeight.ExtraBold,
                                color = DeepForest,
                                fontSize = 14.sp
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = DeepForest,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
