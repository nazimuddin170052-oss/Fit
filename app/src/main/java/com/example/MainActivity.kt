package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AppLanguage
import com.example.data.model.NavigationTab
import com.example.ui.components.AddMealDialog
import com.example.ui.components.FoodScanModal
import com.example.ui.components.WorkoutPlayerDialog
import com.example.ui.components.ZukiBottomBar
import com.example.ui.components.ZukiTopBar
import com.example.ui.screens.*
import com.example.ui.state.FitnessViewModel
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {

    private val viewModel: FitnessViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZukiFitTheme {
                ZukiFitApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ZukiFitApp(viewModel: FitnessViewModel) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val loggedMeals by viewModel.loggedMeals.collectAsStateWithLifecycle()
    val waterLiters by viewModel.waterIntakeLiters.collectAsStateWithLifecycle()
    val steps by viewModel.currentSteps.collectAsStateWithLifecycle()
    val progressHistory by viewModel.progressHistory.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val caloricHistory by viewModel.caloricHistory.collectAsStateWithLifecycle()

    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val isCalculatorOpen by viewModel.isCalculatorOpen.collectAsStateWithLifecycle()
    val isAiCoachOpen by viewModel.isAiCoachOpen.collectAsStateWithLifecycle()
    val showAddMealModal by viewModel.showAddMealModal.collectAsStateWithLifecycle()
    val showFoodScanModal by viewModel.showFoodScanModal.collectAsStateWithLifecycle()
    val showAddWeightModal by viewModel.showAddWeightModal.collectAsStateWithLifecycle()
    val activeWorkout by viewModel.activeWorkoutForSession.collectAsStateWithLifecycle()

    // Handle back button
    BackHandler(enabled = isCalculatorOpen || isAiCoachOpen || currentTab != NavigationTab.HOME) {
        when {
            isCalculatorOpen -> viewModel.closeCalculator()
            isAiCoachOpen -> viewModel.closeAiCoach()
            else -> viewModel.selectTab(NavigationTab.HOME)
        }
    }

    val topBarTitleBn = when {
        isAiCoachOpen -> "ZUKI AI কোচ"
        isCalculatorOpen -> "পুষ্টি ক্যালকুলেটর"
        currentTab == NavigationTab.HOME -> "ZUKI Fit AI"
        currentTab == NavigationTab.FOOD -> "খাবার ও ক্যালরি"
        currentTab == NavigationTab.WORKOUT -> "ওয়ার্কআউট লাইব্রেরি"
        currentTab == NavigationTab.PROGRESS -> "শারীরিক অগ্রগতি"
        currentTab == NavigationTab.PROFILE -> "প্রোফাইল ও সেটিংস"
        else -> "ZUKI Fit AI"
    }

    val topBarTitleEn = when {
        isAiCoachOpen -> "ZUKI AI Coach"
        isCalculatorOpen -> "Nutrition Calculator"
        currentTab == NavigationTab.HOME -> "ZUKI Fit AI"
        currentTab == NavigationTab.FOOD -> "Food & Calories"
        currentTab == NavigationTab.WORKOUT -> "Workout Library"
        currentTab == NavigationTab.PROGRESS -> "Body Progress"
        currentTab == NavigationTab.PROFILE -> "Profile & Settings"
        else -> "ZUKI Fit AI"
    }

    val topBarSubtitleBn = when {
        isAiCoachOpen -> "ব্যক্তিগত পুষ্টি ও ফিটনেস সহকারী"
        isCalculatorOpen -> "খাবারের সঠিক পুষ্টিমান হিসাব"
        currentTab == NavigationTab.HOME -> "সুস্থ শরীর, সুন্দর জীবন"
        currentTab == NavigationTab.FOOD -> "দেশি খাবারের পুষ্টিমান ট্র্যাকার"
        currentTab == NavigationTab.WORKOUT -> "ঘরে বসে ফ্যাট লস ও ফিটনেস"
        currentTab == NavigationTab.PROGRESS -> "ওজন ও কোমর ট্র্যাকিং"
        currentTab == NavigationTab.PROFILE -> "ব্যবহারকারীর তথ্য ও সেটিংস"
        else -> "সুস্থ শরীর, সুন্দর জীবন"
    }

    val topBarSubtitleEn = when {
        isAiCoachOpen -> "Personal Nutrition & Fitness Companion"
        isCalculatorOpen -> "Serving-size nutrient calculation"
        currentTab == NavigationTab.HOME -> "Healthy Body, Better You"
        currentTab == NavigationTab.FOOD -> "Bangladeshi Nutrition Tracker"
        currentTab == NavigationTab.WORKOUT -> "Home Workouts & Fat Loss"
        currentTab == NavigationTab.PROGRESS -> "Weight & Waist Tracking"
        currentTab == NavigationTab.PROFILE -> "Account & App Settings"
        else -> "Healthy Body, Better You"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ZukiTopBar(
                titleBn = topBarTitleBn,
                titleEn = topBarTitleEn,
                subtitleBn = topBarSubtitleBn,
                subtitleEn = topBarSubtitleEn,
                streakDays = userProfile.streakDays,
                language = language,
                onToggleLanguage = { viewModel.toggleLanguage() },
                onOpenAiCoach = { viewModel.openAiCoach() }
            )
        },
        bottomBar = {
            if (!isAiCoachOpen && !isCalculatorOpen) {
                ZukiBottomBar(
                    currentTab = currentTab,
                    onTabSelected = { viewModel.selectTab(it) },
                    language = language
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundLight)
        ) {
            when {
                isAiCoachOpen -> {
                    AiCoachScreen(
                        chatMessages = chatMessages,
                        language = language,
                        onBack = { viewModel.closeAiCoach() },
                        onSendMessage = { viewModel.sendChatMessage(it) }
                    )
                }

                isCalculatorOpen -> {
                    NutritionCalculatorScreen(
                        language = language,
                        onBack = { viewModel.closeCalculator() },
                        onLogMeal = { viewModel.addMeal(it) }
                    )
                }

                else -> {
                    when (currentTab) {
                        NavigationTab.HOME -> {
                            HomeScreen(
                                userProfile = userProfile,
                                loggedMeals = loggedMeals,
                                waterLiters = waterLiters,
                                steps = steps,
                                progressHistory = progressHistory,
                                language = language,
                                onNavigateTab = { viewModel.selectTab(it) },
                                onOpenAiCoach = { viewModel.openAiCoach() },
                                onAddMealClick = { viewModel.openAddMeal() },
                                onLogWeightClick = { viewModel.openAddWeight() },
                                onAddWater = { viewModel.addWater(it) },
                                onResetWater = { viewModel.resetWater() },
                                onAddSteps = { viewModel.addSteps(it) },
                                onStartWorkout = { viewModel.startWorkoutSession(it) },
                                onRemoveMeal = { viewModel.removeMeal(it) }
                            )
                        }

                        NavigationTab.FOOD -> {
                            FoodTrackerScreen(
                                loggedMeals = loggedMeals,
                                calorieGoal = userProfile.dailyCalorieGoal,
                                language = language,
                                onAddMealClick = { viewModel.openAddMeal() },
                                onScanFoodClick = { viewModel.openFoodScan() },
                                onOpenCalculatorClick = { viewModel.openCalculator() },
                                onRemoveMeal = { viewModel.removeMeal(it) }
                            )
                        }

                        NavigationTab.WORKOUT -> {
                            WorkoutScreen(
                                language = language,
                                onStartWorkout = { viewModel.startWorkoutSession(it) }
                            )
                        }

                        NavigationTab.PROGRESS -> {
                            ProgressScreen(
                                userProfile = userProfile,
                                progressHistory = progressHistory,
                                caloricHistory = caloricHistory,
                                waterLiters = waterLiters,
                                steps = steps,
                                language = language,
                                onAddProgress = { weight, waist, nBn, nEn ->
                                    viewModel.addProgress(weight, waist, nBn, nEn)
                                }
                            )
                        }

                        NavigationTab.PROFILE -> {
                            ProfileScreen(
                                userProfile = userProfile,
                                language = language,
                                onLanguageChange = { viewModel.setLanguage(it) },
                                onUpdateProfile = { viewModel.updateProfile(it) },
                                onResetDemoData = { viewModel.resetToDemoData() }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Overlays
    if (showAddMealModal) {
        AddMealDialog(
            language = language,
            onDismiss = { viewModel.closeAddMeal() },
            onMealAdded = { viewModel.addMeal(it) }
        )
    }

    if (showFoodScanModal) {
        FoodScanModal(
            language = language,
            onDismiss = { viewModel.closeFoodScan() },
            onMealLogged = { viewModel.addMeal(it) }
        )
    }

    if (showAddWeightModal) {
        QuickLogWeightDialog(
            language = language,
            onDismiss = { viewModel.closeAddWeight() },
            onSave = { weight, waist ->
                viewModel.addProgress(weight, waist, "ব্যবহারকারী ম্যানুয়াল এন্ট্রি", "User manual entry")
                viewModel.closeAddWeight()
            }
        )
    }

    activeWorkout?.let { workout ->
        WorkoutPlayerDialog(
            workout = workout,
            language = language,
            onDismiss = { viewModel.finishWorkoutSession(0) },
            onWorkoutCompleted = { burned ->
                viewModel.finishWorkoutSession(burned)
            }
        )
    }
}

@Composable
private fun QuickLogWeightDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (Double, Double) -> Unit
) {
    var weightInput by remember { mutableStateOf("74.0") }
    var waistInput by remember { mutableStateOf("33.0") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(22.dp),
            color = SurfaceCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = if (language == AppLanguage.BN) "ওজন ও কোমর লগ করুন" else "Log Weight & Waist",
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

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        val w = weightInput.toDoubleOrNull() ?: 74.0
                        val waist = waistInput.toDoubleOrNull() ?: 33.0
                        onSave(w, waist)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_quick_weight_log"),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (language == AppLanguage.BN) "লগ সংরক্ষণ করুন" else "Save Entry",
                        color = DeepForest,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}
