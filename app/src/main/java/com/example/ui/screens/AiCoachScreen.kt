package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Warning
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
import com.example.data.datasource.FitnessDataSource
import com.example.data.model.AppLanguage
import com.example.data.model.ChatMessage
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AiCoachScreen(
    chatMessages: List<ChatMessage>,
    language: AppLanguage,
    onBack: (() -> Unit)? = null,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    var showWeeklyPlanDialog by remember { mutableStateOf(false) }
    var isSimulatedLoading by remember { mutableStateOf(false) }
    var isSimulatedError by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Glowing orb animation
    val infiniteTransition = rememberInfiniteTransition(label = "orbGlow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val quickQuestions = remember {
        listOf(
            "আজ আমি কত ক্যালরি খেয়েছি?" to "How many calories did I eat today?",
            "আমি কি রাতে রুটি খেতে পারি?" to "Can I eat roti at night?",
            "ওজন কমানোর জন্য সহজ ডায়েট প্ল্যান দাও" to "Give me an easy diet plan for weight loss",
            "হাঁটার সেরা সময় কোনটি?" to "What is the best time for walking?"
        )
    }

    LaunchedEffect(chatMessages.size, isSimulatedLoading) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size + 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // AI Coach Header with Back button & Status
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = DeepForest,
            tonalElevation = 6.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, ForestBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (onBack != null) {
                            IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                        }

                        // Refined Glowing Assistant Symbol
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(ForestCard)
                                .border(1.5.dp, Emerald, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = NeonLime,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = if (language == AppLanguage.BN) "ZUKI AI ফিটনেস কোচ" else "ZUKI AI Fitness Coach",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Box(
                                    modifier = Modifier
                                        .background(Emerald.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "Active", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Emerald)
                                }
                            }
                            Text(
                                text = if (language == AppLanguage.BN) "দেশি পুষ্টি ও নিরাপদ স্বাস্থ্য বিশেষজ্ঞ" else "Bangladeshi Nutrition Specialist",
                                fontSize = 11.sp,
                                color = MintSoft
                            )
                        }
                    }

                    // 7-Day Plan Table Button
                    IconButton(
                        onClick = { showWeeklyPlanDialog = true },
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(ForestCard)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TableChart,
                            contentDescription = "7-Day Sample Plan",
                            tint = NeonLime,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Backend Security Banner: Live AI requires configured secure backend
                Surface(
                    color = ForestCard,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ForestBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = Emerald, modifier = Modifier.size(14.dp))
                            Text(
                                text = if (language == AppLanguage.BN)
                                    "নিরাপদ মোড: অফলাইন সার্টিফাইড ডায়েটিশিয়ান মডেল সক্রিয়"
                                else
                                    "Secure Mode: Offline Certified Nutrition Engine Active",
                                fontSize = 10.sp,
                                color = MintSoft,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Text(
                            text = "Gemini Ready",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonLime
                        )
                    }
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Today's Coach Advice Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepForest),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ForestBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = NeonLime, modifier = Modifier.size(18.dp))
                            Text(
                                text = if (language == AppLanguage.BN) "আজকের জন্য কোচের পরামর্শ" else "Today's Coach Advice",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonLime
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        CoachAdviceRow(textBn = "আরও ১ গ্লাস পানি পান করুন (পর্যাপ্ত আর্দ্রতা বজায় রাখুন)", textEn = "Drink 1 more glass of water (stay hydrated)")
                        CoachAdviceRow(textBn = "সন্ধ্যার আগে ২০-৩০ মিনিট দ্রুত হাঁটুন", textEn = "Take a 20-30 min brisk walk before sunset")
                        CoachAdviceRow(textBn = "রাতের খাবার হালকা রাখুন (ডাল, সবজি ও সালাদ)", textEn = "Keep dinner light (dal, steamed greens & salad)")
                    }
                }
            }

            // Chat Messages
            items(chatMessages) { message ->
                ChatBubbleItem(message = message, language = language)
            }

            // Simulated Loading State Indicator
            if (isSimulatedLoading) {
                item {
                    Surface(
                        color = ForestCard,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.widthIn(max = 240.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Emerald,
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = if (language == AppLanguage.BN) "কোচ ভাবছেন..." else "Coach is thinking...",
                                fontSize = 12.sp,
                                color = MintSoft
                            )
                        }
                    }
                }
            }

            // Simulated Error State (if toggled)
            if (isSimulatedError) {
                item {
                    Surface(
                        color = Color(0xFF3B151E),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF6B6B), modifier = Modifier.size(18.dp))
                                Text(
                                    text = if (language == AppLanguage.BN) "সংযোগ সমস্যা। অফলাইন মডেল ব্যবহৃত হচ্ছে।" else "Network issue. Using offline expert fallback.",
                                    fontSize = 11.sp,
                                    color = Color.White
                                )
                            }
                            IconButton(onClick = { isSimulatedError = false }, modifier = Modifier.size(24.dp)) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Retry", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        // Suggested Questions Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickQuestions) { item ->
                val text = if (language == AppLanguage.BN) item.first else item.second
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    shadowElevation = 1.dp,
                    modifier = Modifier.clickable {
                        onSendMessage(text)
                    }
                ) {
                    Text(
                        text = text,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepForest,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // Clean Message Composer
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = SurfaceCard,
            tonalElevation = 8.dp,
            border = androidx.compose.foundation.BorderStroke(0.5.dp, BorderLight)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = if (language == AppLanguage.BN) "কোচকে খাবার বা ব্যায়াম নিয়ে প্রশ্ন করুন..." else "Ask Coach about food or exercises...",
                            fontSize = 13.sp,
                            color = TextTertiary
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_message_input"),
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BackgroundLight,
                        unfocusedContainerColor = BackgroundLight,
                        focusedBorderColor = Emerald,
                        unfocusedBorderColor = BorderLight
                    ),
                    maxLines = 3
                )

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            val textToSend = inputText
                            inputText = ""
                            coroutineScope.launch {
                                isSimulatedLoading = true
                                delay(300L)
                                onSendMessage(textToSend)
                                isSimulatedLoading = false
                                listState.animateScrollToItem(chatMessages.size + 1)
                            }
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Emerald)
                        .testTag("send_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = DeepForest,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }

    if (showWeeklyPlanDialog) {
        WeeklyPlanDialog(
            language = language,
            onDismiss = { showWeeklyPlanDialog = false }
        )
    }
}

@Composable
private fun CoachAdviceRow(textBn: String, textEn: String) {
    Row(
        modifier = Modifier.padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(Emerald),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = DeepForest, modifier = Modifier.size(12.dp))
        }
        Text(text = textBn, fontSize = 12.sp, color = Color.White)
    }
}

@Composable
private fun ChatBubbleItem(message: ChatMessage, language: AppLanguage) {
    val isUser = message.isUser
    val text = if (language == AppLanguage.BN) message.textBn else message.textEn

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            if (!isUser) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(DeepForest)
                        .border(1.dp, Emerald, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = NeonLime, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Surface(
                color = if (isUser) DeepForest else SurfaceCard,
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = if (isUser) 18.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 18.dp
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isUser) ForestBorder else BorderLight),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = text,
                        fontSize = 13.sp,
                        color = if (isUser) Color.White else TextPrimary,
                        lineHeight = 19.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = message.timestamp,
                        fontSize = 10.sp,
                        color = if (isUser) MintSoft.copy(alpha = 0.7f) else TextTertiary,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}

@Composable
private fun WeeklyPlanDialog(
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(22.dp),
            color = SurfaceCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = if (language == AppLanguage.BN) "স্যাম্পল ৭ দিনের ডায়েট ও ব্যায়াম প্ল্যান" else "Sample 7-Day Weight Loss Plan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepForest
                )
                Text(
                    text = if (language == AppLanguage.BN) "ZUKI Fit AI প্রস্তাবিত টেকসই উদাহরণ" else "ZUKI Fit AI Sustainable Example Plan",
                    fontSize = 11.sp,
                    color = TextTertiary
                )

                Spacer(modifier = Modifier.height(14.dp))

                FitnessDataSource.sample7DayPlan.forEach { day ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(BackgroundLight, RoundedCornerShape(10.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == AppLanguage.BN) "দিন ${day.dayBn}" else day.dayEn,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = DeepForest
                        )
                        Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                            Text(
                                text = if (language == AppLanguage.BN) day.foodBn else day.foodEn,
                                fontSize = 11.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "${if (language == AppLanguage.BN) day.exerciseBn else day.exerciseEn} • ${day.waterLiters}L",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                        Text(
                            text = "${day.calories} kcal",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (language == AppLanguage.BN) "বন্ধ করুন" else "Close", color = DeepForest, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
