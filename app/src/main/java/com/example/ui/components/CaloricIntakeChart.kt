package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.DailyCalorieEntry
import com.example.ui.theme.*

@Composable
fun CaloricIntakeChartCard(
    entries: List<DailyCalorieEntry>,
    targetCalorieGoal: Int,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableStateOf(if (entries.isNotEmpty()) entries.size - 1 else -1) }
    var isWeeklyView by remember { mutableStateOf(true) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("caloric_intake_chart_card"),
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
            // Header with range switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = NeonLime,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (language == AppLanguage.BN) "দৈনিক ক্যালরি গ্রহণ ও বাজেট" else "Caloric Intake vs Goal",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = if (language == AppLanguage.BN)
                            "দৈনিক লক্ষ্য: $targetCalorieGoal kcal • টেকসই ঘাটতি"
                        else
                            "Daily Target: $targetCalorieGoal kcal • Healthy deficit",
                        fontSize = 12.sp,
                        color = MintSoft
                    )
                }

                // Range Toggle Pill
                Surface(
                    color = DeepForest,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ForestBorder)
                ) {
                    Row(modifier = Modifier.padding(2.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isWeeklyView) Emerald else Color.Transparent)
                                .clickable { isWeeklyView = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (language == AppLanguage.BN) "৭ দিন" else "7 Days",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isWeeklyView) DeepForest else TextTertiary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (!isWeeklyView) Emerald else Color.Transparent)
                                .clickable { isWeeklyView = false }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (language == AppLanguage.BN) "মাসিক গড়" else "Monthly",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!isWeeklyView) DeepForest else TextTertiary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stat summary chips
            val avgCalories = if (entries.isNotEmpty()) entries.map { it.caloriesConsumed }.average().toInt() else 0
            val compliantDays = entries.count { it.caloriesConsumed <= it.calorieGoal }
            val compliancePercent = if (entries.isNotEmpty()) (compliantDays * 100) / entries.size else 0

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalorieMetricChip(
                    labelBn = "সাপ্তাহিক গড়",
                    labelEn = "Weekly Avg",
                    value = "$avgCalories kcal",
                    valueColor = NeonLime,
                    language = language,
                    modifier = Modifier.weight(1f)
                )
                CalorieMetricChip(
                    labelBn = "লক্ষ্য পূরণ",
                    labelEn = "On Target",
                    value = "$compliancePercent%",
                    valueColor = Emerald,
                    language = language,
                    modifier = Modifier.weight(1f)
                )
                val deficit = targetCalorieGoal - avgCalories
                CalorieMetricChip(
                    labelBn = if (deficit >= 0) "গড় ঘাটতি" else "অতিরিক্ত",
                    labelEn = if (deficit >= 0) "Avg Deficit" else "Surplus",
                    value = "${if (deficit >= 0) "-" else "+"}${kotlin.math.abs(deficit)} kcal",
                    valueColor = if (deficit >= 0) MintSoft else StreakFire,
                    language = language,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Canvas Bar Chart
            val displayEntries = if (isWeeklyView) entries.takeLast(7) else entries
            if (selectedIndex !in displayEntries.indices && displayEntries.isNotEmpty()) {
                selectedIndex = displayEntries.lastIndex
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                if (displayEntries.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (language == AppLanguage.BN) "কোনো ক্যালরি ডেটা নেই" else "No caloric data logged",
                            color = TextTertiary,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(displayEntries) {
                                detectTapGestures { offset ->
                                    val paddingLeft = 70f
                                    val paddingRight = 30f
                                    val chartWidth = size.width - paddingLeft - paddingRight
                                    val count = displayEntries.size
                                    if (count > 0 && offset.x >= paddingLeft && offset.x <= size.width - paddingRight) {
                                        val barSlotWidth = chartWidth / count
                                        val idx = ((offset.x - paddingLeft) / barSlotWidth).toInt().coerceIn(0, count - 1)
                                        selectedIndex = idx
                                    }
                                }
                            }
                    ) {
                        val width = size.width
                        val height = size.height
                        val paddingLeft = 75f
                        val paddingRight = 30f
                        val paddingTop = 28f
                        val paddingBottom = 48f

                        val chartWidth = width - paddingLeft - paddingRight
                        val chartHeight = height - paddingTop - paddingBottom

                        val maxCalorieScale = 2600f
                        val minCalorieScale = 0f
                        val range = maxCalorieScale - minCalorieScale

                        // Draw Grid lines & Y Labels
                        val ySteps = listOf(2500, 2000, 1500, 1000, 500)
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.argb(140, 165, 243, 209)
                            textSize = 24f
                            textAlign = android.graphics.Paint.Align.RIGHT
                            isAntiAlias = true
                        }

                        ySteps.forEach { stepVal ->
                            val yFrac = (stepVal - minCalorieScale) / range
                            val yPos = paddingTop + chartHeight * (1f - yFrac)

                            drawLine(
                                color = Color(0xFF1B4034),
                                start = Offset(paddingLeft, yPos),
                                end = Offset(width - paddingRight, yPos),
                                strokeWidth = 1.dp.toPx()
                            )

                            drawContext.canvas.nativeCanvas.drawText(
                                "$stepVal",
                                paddingLeft - 12f,
                                yPos + 8f,
                                paint
                            )
                        }

                        // Target Goal Line (Dotted Gold / Emerald)
                        val goalFrac = (targetCalorieGoal - minCalorieScale) / range
                        val goalY = paddingTop + chartHeight * (1f - goalFrac)

                        // Draw dashed line for goal
                        val dashWidth = 14f
                        val gapWidth = 10f
                        var startX = paddingLeft
                        while (startX < width - paddingRight) {
                            val endX = (startX + dashWidth).coerceAtMost(width - paddingRight)
                            drawLine(
                                color = NeonLime.copy(alpha = 0.85f),
                                start = Offset(startX, goalY),
                                end = Offset(endX, goalY),
                                strokeWidth = 1.5.dp.toPx()
                            )
                            startX += dashWidth + gapWidth
                        }

                        // Goal badge text on right
                        val goalTextPaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.argb(230, 212, 255, 104)
                            textSize = 22f
                            textAlign = android.graphics.Paint.Align.RIGHT
                            isFakeBoldText = true
                            isAntiAlias = true
                        }
                        val goalLabel = if (language == AppLanguage.BN) "লক্ষ্য $targetCalorieGoal" else "Goal $targetCalorieGoal"
                        drawContext.canvas.nativeCanvas.drawText(
                            goalLabel,
                            width - paddingRight,
                            goalY - 8f,
                            goalTextPaint
                        )

                        // Render Bars
                        val count = displayEntries.size
                        val slotWidth = chartWidth / count
                        val barWidth = (slotWidth * 0.58f).coerceAtMost(36.dp.toPx()).coerceAtLeast(18.dp.toPx())

                        val labelPaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = 23f
                            textAlign = android.graphics.Paint.Align.CENTER
                            isAntiAlias = true
                        }

                        val datePaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.argb(180, 165, 243, 209)
                            textSize = 19f
                            textAlign = android.graphics.Paint.Align.CENTER
                            isAntiAlias = true
                        }

                        displayEntries.forEachIndexed { idx, item ->
                            val centerX = paddingLeft + (idx + 0.5f) * slotWidth
                            val barLeft = centerX - barWidth / 2
                            val frac = (item.caloriesConsumed.toFloat() / range).coerceIn(0.04f, 1f)
                            val barTop = paddingTop + chartHeight * (1f - frac)
                            val barHeight = chartHeight * frac

                            val isSelected = idx == selectedIndex

                            // Bar color gradient based on goal compliance
                            val colors = when {
                                item.caloriesConsumed > item.calorieGoal -> listOf(StreakFire, Color(0xFFFB923C))
                                item.caloriesConsumed >= item.calorieGoal * 0.90 -> listOf(NeonLime, Emerald)
                                else -> listOf(Emerald, MintSoft)
                            }

                            // Selection halo
                            if (isSelected) {
                                drawRoundRect(
                                    color = NeonLime.copy(alpha = 0.22f),
                                    topLeft = Offset(barLeft - 4.dp.toPx(), barTop - 4.dp.toPx()),
                                    size = Size(barWidth + 8.dp.toPx(), barHeight + 4.dp.toPx()),
                                    cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
                                )
                                drawRoundRect(
                                    color = NeonLime,
                                    topLeft = Offset(barLeft - 2.dp.toPx(), barTop - 2.dp.toPx()),
                                    size = Size(barWidth + 4.dp.toPx(), barHeight + 2.dp.toPx()),
                                    cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()),
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }

                            // Bar body with rounded top corners
                            val barPath = Path().apply {
                                addRoundRect(
                                    RoundRect(
                                        left = barLeft,
                                        top = barTop,
                                        right = barLeft + barWidth,
                                        bottom = paddingTop + chartHeight,
                                        topLeftCornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                                        topRightCornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                                        bottomLeftCornerRadius = CornerRadius.Zero,
                                        bottomRightCornerRadius = CornerRadius.Zero
                                    )
                                )
                            }

                            drawPath(
                                path = barPath,
                                brush = Brush.verticalGradient(
                                    colors = colors,
                                    startY = barTop,
                                    endY = paddingTop + chartHeight
                                )
                            )

                            // "Today" star or small dot indicator
                            if (item.isToday) {
                                drawCircle(
                                    color = NeonLime,
                                    radius = 3.dp.toPx(),
                                    center = Offset(centerX, barTop - 7.dp.toPx())
                                )
                            }

                            // X Axis Labels (Day label + Date)
                            val dayText = if (language == AppLanguage.BN) item.dayLabelBn else item.dayLabelEn
                            drawContext.canvas.nativeCanvas.drawText(
                                dayText,
                                centerX,
                                height - 24f,
                                if (isSelected || item.isToday) {
                                    android.graphics.Paint(labelPaint).apply {
                                        color = android.graphics.Color.argb(255, 212, 255, 104)
                                        isFakeBoldText = true
                                    }
                                } else labelPaint
                            )

                            val dateText = item.dateLabelEn.split(" ").lastOrNull() ?: ""
                            drawContext.canvas.nativeCanvas.drawText(
                                dateText,
                                centerX,
                                height - 4f,
                                datePaint
                            )
                        }
                    }
                }
            }

            // Interactive inspection detail card
            if (selectedIndex in displayEntries.indices) {
                val selected = displayEntries[selectedIndex]
                val diffFromGoal = selected.caloriesConsumed - selected.calorieGoal
                val isDeficit = diffFromGoal <= 0

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    color = DeepForest,
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ForestBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (language == AppLanguage.BN)
                                        "${selected.dayLabelBn}, ${selected.dateLabelBn}"
                                    else
                                        "${selected.dayLabelEn}, ${selected.dateLabelEn}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                if (selected.isToday) {
                                    Box(
                                        modifier = Modifier
                                            .background(NeonLime, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (language == AppLanguage.BN) "আজকের লাইভ" else "Today Live",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = DeepForest
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "${selected.caloriesConsumed} kcal",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isDeficit) NeonLime else StreakFire
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (language == AppLanguage.BN) {
                                if (isDeficit) "লক্ষ্যমাত্রার চেয়ে ${kotlin.math.abs(diffFromGoal)} kcal কম (নিরাপদ ফ্যাট-বার্ন বাজেট) ✓"
                                else "লক্ষ্যমাত্রার চেয়ে $diffFromGoal kcal বেশি (পরিমিত ব্যায়াম প্রয়োজন)"
                            } else {
                                if (isDeficit) "${kotlin.math.abs(diffFromGoal)} kcal under target (safe fat-burn zone) ✓"
                                else "$diffFromGoal kcal over target (light activity advised)"
                            },
                            fontSize = 11.sp,
                            color = if (isDeficit) MintSoft else Color(0xFFFCA5A5)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Macros Row: Protein, Carbs, Fat
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MacroNutrientBadge(
                                nameBn = "প্রোটিন",
                                nameEn = "Protein",
                                grams = selected.proteinGrams,
                                color = ProteinBlue,
                                language = language,
                                modifier = Modifier.weight(1f)
                            )
                            MacroNutrientBadge(
                                nameBn = "কার্বস",
                                nameEn = "Carbs",
                                grams = selected.carbsGrams,
                                color = CarbsAmber,
                                language = language,
                                modifier = Modifier.weight(1f)
                            )
                            MacroNutrientBadge(
                                nameBn = "ফ্যাট",
                                nameEn = "Fat",
                                grams = selected.fatGrams,
                                color = FatViolet,
                                language = language,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalorieMetricChip(
    labelBn: String,
    labelEn: String,
    value: String,
    valueColor: Color,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DeepForest,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ForestBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (language == AppLanguage.BN) labelBn else labelEn,
                fontSize = 10.sp,
                color = TextTertiary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = valueColor
            )
        }
    }
}

@Composable
private fun MacroNutrientBadge(
    nameBn: String,
    nameEn: String,
    grams: Double,
    color: Color,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    Surface(
        color = ForestCardElevated,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = if (language == AppLanguage.BN) nameBn else nameEn,
                    fontSize = 9.sp,
                    color = TextTertiary
                )
                Text(
                    text = "%.0fg".format(grams),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
