package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import com.example.data.datasource.FitnessDataSource
import com.example.data.model.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddMealDialog(
    initialMealType: MealType = MealType.BREAKFAST,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onMealAdded: (MealLog) -> Unit
) {
    var selectedMealType by remember { mutableStateOf(initialMealType) }
    var selectedFood by remember { mutableStateOf<FoodItem?>(FitnessDataSource.bangladeshiFoods.first()) }
    var customName by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("1.0") }
    var isCustomFood by remember { mutableStateOf(false) }

    val currentQty = quantityText.toDoubleOrNull() ?: 1.0
    val calculatedNutrients = remember(selectedFood, currentQty, isCustomFood) {
        if (!isCustomFood && selectedFood != null) {
            selectedFood!!.calculate(currentQty)
        } else {
            // Rough estimation for custom
            Nutrients(
                calories = (currentQty * 150).toInt(),
                protein = currentQty * 5.0,
                carbs = currentQty * 20.0,
                fat = currentQty * 3.0,
                fiber = currentQty * 2.0
            )
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(24.dp),
            color = SurfaceCard,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == AppLanguage.BN) "+ খাবার যোগ করুন" else "+ Log Meal",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestDark
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Meal Type Selector Pills
                Text(
                    text = if (language == AppLanguage.BN) "খাবারের সময়:" else "Meal Type:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MealType.entries.forEach { type ->
                        val isSelected = selectedMealType == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) ForestGreen else BackgroundLight)
                                .clickable { selectedMealType = type }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (language == AppLanguage.BN) type.titleBn.split(" ").first() else type.titleEn,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mode switch: Catalog vs Custom
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { isCustomFood = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isCustomFood) EmeraldGreen else BackgroundLight,
                            contentColor = if (!isCustomFood) Color.White else TextSecondary
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (language == AppLanguage.BN) "তালিকা থেকে" else "From Catalog",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Button(
                        onClick = { isCustomFood = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCustomFood) EmeraldGreen else BackgroundLight,
                            contentColor = if (isCustomFood) Color.White else TextSecondary
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (language == AppLanguage.BN) "নতুন লিখুন" else "Custom Food",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (!isCustomFood) {
                    Text(
                        text = if (language == AppLanguage.BN) "বাংলাদেশী খাবার নির্বাচন করুন:" else "Select Bangladeshi Food:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(FitnessDataSource.bangladeshiFoods) { food ->
                            val isSelected = selectedFood?.id == food.id
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MintGlow else BackgroundLight,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) EmeraldGreen else BorderLight
                                ),
                                modifier = Modifier.clickable { selectedFood = food }
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = if (language == AppLanguage.BN) food.nameBn.split("(").first() else food.nameEn,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) ForestGreen else TextPrimary
                                    )
                                    Text(
                                        text = "%.0f kcal / %s".format(food.caloriesPerUnit, if (language == AppLanguage.BN) food.unit.labelBn else food.unit.labelEn),
                                        fontSize = 11.sp,
                                        color = TextTertiary
                                    )
                                }
                            }
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = customName,
                        onValueChange = { customName = it },
                        label = { Text(if (language == AppLanguage.BN) "খাবারের নাম" else "Food Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quantity input
                val unitLabel = if (!isCustomFood && selectedFood != null) {
                    if (language == AppLanguage.BN) selectedFood!!.unit.labelBn else selectedFood!!.unit.labelEn
                } else if (language == AppLanguage.BN) "পরিমাণ / সার্ভিং" else "Serving"

                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { quantityText = it },
                    label = { Text(if (language == AppLanguage.BN) "পরিমাণ ($unitLabel)" else "Quantity ($unitLabel)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Nutrient Summary Preview Card
                Surface(
                    color = BackgroundLight,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (language == AppLanguage.BN) "আনুমানিক পুষ্টিমান:" else "Estimated Nutrients:",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${calculatedNutrients.calories} kcal",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${if (language == AppLanguage.BN) "প্রোটিন" else "Protein"}: %.1fg".format(calculatedNutrients.protein),
                                fontSize = 12.sp,
                                color = ProteinBlue,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${if (language == AppLanguage.BN) "কার্বস" else "Carbs"}: %.1fg".format(calculatedNutrients.carbs),
                                fontSize = 12.sp,
                                color = CarbsOrange,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${if (language == AppLanguage.BN) "ফ্যাট" else "Fat"}: %.1fg".format(calculatedNutrients.fat),
                                fontSize = 12.sp,
                                color = FatPurple,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit button
                Button(
                    onClick = {
                        val foodNameBn = if (!isCustomFood && selectedFood != null) selectedFood!!.nameBn else customName.ifBlank { "হালকা খাবার" }
                        val foodNameEn = if (!isCustomFood && selectedFood != null) selectedFood!!.nameEn else customName.ifBlank { "Custom Meal" }
                        val servingBn = "$currentQty $unitLabel"
                        val servingEn = "$currentQty $unitLabel"
                        val timeStr = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date())

                        val newMeal = MealLog(
                            id = "meal_${System.currentTimeMillis()}",
                            mealType = selectedMealType,
                            foodNameBn = foodNameBn,
                            foodNameEn = foodNameEn,
                            emoji = if (!isCustomFood && selectedFood != null) selectedFood!!.emoji else "🍲",
                            servingDescBn = servingBn,
                            servingDescEn = servingEn,
                            calories = calculatedNutrients.calories,
                            protein = calculatedNutrients.protein,
                            carbs = calculatedNutrients.carbs,
                            fat = calculatedNutrients.fat,
                            timestamp = timeStr
                        )
                        onMealAdded(newMeal)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_add_meal"),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (language == AppLanguage.BN) "লগ সংরক্ষণ করুন" else "Save Meal",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
