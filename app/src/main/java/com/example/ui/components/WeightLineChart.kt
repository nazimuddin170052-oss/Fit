package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
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
import com.example.data.model.ProgressEntry
import com.example.ui.theme.*

enum class ChartMetricMode {
    WEIGHT, WAIST, BOTH
}

@Composable
fun WeightLineChartCard(
    entries: List<ProgressEntry>,
    language: AppLanguage,
    goalWeightKg: Double = 65.0,
    modifier: Modifier = Modifier
) {
    var isMonthlyView by remember { mutableStateOf(false) }
    var metricMode by remember { mutableStateOf(ChartMetricMode.WEIGHT) }
    var selectedIndex by remember { mutableStateOf(if (entries.isNotEmpty()) entries.size - 1 else -1) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weight_progress_chart_card"),
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
            // Header with Weekly / Monthly switcher
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
                            imageVector = Icons.Default.Scale,
                            contentDescription = null,
                            tint = NeonLime,
                            modifier = Modifier.size(19.dp)
                        )
                        Text(
                            text = if (language == AppLanguage.BN) "ওজন ও কোমর ট্র্যাকার" else "Weight & Waist Trend",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = if (language == AppLanguage.BN) "WHO স্বীকৃত টেকসই সাপ্তাহিক গতি" else "WHO approved sustainable trajectory",
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
                                .background(if (!isMonthlyView) Emerald else Color.Transparent)
                                .clickable { isMonthlyView = false }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (language == AppLanguage.BN) "সাপ্তাহিক" else "Weekly",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!isMonthlyView) DeepForest else TextTertiary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isMonthlyView) Emerald else Color.Transparent)
                                .clickable { isMonthlyView = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (language == AppLanguage.BN) "মাসিক" else "Monthly",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isMonthlyView) DeepForest else TextTertiary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Metric Mode Selector (Weight / Waist / Both)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricSelectorPill(
                    labelBn = "ওজন (কেজি)",
                    labelEn = "Weight (kg)",
                    dotColor = NeonLime,
                    isSelected = metricMode == ChartMetricMode.WEIGHT,
                    onClick = { metricMode = ChartMetricMode.WEIGHT },
                    language = language,
                    modifier = Modifier.weight(1f)
                )
                MetricSelectorPill(
                    labelBn = "কোমর (ইঞ্চি)",
                    labelEn = "Waist (in)",
                    dotColor = WaterCyan,
                    isSelected = metricMode == ChartMetricMode.WAIST,
                    onClick = { metricMode = ChartMetricMode.WAIST },
                    language = language,
                    modifier = Modifier.weight(1f)
                )
                MetricSelectorPill(
                    labelBn = "উভয়",
                    labelEn = "Both",
                    dotColor = Emerald,
                    isSelected = metricMode == ChartMetricMode.BOTH,
                    onClick = { metricMode = ChartMetricMode.BOTH },
                    language = language,
                    modifier = Modifier.weight(0.8f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Summary Stats Strip (Current Weight, Total Loss, Remaining to Goal)
            val currentWeight = entries.lastOrNull()?.weightKg ?: 74.0
            val startWeight = entries.firstOrNull()?.weightKg ?: currentWeight
            val totalLoss = startWeight - currentWeight
            val remainingToGoal = (currentWeight - goalWeightKg).coerceAtLeast(0.0)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProgressMetricChip(
                    labelBn = "বর্তমান",
                    labelEn = "Current",
                    value = "%.1f kg".format(currentWeight),
                    valueColor = NeonLime,
                    language = language,
                    modifier = Modifier.weight(1f)
                )
                ProgressMetricChip(
                    labelBn = "মোট পরিবর্তন",
                    labelEn = "Total Lost",
                    value = "${if (totalLoss >= 0) "▼ " else "▲ "}%.1f kg".format(kotlin.math.abs(totalLoss)),
                    valueColor = if (totalLoss >= 0) Emerald else StreakFire,
                    language = language,
                    modifier = Modifier.weight(1f)
                )
                ProgressMetricChip(
                    labelBn = "লক্ষ্য বাকি",
                    labelEn = "To Goal",
                    value = "%.1f kg".format(remainingToGoal),
                    valueColor = MintSoft,
                    language = language,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Canvas Line Chart with dynamic points & touch scrubbing
            val displayPoints = if (!isMonthlyView) entries.takeLast(7) else entries
            if (selectedIndex !in displayPoints.indices && displayPoints.isNotEmpty()) {
                selectedIndex = displayPoints.lastIndex
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                if (displayPoints.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (language == AppLanguage.BN) "এখনও কোনো পরিমাপ যোগ করা হয়নি" else "No measurements logged yet",
                            color = TextTertiary,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(displayPoints) {
                                detectTapGestures { offset ->
                                    val paddingLeft = 70f
                                    val paddingRight = 35f
                                    val chartWidth = size.width - paddingLeft - paddingRight
                                    val count = displayPoints.size
                                    if (count > 0 && offset.x >= paddingLeft && offset.x <= size.width - paddingRight) {
                                        val slotWidth = if (count > 1) chartWidth / (count - 1) else chartWidth
                                        val idx = if (count > 1) {
                                            ((offset.x - paddingLeft + slotWidth / 2) / slotWidth).toInt().coerceIn(0, count - 1)
                                        } else 0
                                        selectedIndex = idx
                                    }
                                }
                            }
                    ) {
                        val width = size.width
                        val height = size.height
                        val paddingLeft = 75f
                        val paddingRight = 35f
                        val paddingTop = 26f
                        val paddingBottom = 48f

                        val chartWidth = width - paddingLeft - paddingRight
                        val chartHeight = height - paddingTop - paddingBottom

                        // Calculate dynamic scale
                        val weights = displayPoints.map { it.weightKg.toFloat() }
                        val waists = displayPoints.map { it.waistInches.toFloat() }

                        val minVal = when (metricMode) {
                            ChartMetricMode.WEIGHT -> (weights.minOrNull() ?: 70f) - 1.5f
                            ChartMetricMode.WAIST -> (waists.minOrNull() ?: 30f) - 1.5f
                            ChartMetricMode.BOTH -> (weights.minOrNull() ?: 70f) - 1.5f
                        }
                        val maxVal = when (metricMode) {
                            ChartMetricMode.WEIGHT -> (weights.maxOrNull() ?: 80f) + 1.5f
                            ChartMetricMode.WAIST -> (waists.maxOrNull() ?: 36f) + 1.5f
                            ChartMetricMode.BOTH -> (weights.maxOrNull() ?: 80f) + 1.5f
                        }
                        val valRange = (maxVal - minVal).coerceAtLeast(1f)

                        // Draw Grid lines & Y scale labels
                        val steps = 5
                        val stepSize = valRange / steps
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.argb(140, 165, 243, 209)
                            textSize = 24f
                            textAlign = android.graphics.Paint.Align.RIGHT
                            isAntiAlias = true
                        }

                        for (i in 0..steps) {
                            val curVal = maxVal - i * stepSize
                            val yFrac = i.toFloat() / steps
                            val yPos = paddingTop + chartHeight * yFrac

                            drawLine(
                                color = Color(0xFF1B4034),
                                start = Offset(paddingLeft, yPos),
                                end = Offset(width - paddingRight, yPos),
                                strokeWidth = 1.dp.toPx()
                            )

                            val labelStr = "%.1f".format(curVal)
                            drawContext.canvas.nativeCanvas.drawText(
                                labelStr,
                                paddingLeft - 10f,
                                yPos + 8f,
                                paint
                            )
                        }

                        // Goal Weight reference dashed line if within visible range or mode is WEIGHT
                        if (metricMode != ChartMetricMode.WAIST && goalWeightKg >= minVal && goalWeightKg <= maxVal) {
                            val goalFrac = ((goalWeightKg.toFloat() - minVal) / valRange).coerceIn(0f, 1f)
                            val goalY = paddingTop + chartHeight * (1f - goalFrac)

                            var curX = paddingLeft
                            val dashW = 12f
                            val gapW = 8f
                            while (curX < width - paddingRight) {
                                drawLine(
                                    color = Emerald.copy(alpha = 0.85f),
                                    start = Offset(curX, goalY),
                                    end = Offset((curX + dashW).coerceAtMost(width - paddingRight), goalY),
                                    strokeWidth = 1.5.dp.toPx()
                                )
                                curX += dashW + gapW
                            }

                            val goalPaint = android.graphics.Paint().apply {
                                color = android.graphics.Color.argb(230, 25, 216, 144)
                                textSize = 20f
                                textAlign = android.graphics.Paint.Align.RIGHT
                                isFakeBoldText = true
                                isAntiAlias = true
                            }
                            val goalText = if (language == AppLanguage.BN) "লক্ষ্য %.1f kg".format(goalWeightKg) else "Goal %.1f kg".format(goalWeightKg)
                            drawContext.canvas.nativeCanvas.drawText(
                                goalText,
                                width - paddingRight,
                                goalY - 6f,
                                goalPaint
                            )
                        }

                        // Coordinates calculation
                        val weightPoints = displayPoints.mapIndexed { index, entry ->
                            val xFrac = if (displayPoints.size > 1) index.toFloat() / (displayPoints.size - 1) else 0.5f
                            val xPos = paddingLeft + chartWidth * xFrac
                            val yFrac = ((entry.weightKg.toFloat() - minVal) / valRange).coerceIn(0f, 1f)
                            val yPos = paddingTop + chartHeight * (1f - yFrac)
                            Offset(xPos, yPos)
                        }

                        val waistMin = (waists.minOrNull() ?: 30f) - 1f
                        val waistMax = (waists.maxOrNull() ?: 36f) + 1f
                        val waistRange = (waistMax - waistMin).coerceAtLeast(1f)
                        val waistPoints = displayPoints.mapIndexed { index, entry ->
                            val xFrac = if (displayPoints.size > 1) index.toFloat() / (displayPoints.size - 1) else 0.5f
                            val xPos = paddingLeft + chartWidth * xFrac
                            val yFrac = if (metricMode == ChartMetricMode.WAIST) {
                                ((entry.waistInches.toFloat() - minVal) / valRange).coerceIn(0f, 1f)
                            } else {
                                ((entry.waistInches.toFloat() - waistMin) / waistRange).coerceIn(0f, 1f)
                            }
                            val yPos = paddingTop + chartHeight * (1f - yFrac)
                            Offset(xPos, yPos)
                        }

                        // Selected cursor indicator line
                        if (selectedIndex in displayPoints.indices) {
                            val selX = weightPoints[selectedIndex].x
                            drawLine(
                                color = NeonLime.copy(alpha = 0.5f),
                                start = Offset(selX, paddingTop),
                                end = Offset(selX, paddingTop + chartHeight),
                                strokeWidth = 1.5.dp.toPx()
                            )
                        }

                        // Render Weight Curve
                        if (metricMode == ChartMetricMode.WEIGHT || metricMode == ChartMetricMode.BOTH) {
                            if (weightPoints.size >= 2) {
                                val fillPath = Path().apply {
                                    moveTo(weightPoints.first().x, weightPoints.first().y)
                                    for (i in 1 until weightPoints.size) {
                                        val prev = weightPoints[i - 1]
                                        val curr = weightPoints[i]
                                        val midX = (prev.x + curr.x) / 2
                                        cubicTo(midX, prev.y, midX, curr.y, curr.x, curr.y)
                                    }
                                    lineTo(weightPoints.last().x, paddingTop + chartHeight)
                                    lineTo(weightPoints.first().x, paddingTop + chartHeight)
                                    close()
                                }
                                drawPath(
                                    path = fillPath,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(NeonLime.copy(alpha = 0.35f), Color.Transparent),
                                        startY = paddingTop,
                                        endY = paddingTop + chartHeight
                                    )
                                )

                                val strokePath = Path().apply {
                                    moveTo(weightPoints.first().x, weightPoints.first().y)
                                    for (i in 1 until weightPoints.size) {
                                        val prev = weightPoints[i - 1]
                                        val curr = weightPoints[i]
                                        val midX = (prev.x + curr.x) / 2
                                        cubicTo(midX, prev.y, midX, curr.y, curr.x, curr.y)
                                    }
                                }
                                drawPath(
                                    path = strokePath,
                                    color = NeonLime,
                                    style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }
                        }

                        // Render Waist Curve
                        if (metricMode == ChartMetricMode.WAIST || metricMode == ChartMetricMode.BOTH) {
                            if (waistPoints.size >= 2) {
                                val waistStroke = Path().apply {
                                    moveTo(waistPoints.first().x, waistPoints.first().y)
                                    for (i in 1 until waistPoints.size) {
                                        val prev = waistPoints[i - 1]
                                        val curr = waistPoints[i]
                                        val midX = (prev.x + curr.x) / 2
                                        cubicTo(midX, prev.y, midX, curr.y, curr.x, curr.y)
                                    }
                                }
                                drawPath(
                                    path = waistStroke,
                                    color = WaterCyan,
                                    style = Stroke(
                                        width = 2.5.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                )
                            }
                        }

                        // Draw Dots & Date Labels
                        val datePaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = 23f
                            textAlign = android.graphics.Paint.Align.CENTER
                            isAntiAlias = true
                        }

                        val activePoints = if (metricMode == ChartMetricMode.WAIST) waistPoints else weightPoints
                        activePoints.forEachIndexed { i, pt ->
                            val isSelected = i == selectedIndex

                            if (isSelected) {
                                drawCircle(
                                    color = NeonLime.copy(alpha = 0.28f),
                                    radius = 12.dp.toPx(),
                                    center = pt
                                )
                            }

                            drawCircle(
                                color = DeepForest,
                                radius = (if (isSelected) 7.dp else 5.dp).toPx(),
                                center = pt
                            )
                            drawCircle(
                                color = if (metricMode == ChartMetricMode.WAIST) WaterCyan else NeonLime,
                                radius = (if (isSelected) 5.dp else 3.5.dp).toPx(),
                                center = pt
                            )

                            val entry = displayPoints[i]
                            val label = if (language == AppLanguage.BN) entry.dateLabelBn.split(" ").first() else entry.dateLabelEn
                            drawContext.canvas.nativeCanvas.drawText(
                                label,
                                pt.x,
                                height - 12f,
                                if (isSelected) {
                                    android.graphics.Paint(datePaint).apply {
                                        color = android.graphics.Color.argb(255, 212, 255, 104)
                                        isFakeBoldText = true
                                    }
                                } else datePaint
                            )
                        }
                    }
                }
            }

            // Selected Point Details Card
            if (selectedIndex in displayPoints.indices) {
                val selected = displayPoints[selectedIndex]
                val prev = if (selectedIndex > 0) displayPoints[selectedIndex - 1] else null
                val diff = if (prev != null) selected.weightKg - prev.weightKg else 0.0

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    color = DeepForest,
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ForestBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (language == AppLanguage.BN) selected.dateLabelBn else selected.dateLabelEn,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Box(
                                    modifier = Modifier
                                        .background(if (selected.isSample) MintGlow.copy(alpha = 0.15f) else NeonLime.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (selected.isSample) {
                                            if (language == AppLanguage.BN) "নমুনা ডেটা" else "Demo Log"
                                        } else {
                                            if (language == AppLanguage.BN) "আপনার রেকর্ড" else "User Log"
                                        },
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected.isSample) MintSoft else NeonLime
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            val note = if (language == AppLanguage.BN) selected.notesBn else selected.notesEn
                            if (note.isNotBlank()) {
                                Text(text = note, fontSize = 11.sp, color = TextTertiary)
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "%.1f kg".format(selected.weightKg),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NeonLime
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "${if (language == AppLanguage.BN) "কোমর:" else "Waist:"} %.1f\"".format(selected.waistInches),
                                    fontSize = 11.sp,
                                    color = WaterCyan
                                )
                                if (prev != null) {
                                    Text(
                                        text = "(${if (diff <= 0) "▼ " else "▲ "}%.1f)".format(kotlin.math.abs(diff)),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (diff <= 0) Emerald else StreakFire
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricSelectorPill(
    labelBn: String,
    labelEn: String,
    dotColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isSelected) DeepForest else ForestCardElevated,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) dotColor else ForestBorder),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = if (language == AppLanguage.BN) labelBn else labelEn,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else TextTertiary
            )
        }
    }
}

@Composable
private fun ProgressMetricChip(
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
