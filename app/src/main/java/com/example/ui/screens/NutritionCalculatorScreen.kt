package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
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
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun NutritionCalculatorScreen(
    language: AppLanguage,
    onBack: () -> Unit,
    onLogMeal: (MealLog) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFood by remember { mutableStateOf(FitnessDataSource.bangladeshiFoods.first()) }
    var quantityText by remember { mutableStateOf("1.0") }
    var selectedUnit by remember { mutableStateOf(selectedFood.unit) }

    LaunchedEffect(selectedFood) {
        selectedUnit = selectedFood.unit
    }

    val quantity = quantityText.toDoubleOrNull() ?: 1.0
    val nutrients = remember(selectedFood, quantity) {
        selectedFood.calculate(quantity)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ForestDark)
                }
                Column {
                    Text(
                        text = if (language == AppLanguage.BN) "পুষ্টিমান ক্যালকুলেটর" else "Nutrition Calculator",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestDark
                    )
                    Text(
                        text = if (language == AppLanguage.BN) "খাবারের পরিমাণ অনুযায়ী পুষ্টি হিসাব করুন" else "Calculate nutrients by serving size",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Food Selection Carousel
        item {
            Text(
                text = if (language == AppLanguage.BN) "খাবার নির্বাচন করুন:" else "Select Food Item:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = ForestDark
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(FitnessDataSource.bangladeshiFoods) { food ->
                    val isSelected = selectedFood.id == food.id
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) ForestGreen else SurfaceCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) EmeraldGreen else BorderLight),
                        modifier = Modifier
                            .clickable { selectedFood = food }
                            .testTag("calc_food_${food.id}")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (language == AppLanguage.BN) food.nameBn.split("(").first() else food.nameEn,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "%.0f kcal".format(food.caloriesPerUnit),
                                fontSize = 11.sp,
                                color = if (isSelected) MintSoft else TextTertiary
                            )
                        }
                    }
                }
            }
        }

        // Serving Size Input Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (language == AppLanguage.BN) "সার্ভিং পরিমাণ ও একক" else "Serving Size & Quantity",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = quantityText,
                            onValueChange = { quantityText = it },
                            label = { Text(if (language == AppLanguage.BN) "পরিমাণ" else "Quantity") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("calc_qty_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        // Unit display badge
                        Surface(
                            color = BackgroundLight,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            Text(
                                text = if (language == AppLanguage.BN) selectedFood.unit.labelBn else selectedFood.unit.labelEn,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${if (language == AppLanguage.BN) "স্ট্যান্ডার্ড পরিমাণ:" else "Standard Serving:"} ${if (language == AppLanguage.BN) selectedFood.defaultServingBn else selectedFood.defaultServingEn}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Live Calculated Nutrition Results Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = ForestDark)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (language == AppLanguage.BN) "মোট আনুমানিক শক্তি" else "Total Energy",
                                fontSize = 12.sp,
                                color = MintSoft
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${nutrients.calories} kcal",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = EmeraldGreen,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4-Macro Grid
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ForestCard, RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MacroColumn(titleBn = "প্রোটিন", titleEn = "Protein", value = "%.1fg".format(nutrients.protein), color = ProteinBlue, language = language)
                        MacroColumn(titleBn = "কার্বস", titleEn = "Carbs", value = "%.1fg".format(nutrients.carbs), color = CarbsOrange, language = language)
                        MacroColumn(titleBn = "ফ্যাট", titleEn = "Fat", value = "%.1fg".format(nutrients.fat), color = FatPurple, language = language)
                        MacroColumn(titleBn = "ফাইবার", titleEn = "Fiber", value = "%.1fg".format(nutrients.fiber), color = EmeraldGreen, language = language)
                    }
                }
            }
        }

        // Log this item button
        item {
            Button(
                onClick = {
                    val unitLabel = if (language == AppLanguage.BN) selectedFood.unit.labelBn else selectedFood.unit.labelEn
                    val log = MealLog(
                        id = "calc_meal_${System.currentTimeMillis()}",
                        mealType = MealType.LUNCH,
                        foodNameBn = selectedFood.nameBn,
                        foodNameEn = selectedFood.nameEn,
                        emoji = selectedFood.emoji,
                        servingDescBn = "$quantity $unitLabel",
                        servingDescEn = "$quantity $unitLabel",
                        calories = nutrients.calories,
                        protein = nutrients.protein,
                        carbs = nutrients.carbs,
                        fat = nutrients.fat,
                        timestamp = "Just now"
                    )
                    onLogMeal(log)
                    onBack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("calc_log_meal_button")
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (language == AppLanguage.BN) "দৈনিক লগে এই খাবার যোগ করুন" else "Log This Food to Daily Meals",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        // Scientific Disclaimer Card
        item {
            Surface(
                color = BackgroundLight,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(18.dp))
                    Text(
                        text = if (language == AppLanguage.BN)
                            "বিজ্ঞপ্তি: এখানে প্রদর্শিত ক্যালরি ও পুষ্টিমান বাংলাদেশ পুষ্টি গবেষণা কাউন্সিল (BIRDEM) ও USDA ডেটাবেসের গড় মান। রান্নার তেলের পরিমাণ বা প্রস্তুতপ্রণালীর ভিন্নতায় মান কিছুটা পরিবর্তিত হতে পারে।"
                        else
                            "Notice: Displayed nutrition values are standard averages from national food composition tables. Actual values vary based on cooking method and oil used.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun MacroColumn(
    titleBn: String,
    titleEn: String,
    value: String,
    color: Color,
    language: AppLanguage
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = if (language == AppLanguage.BN) titleBn else titleEn,
            fontSize = 11.sp,
            color = TextTertiary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
