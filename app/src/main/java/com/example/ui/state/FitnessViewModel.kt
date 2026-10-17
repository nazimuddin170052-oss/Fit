package com.example.ui.state

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.FitnessRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FitnessViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FitnessRepository(application)

    val language = repository.language
    val userProfile = repository.userProfile
    val loggedMeals = repository.loggedMeals
    val waterIntakeLiters = repository.waterIntakeLiters
    val currentSteps = repository.currentSteps
    val progressHistory = repository.progressHistory
    val chatMessages = repository.chatMessages
    val caloricHistory = repository.caloricHistory

    private val _currentTab = MutableStateFlow(NavigationTab.HOME)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    private val _isCalculatorOpen = MutableStateFlow(false)
    val isCalculatorOpen: StateFlow<Boolean> = _isCalculatorOpen.asStateFlow()

    private val _isAiCoachOpen = MutableStateFlow(false)
    val isAiCoachOpen: StateFlow<Boolean> = _isAiCoachOpen.asStateFlow()

    private val _showAddMealModal = MutableStateFlow(false)
    val showAddMealModal: StateFlow<Boolean> = _showAddMealModal.asStateFlow()

    private val _showFoodScanModal = MutableStateFlow(false)
    val showFoodScanModal: StateFlow<Boolean> = _showFoodScanModal.asStateFlow()

    private val _showAddWeightModal = MutableStateFlow(false)
    val showAddWeightModal: StateFlow<Boolean> = _showAddWeightModal.asStateFlow()

    private val _activeWorkoutForSession = MutableStateFlow<WorkoutPlan?>(null)
    val activeWorkoutForSession: StateFlow<WorkoutPlan?> = _activeWorkoutForSession.asStateFlow()

    fun selectTab(tab: NavigationTab) {
        _isCalculatorOpen.value = false
        _isAiCoachOpen.value = false
        _currentTab.value = tab
    }

    fun toggleLanguage() {
        val next = if (language.value == AppLanguage.BN) AppLanguage.EN else AppLanguage.BN
        repository.setLanguage(next)
    }

    fun setLanguage(lang: AppLanguage) {
        repository.setLanguage(lang)
    }

    fun openCalculator() {
        _isCalculatorOpen.value = true
        _isAiCoachOpen.value = false
    }

    fun closeCalculator() {
        _isCalculatorOpen.value = false
    }

    fun openAiCoach() {
        _isAiCoachOpen.value = true
        _isCalculatorOpen.value = false
    }

    fun closeAiCoach() {
        _isAiCoachOpen.value = false
    }

    fun openAddMeal() {
        _showAddMealModal.value = true
    }

    fun closeAddMeal() {
        _showAddMealModal.value = false
    }

    fun openFoodScan() {
        _showFoodScanModal.value = true
    }

    fun closeFoodScan() {
        _showFoodScanModal.value = false
    }

    fun openAddWeight() {
        _showAddWeightModal.value = true
    }

    fun closeAddWeight() {
        _showAddWeightModal.value = false
    }

    fun startWorkoutSession(workout: WorkoutPlan) {
        _activeWorkoutForSession.value = workout
    }

    fun finishWorkoutSession(caloriesBurned: Int) {
        _activeWorkoutForSession.value = null
        repository.addSteps(350)
    }

    fun addMeal(meal: MealLog) {
        repository.addMeal(meal)
    }

    fun removeMeal(mealId: String) {
        repository.removeMeal(mealId)
    }

    fun addWater(amount: Double) {
        repository.addWater(amount)
    }

    fun resetWater() {
        repository.resetWater()
    }

    fun addSteps(steps: Int) {
        repository.addSteps(steps)
    }

    fun addProgress(weight: Double, waist: Double, notesBn: String, notesEn: String) {
        repository.addProgressEntry(weight, waist, notesBn, notesEn)
    }

    fun updateProfile(profile: UserProfile) {
        repository.updateProfile(profile)
    }

    fun resetToDemoData() {
        repository.resetToDemoData()
    }

    fun sendChatMessage(text: String) {
        viewModelScope.launch {
            repository.sendUserMessage(text)
        }
    }
}
