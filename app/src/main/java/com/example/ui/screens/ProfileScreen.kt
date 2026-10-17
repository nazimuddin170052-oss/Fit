package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
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
import com.example.data.model.AppLanguage
import com.example.data.model.UserProfile
import com.example.ui.components.SetupInstructionsDialog
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onUpdateProfile: (UserProfile) -> Unit,
    onResetDemoData: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showSetupGuideDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = DeepForest),
                border = androidx.compose.foundation.BorderStroke(1.dp, ForestBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(ForestCard)
                            .border(2.dp, Emerald, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userProfile.name.take(1),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NeonLime
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = userProfile.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${userProfile.age} ${if (language == AppLanguage.BN) "বছর" else "yrs"} • ${userProfile.heightCm} cm • ${userProfile.streakDays} ${if (language == AppLanguage.BN) "দিনের স্ট্রিক 🔥" else "Day Streak 🔥"}",
                        fontSize = 13.sp,
                        color = MintSoft
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Weight Goal Grid
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ForestCard, RoundedCornerShape(16.dp))
                            .border(1.dp, ForestBorder, RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ProfileStat(titleBn = "বর্তমান", titleEn = "Current", value = "%.0f kg".format(userProfile.currentWeightKg), language = language)
                        ProfileStat(titleBn = "লক্ষ্য", titleEn = "Goal", value = "%.0f kg".format(userProfile.goalWeightKg), language = language)
                        ProfileStat(titleBn = "টার্গেট", titleEn = "Target", value = "${userProfile.targetMonths} ${if (language == AppLanguage.BN) "মাস" else "mos"}", language = language)
                        ProfileStat(titleBn = "দৈনিক বাজেট", titleEn = "Daily Budget", value = "${userProfile.dailyCalorieGoal} kcal", language = language)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = { showEditProfileDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MintSoft.copy(alpha = 0.5f)),
                        modifier = Modifier.testTag("edit_profile_button")
                    ) {
                        Text(
                            text = if (language == AppLanguage.BN) "প্রোফাইল ও লক্ষ্য এডিট করুন" else "Edit Profile & Goals",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Language Switcher Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == AppLanguage.BN) "অ্যাপের ভাষা (Language)" else "App Language",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepForest
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LanguageOptionPill(
                            label = "বাংলা (ডিফল্ট) 🇧🇩",
                            isSelected = language == AppLanguage.BN,
                            onClick = { onLanguageChange(AppLanguage.BN) },
                            modifier = Modifier.weight(1f)
                        )
                        LanguageOptionPill(
                            label = "English 🇬🇧",
                            isSelected = language == AppLanguage.EN,
                            onClick = { onLanguageChange(AppLanguage.EN) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Settings & Options Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    // Setup Instructions
                    SettingsRowItem(
                        icon = Icons.Default.Smartphone,
                        titleBn = "APK প্যাকেজিং ও সেটআপ গাইড",
                        titleEn = "APK Packaging & Setup Guide",
                        subtitleBn = "অ্যান্ড্রয়েড ইনস্টলেশন ও ক্যাপাসিটর প্রস্তুতি",
                        subtitleEn = "Android installation & Capacitor readiness",
                        onClick = { showSetupGuideDialog = true },
                        language = language
                    )

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp), color = BorderLight)

                    // Reset Demo Data
                    SettingsRowItem(
                        icon = Icons.Default.Refresh,
                        titleBn = "নমুনা ডেটা রিসেট করুন",
                        titleEn = "Reset Sample Data",
                        subtitleBn = "ডিফল্ট খাবার, ওজন ও চ্যাট পুনরুদ্ধার করুন",
                        subtitleEn = "Restore initial sample food, weight & chat",
                        onClick = onResetDemoData,
                        language = language
                    )
                }
            }
        }

        // Safe Guidelines & Medical Disclaimer
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DeepForest),
                border = androidx.compose.foundation.BorderStroke(1.dp, ForestBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.HealthAndSafety, contentDescription = null, tint = NeonLime)
                        Text(
                            text = if (language == AppLanguage.BN) "স্বাস্থ্যসম্মত ওজন নিয়ন্ত্রণ নির্দেশিকা" else "Safe Weight Policy",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (language == AppLanguage.BN)
                            "ZUKI Fit AI কোনো ক্র্যাশ বা অনাহার ডায়েট সমর্থন করে না। নিরাপদ ক্যালরি ঘাটতি (৩০০-৫০০ kcal), দেশি পুষ্টিকর খাবার, পানি এবং পর্যাপ্ত ঘুমই দীর্ঘস্থায়ী ফিটনেসের চাবিকাঠি।"
                        else
                            "ZUKI Fit AI promotes sustainable fitness. Starvation and extreme diets harm metabolism. Safe 300-500 kcal deficit with wholesome foods and proper hydration ensures permanent results.",
                        fontSize = 12.sp,
                        color = MintSoft,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Version Footer
        item {
            Box(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = "ZUKI Fit AI v1.2.0 • Premium Native Android Compose",
                    fontSize = 11.sp,
                    color = TextTertiary
                )
            }
        }
    }

    if (showEditProfileDialog) {
        EditProfileDialog(
            currentProfile = userProfile,
            language = language,
            onDismiss = { showEditProfileDialog = false },
            onSave = { updated ->
                onUpdateProfile(updated)
                showEditProfileDialog = false
            }
        )
    }

    if (showSetupGuideDialog) {
        SetupInstructionsDialog(
            language = language,
            onDismiss = { showSetupGuideDialog = false }
        )
    }
}

@Composable
private fun ProfileStat(titleBn: String, titleEn: String, value: String, language: AppLanguage) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = if (language == AppLanguage.BN) titleBn else titleEn, fontSize = 10.sp, color = TextTertiary)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
private fun LanguageOptionPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) DeepForest else BackgroundLight,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) ForestBorder else BorderLight),
        modifier = modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) NeonLime else TextPrimary
            )
        }
    }
}

@Composable
private fun SettingsRowItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    titleBn: String,
    titleEn: String,
    subtitleBn: String,
    subtitleEn: String,
    onClick: () -> Unit,
    language: AppLanguage
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MintGlow),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = DeepForest, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(text = if (language == AppLanguage.BN) titleBn else titleEn, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = if (language == AppLanguage.BN) subtitleBn else subtitleEn, fontSize = 11.sp, color = TextTertiary)
            }
        }
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun EditProfileDialog(
    currentProfile: UserProfile,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (UserProfile) -> Unit
) {
    var name by remember { mutableStateOf(currentProfile.name) }
    var currentWeight by remember { mutableStateOf(currentProfile.currentWeightKg.toString()) }
    var goalWeight by remember { mutableStateOf(currentProfile.goalWeightKg.toString()) }
    var calories by remember { mutableStateOf(currentProfile.dailyCalorieGoal.toString()) }

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
                    text = if (language == AppLanguage.BN) "প্রোফাইল ও ফিটনেস লক্ষ্য" else "Edit Profile & Goals",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepForest
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (language == AppLanguage.BN) "নাম" else "Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = currentWeight,
                    onValueChange = { currentWeight = it },
                    label = { Text(if (language == AppLanguage.BN) "বর্তমান ওজন (কেজি)" else "Current Weight (kg)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = goalWeight,
                    onValueChange = { goalWeight = it },
                    label = { Text(if (language == AppLanguage.BN) "লক্ষ্যমাত্রা ওজন (কেজি)" else "Goal Weight (kg)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it },
                    label = { Text(if (language == AppLanguage.BN) "দৈনিক ক্যালরি টার্গেট" else "Daily Calorie Target") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        val w = currentWeight.toDoubleOrNull() ?: currentProfile.currentWeightKg
                        val gw = goalWeight.toDoubleOrNull() ?: currentProfile.goalWeightKg
                        val c = calories.toIntOrNull() ?: currentProfile.dailyCalorieGoal
                        val updated = currentProfile.copy(
                            name = name.ifBlank { currentProfile.name },
                            currentWeightKg = w,
                            goalWeightKg = gw,
                            dailyCalorieGoal = c
                        )
                        onSave(updated)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (language == AppLanguage.BN) "সংরক্ষণ করুন" else "Save Changes",
                        color = DeepForest,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}
