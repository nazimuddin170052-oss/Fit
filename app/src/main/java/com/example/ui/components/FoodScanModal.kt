package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Remove
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
import com.example.data.model.MealLog
import com.example.data.model.MealType
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

private data class ScanDetectionItem(
    val titleBn: String,
    val titleEn: String,
    val emoji: String,
    val baseServingDescBn: String,
    val baseServingDescEn: String,
    val confidence: Int,
    val baseCalories: Int,
    val baseProtein: Double,
    val baseCarbs: Double,
    val baseFat: Double
)

@Composable
fun FoodScanModal(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onMealLogged: (MealLog) -> Unit
) {
    val presets = remember {
        listOf(
            ScanDetectionItem(
                titleBn = "ভাত + রুই মাছের ঝোল + সবজি ভাজি",
                titleEn = "Rice + Rui Fish Curry + Mixed Veg",
                emoji = "🐟",
                baseServingDescBn = "১ কাপ ভাত, ১ পিস মাছ ও সবজি",
                baseServingDescEn = "1 cup rice, 1 pc fish & veg",
                confidence = 96,
                baseCalories = 471,
                baseProtein = 25.3,
                baseCarbs = 59.5,
                baseFat = 13.4
            ),
            ScanDetectionItem(
                titleBn = "আটার রুটি (২টি) + ডিম ভাজি",
                titleEn = "Whole Wheat Roti (2 pcs) + Egg Omelette",
                emoji = "🍳",
                baseServingDescBn = "২টি ঘরে বানানো রুটি ও ১টি ডিম",
                baseServingDescEn = "2 handmade rotis & 1 egg",
                confidence = 98,
                baseCalories = 355,
                baseProtein = 13.5,
                baseCarbs = 45.5,
                baseFat = 12.2
            ),
            ScanDetectionItem(
                titleBn = "মুসুর ডাল + লাউ শাক ভাজি",
                titleEn = "Musur Dal Soup + Leafy Greens",
                emoji = "🥣",
                baseServingDescBn = "১ বাটি পাতলা ডাল ও ১ বাটি শাক",
                baseServingDescEn = "1 bowl dal & 1 bowl greens",
                confidence = 94,
                baseCalories = 195,
                baseProtein = 10.3,
                baseCarbs = 23.0,
                baseFat = 6.0
            ),
            ScanDetectionItem(
                titleBn = "মুরগির মাংস ভুনা + শসা সালাদ",
                titleEn = "Deshi Chicken Curry + Cucumber Salad",
                emoji = "🍗",
                baseServingDescBn = "১ বাটি চিকেন ও সালাদ",
                baseServingDescEn = "1 bowl chicken & salad",
                confidence = 95,
                baseCalories = 280,
                baseProtein = 26.0,
                baseCarbs = 6.5,
                baseFat = 15.0
            )
        )
    }

    var selectedIndex by remember { mutableStateOf(0) }
    var portionMultiplier by remember { mutableStateOf(1.0) } // 0.5, 1.0, 1.5, 2.0
    var isConfirmedStep by remember { mutableStateOf(false) }

    // Laser scan animation
    val infiniteTransition = rememberInfiniteTransition(label = "laserScan")
    val laserOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserY"
    )

    val activeItem = presets[selectedIndex]
    val calculatedCalories = (activeItem.baseCalories * portionMultiplier).toInt()
    val calculatedProtein = ((activeItem.baseProtein * portionMultiplier * 10).toInt()) / 10.0
    val calculatedCarbs = ((activeItem.baseCarbs * portionMultiplier * 10).toInt()) / 10.0
    val calculatedFat = ((activeItem.baseFat * portionMultiplier * 10).toInt()) / 10.0

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
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ForestCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = NeonLime,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (language == AppLanguage.BN) "AI খাবার শনাক্তকরণ" else "AI Food Scanner",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (!isConfirmedStep) {
                                    if (language == AppLanguage.BN) "ধাপ ১: থালা স্ক্যান করুন" else "Step 1: Scan plate"
                                } else {
                                    if (language == AppLanguage.BN) "ধাপ ২: পরিমাণ যাচাই ও নিশ্চিতকরণ" else "Step 2: Verify portion"
                                },
                                fontSize = 11.sp,
                                color = MintSoft
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextTertiary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (!isConfirmedStep) {
                    // STAGE 1: Viewfinder with animated laser line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Charcoal)
                            .border(1.5.dp, Emerald.copy(alpha = 0.6f), RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Framing brackets
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("┏", color = NeonLime, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                                Text("┓", color = NeonLime, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("┗", color = NeonLime, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                                Text("┛", color = NeonLime, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Center item emoji & guidance
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = activeItem.emoji, fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (language == AppLanguage.BN) "খাবারের প্লেট ফ্রেমের মাঝে রাখুন" else "Center meal plate in frame",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp
                            )
                        }

                        // Laser line
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(2.dp)
                                .align(Alignment.TopCenter)
                                .offset(y = (laserOffset * 170).dp)
                                .background(NeonLime)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Preset dish selector
                    Text(
                        text = if (language == AppLanguage.BN) "নমুনা খাবার নির্বাচন করুন:" else "Select Sample Dish to Detect:",
                        fontSize = 12.sp,
                        color = MintSoft,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presets.forEachIndexed { idx, preset ->
                            val isSel = selectedIndex == idx
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSel) Emerald else ForestCard)
                                    .clickable {
                                        selectedIndex = idx
                                        portionMultiplier = 1.0
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = preset.emoji,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Detected Dish Preview Card
                    Surface(
                        color = ForestCard,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ForestBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (language == AppLanguage.BN) activeItem.titleBn else activeItem.titleEn,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Box(
                                    modifier = Modifier
                                        .background(Emerald.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${activeItem.confidence}% নিশ্চিত",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Emerald
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${activeItem.baseCalories} kcal • P: ${activeItem.baseProtein}g • C: ${activeItem.baseCarbs}g • F: ${activeItem.baseFat}g",
                                fontSize = 11.sp,
                                color = TextTertiary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { isConfirmedStep = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("scan_next_verify_button")
                    ) {
                        Text(
                            text = if (language == AppLanguage.BN) "শনাক্তকরণ নিশ্চিত করুন" else "Verify Detected Meal",
                            color = DeepForest,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    // STAGE 2: Confirmation & Portion Adjustment Screen
                    Surface(
                        color = ForestCard,
                        shape = RoundedCornerShape(18.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ForestBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = activeItem.emoji, fontSize = 28.sp)
                                Column {
                                    Text(
                                        text = if (language == AppLanguage.BN) activeItem.titleBn else activeItem.titleEn,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = if (language == AppLanguage.BN) activeItem.baseServingDescBn else activeItem.baseServingDescEn,
                                        fontSize = 11.sp,
                                        color = MintSoft
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Portion Adjuster Controls
                            Text(
                                text = if (language == AppLanguage.BN) "পরিমাণ যাচাই করুন (Portion Size):" else "Verify Serving Portion:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextTertiary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        if (portionMultiplier > 0.5) portionMultiplier -= 0.5
                                    },
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(DeepForest)
                                ) {
                                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = Color.White)
                                }

                                Surface(
                                    color = DeepForest,
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ForestBorder)
                                ) {
                                    Text(
                                        text = "${portionMultiplier}x ${if (language == AppLanguage.BN) "সার্ভিং" else "Serving"}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonLime,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        if (portionMultiplier < 3.0) portionMultiplier += 0.5
                                    },
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(DeepForest)
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = Color.White)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Updated Nutrients Calculation
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(DeepForest, RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (language == AppLanguage.BN) "গণনাকৃত মোট ক্যালরি" else "Calculated Calories",
                                        fontSize = 11.sp,
                                        color = TextTertiary
                                    )
                                    Text(
                                        text = "$calculatedCalories kcal",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Emerald
                                    )
                                }
                                Text(
                                    text = "P: ${calculatedProtein}g • C: ${calculatedCarbs}g • F: ${calculatedFat}g",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Safe Disclaimer: Image-based nutrition estimates are never exact
                    Surface(
                        color = ForestCard,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = NeonLime, modifier = Modifier.size(16.dp))
                            Text(
                                text = if (language == AppLanguage.BN)
                                    "ছবি ভিত্তিক পুষ্টিমান আনুমানিক এবং জাতীয় গড় তথ্যের ওপর প্রতিষ্ঠিত। ১০০% নির্ভুল নয়।"
                                else
                                    "Image-based nutrition estimates are approximate averages and never treated as exact.",
                                fontSize = 11.sp,
                                color = MintSoft,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { isConfirmedStep = false },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (language == AppLanguage.BN) "পেছনে যান" else "Back", color = Color.White)
                        }

                        Button(
                            onClick = {
                                val timeStr = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date())
                                val meal = MealLog(
                                    id = "scan_meal_${System.currentTimeMillis()}",
                                    mealType = MealType.LUNCH,
                                    foodNameBn = activeItem.titleBn,
                                    foodNameEn = activeItem.titleEn,
                                    emoji = activeItem.emoji,
                                    servingDescBn = "${portionMultiplier}x ${activeItem.baseServingDescBn} (AI স্ক্যান)",
                                    servingDescEn = "${portionMultiplier}x ${activeItem.baseServingDescEn} (AI Scan)",
                                    calories = calculatedCalories,
                                    protein = calculatedProtein,
                                    carbs = calculatedCarbs,
                                    fat = calculatedFat,
                                    timestamp = timeStr
                                )
                                onMealLogged(meal)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1.5f)
                                .height(46.dp)
                                .testTag("confirm_log_scanned_meal_button")
                        ) {
                            Text(
                                text = if (language == AppLanguage.BN) "নিশ্চিত করে লগ করুন" else "Confirm & Log",
                                color = DeepForest,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
