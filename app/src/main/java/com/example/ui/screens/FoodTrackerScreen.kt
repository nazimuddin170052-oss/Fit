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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Search
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
fun FoodTrackerScreen(
    loggedMeals: List<MealLog>,
    calorieGoal: Int,
    language: AppLanguage,
    onAddMealClick: () -> Unit,
    onScanFoodClick: () -> Unit,
    onOpenCalculatorClick: () -> Unit,
    onRemoveMeal: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(FoodCategory.ALL) }

    val totalCalories = loggedMeals.sumOf { it.calories }
    val progressPercentage = if (calorieGoal > 0) ((totalCalories.toFloat() / calorieGoal.toFloat()) * 100).toInt() else 0

    val filteredFoods = remember(searchQuery, selectedCategory) {
        FitnessDataSource.bangladeshiFoods.filter { food ->
            val matchesCategory = when (selectedCategory) {
                FoodCategory.ALL -> true
                FoodCategory.BANGLADESHI -> food.isBangladeshi
                else -> food.category == selectedCategory
            }
            val matchesQuery = searchQuery.isBlank() ||
                    food.nameBn.contains(searchQuery, ignoreCase = true) ||
                    food.nameEn.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search & AI Scanner Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = if (language == AppLanguage.BN) "খাবার খুঁজুন (ভাত, মাছ, ডিম...)" else "Search food (rice, fish, egg...)",
                            fontSize = 13.sp,
                            color = TextTertiary
                        )
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = TextSecondary)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("search_food_input"),
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceCard,
                        unfocusedContainerColor = SurfaceCard,
                        focusedBorderColor = Emerald,
                        unfocusedBorderColor = BorderLight
                    ),
                    singleLine = true
                )

                // Camera Scanner
                IconButton(
                    onClick = onScanFoodClick,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Emerald)
                        .testTag("scan_food_camera_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Scan food with AI camera",
                        tint = DeepForest
                    )
                }

                // Nutrition Calculator
                IconButton(
                    onClick = onOpenCalculatorClick,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(DeepForest)
                        .testTag("nutrition_calc_shortcut_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = "Nutrition Calculator",
                        tint = NeonLime
                    )
                }
            }
        }

        // Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(FoodCategory.entries) { category ->
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

        // Total Daily Summary Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DeepForest),
                border = androidx.compose.foundation.BorderStroke(1.dp, ForestBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (language == AppLanguage.BN) "আজকের মোট গৃহীত ক্যালরি" else "Total Calories Today",
                            fontSize = 12.sp,
                            color = MintSoft
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "%,d / %,d kcal".format(totalCalories, calorieGoal),
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Emerald)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "$progressPercentage%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DeepForest
                        )
                    }
                }
            }
        }

        // Today's Meals Section with "+ Add Meal"
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == AppLanguage.BN) "আজকের লগ করা খাবার" else "Logged Meals Today",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepForest
                )
                Button(
                    onClick = onAddMealClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("add_meal_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = DeepForest, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (language == AppLanguage.BN) "খাবার যোগ" else "Add Meal",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DeepForest
                    )
                }
            }
        }

        if (loggedMeals.isEmpty()) {
            item {
                Surface(
                    color = SurfaceCard,
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (language == AppLanguage.BN) "আজ এখনও কোনো খাবার যোগ করা হয়নি।" else "No meals logged yet today.",
                            color = TextTertiary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(loggedMeals) { meal ->
                TimelineMealItem(
                    meal = meal,
                    language = language,
                    onRemove = { onRemoveMeal(meal.id) }
                )
            }
        }

        // Bangladeshi Foods Directory
        item {
            Column {
                Text(
                    text = if (language == AppLanguage.BN) "বাংলাদেশী পুষ্টি তথ্যতালিকা" else "Bangladeshi Foods Directory",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepForest
                )
                Text(
                    text = if (language == AppLanguage.BN) "জাতীয় পুষ্টি গবেষণা অনুযায়ী স্ট্যান্ডার্ড মান" else "Standard nutritional averages",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        items(filteredFoods) { food ->
            FoodDirectoryCard(food = food, language = language)
        }
    }
}

@Composable
private fun FoodDirectoryCard(food: FoodItem, language: AppLanguage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MintGlow),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = food.emoji, fontSize = 22.sp)
                    }

                    Column {
                        Text(
                            text = if (language == AppLanguage.BN) food.nameBn else food.nameEn,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (language == AppLanguage.BN) food.defaultServingBn else food.defaultServingEn,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                Text(
                    text = "%.0f kcal".format(food.caloriesPerUnit),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DeepForest
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Macro pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MacroChip(label = "P", amount = "%.1fg".format(food.proteinPerUnit), color = ProteinBlue)
                MacroChip(label = "C", amount = "%.1fg".format(food.carbsPerUnit), color = CarbsAmber)
                MacroChip(label = "F", amount = "%.1fg".format(food.fatPerUnit), color = FatViolet)
                MacroChip(label = "Fiber", amount = "%.1fg".format(food.fiberPerUnit), color = Emerald)
            }

            if (food.noteBn.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (language == AppLanguage.BN) food.noteBn else food.noteEn,
                    fontSize = 11.sp,
                    color = TextTertiary,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun MacroChip(label: String, amount: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = amount, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
        }
    }
}
