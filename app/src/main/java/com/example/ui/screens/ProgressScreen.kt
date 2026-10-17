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
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WaterDrop
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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Scale
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppLanguage
import com.example.data.model.DailyCalorieEntry
import com.example.data.model.ProgressEntry
import com.example.data.model.UserProfile
import com.example.ui.components.CaloricIntakeChartCard
import com.example.ui.components.WeightLineChartCard
import com.example.ui.theme.*

enum class ProgressChartTab {
    WEIGHT, CALORIES, BOTH
}

@Composable
fun ProgressScreen(
    userProfile: UserProfile,
    progressHistory: List<ProgressEntry>,
    caloricHistory: List<DailyCalorieEntry> = emptyList(),
    waterLiters: Double,
    steps: Int,
    language: AppLanguage,
    onAddProgress: (weight: Double, waist: Double, notesBn: String, notesEn: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedChartTab by remember { mutableStateOf(ProgressChartTab.WEIGHT) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Chart View Switcher Tabs (Weight, Caloric Intake, Both)
        item {
            Surface(
                color = SurfaceCard,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ChartTabButton(
                        titleBn = "⚖️ ওজন ট্রেন্ড",
                        titleEn = "⚖️ Weight Trend",
                        isSelected = selectedChartTab == ProgressChartTab.WEIGHT,
                        onClick = { selectedChartTab = ProgressChartTab.WEIGHT },
                        language = language,
                        modifier = Modifier.weight(1f)
                    )
                    ChartTabButton(
                        titleBn = "🔥 ক্যালরি ইনটেক",
                        titleEn = "🔥 Calorie Intake",
                        isSelected = selectedChartTab == ProgressChartTab.CALORIES,
                        onClick = { selectedChartTab = ProgressChartTab.CALORIES },
                        language = language,
                        modifier = Modifier.weight(1f)
                    )
                    ChartTabButton(
                        titleBn = "📊 উভয় চার্ট",
                        titleEn = "📊 Both",
                        isSelected = selectedChartTab == ProgressChartTab.BOTH,
                        onClick = { selectedChartTab = ProgressChartTab.BOTH },
                        language = language,
                        modifier = Modifier.weight(0.85f)
                    )
                }
            }
        }

        // Active Chart(s) based on selected tab
        item {
            when (selectedChartTab) {
                ProgressChartTab.WEIGHT -> {
                    WeightLineChartCard(
                        entries = progressHistory,
                        goalWeightKg = userProfile.goalWeightKg,
                        language = language
                    )
                }
                ProgressChartTab.CALORIES -> {
                    CaloricIntakeChartCard(
                        entries = caloricHistory,
                        targetCalorieGoal = userProfile.dailyCalorieGoal,
                        language = language
                    )
                }
                ProgressChartTab.BOTH -> {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        WeightLineChartCard(
                            entries = progressHistory,
                            goalWeightKg = userProfile.goalWeightKg,
                            language = language
                        )
                        CaloricIntakeChartCard(
                            entries = caloricHistory,
                            targetCalorieGoal = userProfile.dailyCalorieGoal,
                            language = language
                        )
                    }
                }
            }
        }

        // Workout Consistency Streak Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DeepForest),
                border = androidx.compose.foundation.BorderStroke(1.dp, ForestBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.LocalFireDepartment, contentDescription = null, tint = StreakFire, modifier = Modifier.size(22.dp))
                            Text(
                                text = if (language == AppLanguage.BN) "ওয়ার্কআউট ধারাবাহিকতা" else "Workout Consistency",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Text(
                            text = "${userProfile.streakDays} ${if (language == AppLanguage.BN) "দিনের স্ট্রিক 🔥" else "Day Streak 🔥"}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NeonLime
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 7 Weekday Dots (Sat to Fri)
                    val days = listOf("শনি", "রবি", "সোম", "মঙ্গল", "বুধ", "বৃহঃ", "শুক্র")
                    val daysEn = listOf("Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        days.forEachIndexed { idx, dayBn ->
                            val isCompleted = idx <= 4 // 5-day streak
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(if (isCompleted) Emerald else ForestCard)
                                        .border(1.dp, if (isCompleted) NeonLime else ForestBorder, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isCompleted) {
                                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = DeepForest, modifier = Modifier.size(18.dp))
                                    } else {
                                        Text(text = "·", color = TextTertiary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (language == AppLanguage.BN) dayBn else daysEn[idx],
                                    fontSize = 10.sp,
                                    color = if (isCompleted) MintSoft else TextTertiary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Daily Metric Cards Row: Water (2.5L), Sleep (7.5h), Steps (8,450)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricSummaryCard(
                    titleBn = "পানি",
                    titleEn = "Water",
                    value = "%.1f L".format(waterLiters),
                    icon = Icons.Default.WaterDrop,
                    iconTint = WaterCyan,
                    language = language,
                    modifier = Modifier.weight(1f)
                )

                MetricSummaryCard(
                    titleBn = "ঘুম",
                    titleEn = "Sleep",
                    value = "7.5 h",
                    icon = Icons.Default.Bedtime,
                    iconTint = SleepIndigo,
                    language = language,
                    modifier = Modifier.weight(1f)
                )

                MetricSummaryCard(
                    titleBn = "হাঁটা",
                    titleEn = "Steps",
                    value = "%,d".format(steps),
                    icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                    iconTint = StepsGreen,
                    language = language,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Add Progress Log Button
        item {
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Emerald),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("add_progress_log_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = DeepForest)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (language == AppLanguage.BN) "নতুন ওজন ও কোমর পরিমাপ রেকর্ড করুন" else "Log Weight & Waist Measurement",
                    fontWeight = FontWeight.ExtraBold,
                    color = DeepForest,
                    fontSize = 13.sp
                )
            }
        }

        // Measurement History Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == AppLanguage.BN) "পরিমাপের রেকর্ড ও ইতিহাস" else "Measurement Log History",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepForest
                )
                Text(
                    text = "${progressHistory.size} ${if (language == AppLanguage.BN) "টি এন্ট্রি" else "entries"}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        if (progressHistory.isEmpty()) {
            item {
                Surface(
                    color = SurfaceCard,
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(28.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (language == AppLanguage.BN) "কোনো রেকর্ড নেই। উপরের বোতাম দিয়ে প্রথম পরিমাপ যোগ করুন।" else "No logs recorded yet. Tap above to log your first entry.",
                            color = TextTertiary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(progressHistory.reversed()) { entry ->
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = if (language == AppLanguage.BN) entry.dateLabelBn else entry.dateLabelEn,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                // Clear distinction between Demo and Real user record
                                if (entry.isSample) {
                                    Box(
                                        modifier = Modifier
                                            .background(MintGlow, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (language == AppLanguage.BN) "নমুনা ডেটা" else "Demo Data",
                                            fontSize = 9.sp,
                                            color = EmeraldDark,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .background(DeepForest, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (language == AppLanguage.BN) "ব্যবহারকারী রেকর্ড" else "User Record",
                                            fontSize = 9.sp,
                                            color = NeonLime,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            val note = if (language == AppLanguage.BN) entry.notesBn else entry.notesEn
                            if (note.isNotBlank()) {
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(text = note, fontSize = 11.sp, color = TextTertiary)
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "%.1f kg".format(entry.weightKg),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = DeepForest
                            )
                            Text(
                                text = "${if (language == AppLanguage.BN) "কোমর:" else "Waist:"} %.1f\"".format(entry.waistInches),
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Sustainable Weight Policy Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DeepForest),
                border = androidx.compose.foundation.BorderStroke(1.dp, ForestBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = NeonLime)
                        Text(
                            text = if (language == AppLanguage.BN) "নিরাপদ ও টেকসই ওজন ব্যবস্থাপনা" else "Safe & Sustainable Weight Policy",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (language == AppLanguage.BN)
                            "• সপ্তাহে ০.৫ থেকে ১ কেজি ওজন হ্রাস হলো বিশ্ব স্বাস্থ্য সংস্থা (WHO) স্বীকৃত নিরাপদ হার।\n• ক্র্যাশ ডায়েট বা অনাহার মেটাবলিজম কমিয়ে দেয় এবং পেশিক্ষয় করায়।\n• দেশি পুষ্টিকর খাবার, পানি এবং নিয়মিত হালকা হাঁটাই দীর্ঘস্থায়ী সাফল্যের চাবিকাঠি।"
                        else
                            "• Losing 0.5 - 1.0 kg per week is the medically proven sustainable rate.\n• Starvation diets trigger metabolic slowdown and muscle wasting.\n• Wholesome local foods, consistent hydration, and active walking ensure permanent health.",
                        fontSize = 12.sp,
                        color = MintSoft,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddMeasurementDialog(
            language = language,
            onDismiss = { showAddDialog = false },
            onSave = { weight, waist, note ->
                onAddProgress(weight, waist, note, note)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun MetricSummaryCard(
    titleBn: String,
    titleEn: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (language == AppLanguage.BN) titleBn else titleEn,
                fontSize = 11.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun AddMeasurementDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (Double, Double, String) -> Unit
) {
    var weightInput by remember { mutableStateOf("74.0") }
    var waistInput by remember { mutableStateOf("33.0") }
    var noteInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(22.dp),
            color = SurfaceCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = if (language == AppLanguage.BN) "পরিমাপ রেকর্ড করুন" else "Log Measurement",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepForest
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text(if (language == AppLanguage.BN) "ওজন (কেজি / kg)" else "Weight (kg)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = waistInput,
                    onValueChange = { waistInput = it },
                    label = { Text(if (language == AppLanguage.BN) "কোমর (ইঞ্চি / inches)" else "Waist (inches)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = noteInput,
                    onValueChange = { noteInput = it },
                    label = { Text(if (language == AppLanguage.BN) "নোট / অনুভূতি" else "Note / Feeling") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        val w = weightInput.toDoubleOrNull() ?: 74.0
                        val waist = waistInput.toDoubleOrNull() ?: 33.0
                        onSave(w, waist, noteInput)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (language == AppLanguage.BN) "সংরক্ষণ করুন" else "Save Log",
                        color = DeepForest,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun ChartTabButton(
    titleBn: String,
    titleEn: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) DeepForest else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (language == AppLanguage.BN) titleBn else titleEn,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = if (isSelected) NeonLime else TextSecondary
        )
    }
}

