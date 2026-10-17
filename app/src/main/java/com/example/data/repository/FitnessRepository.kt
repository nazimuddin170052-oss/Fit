package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.datasource.FitnessDataSource
import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class FitnessRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("zuki_fit_ai_prefs", Context.MODE_PRIVATE)

    private val _language = MutableStateFlow(
        if (prefs.getString("pref_language", "BN") == "EN") AppLanguage.EN else AppLanguage.BN
    )
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _userProfile = MutableStateFlow(loadProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _loggedMeals = MutableStateFlow(loadMeals())
    val loggedMeals: StateFlow<List<MealLog>> = _loggedMeals.asStateFlow()

    private val _waterIntakeLiters = MutableStateFlow(prefs.getFloat("water_intake", 2.0f).toDouble())
    val waterIntakeLiters: StateFlow<Double> = _waterIntakeLiters.asStateFlow()

    private val _currentSteps = MutableStateFlow(prefs.getInt("current_steps", 6420))
    val currentSteps: StateFlow<Int> = _currentSteps.asStateFlow()

    private val _progressHistory = MutableStateFlow(loadProgressHistory())
    val progressHistory: StateFlow<List<ProgressEntry>> = _progressHistory.asStateFlow()

    private val _chatMessages = MutableStateFlow(loadChatHistory())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _caloricHistory = MutableStateFlow(computeCaloricHistory(_loggedMeals.value))
    val caloricHistory: StateFlow<List<DailyCalorieEntry>> = _caloricHistory.asStateFlow()

    private fun computeCaloricHistory(meals: List<MealLog>): List<DailyCalorieEntry> {
        val todayCals = meals.sumOf { it.calories }
        val todayP = meals.sumOf { it.protein }
        val todayC = meals.sumOf { it.carbs }
        val todayF = meals.sumOf { it.fat }
        return FitnessDataSource.sampleWeeklyCaloricHistory.map { entry ->
            if (entry.isToday) {
                entry.copy(
                    caloriesConsumed = todayCals,
                    calorieGoal = _userProfile.value.dailyCalorieGoal,
                    proteinGrams = todayP,
                    carbsGrams = todayC,
                    fatGrams = todayF
                )
            } else {
                entry
            }
        }
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
        prefs.edit().putString("pref_language", lang.name).apply()
    }

    fun updateProfile(profile: UserProfile) {
        _userProfile.value = profile
        _caloricHistory.value = computeCaloricHistory(_loggedMeals.value)
        prefs.edit()
            .putString("profile_name", profile.name)
            .putInt("profile_age", profile.age)
            .putInt("profile_height", profile.heightCm)
            .putFloat("profile_current_weight", profile.currentWeightKg.toFloat())
            .putFloat("profile_goal_weight", profile.goalWeightKg.toFloat())
            .putInt("profile_target_months", profile.targetMonths)
            .putInt("profile_calorie_goal", profile.dailyCalorieGoal)
            .putFloat("profile_water_goal", profile.dailyWaterGoalLiters.toFloat())
            .putInt("profile_step_goal", profile.dailyStepGoal)
            .apply()
    }

    fun addMeal(meal: MealLog) {
        val updated = _loggedMeals.value + meal
        _loggedMeals.value = updated
        _caloricHistory.value = computeCaloricHistory(updated)
        saveMeals(updated)
    }

    fun removeMeal(mealId: String) {
        val updated = _loggedMeals.value.filterNot { it.id == mealId }
        _loggedMeals.value = updated
        _caloricHistory.value = computeCaloricHistory(updated)
        saveMeals(updated)
    }

    fun addWater(amountLiters: Double) {
        val newAmount = ((_waterIntakeLiters.value + amountLiters) * 10).toInt() / 10.0
        val clamped = newAmount.coerceIn(0.0, 6.0)
        _waterIntakeLiters.value = clamped
        prefs.edit().putFloat("water_intake", clamped.toFloat()).apply()
    }

    fun resetWater() {
        _waterIntakeLiters.value = 0.0
        prefs.edit().putFloat("water_intake", 0.0f).apply()
    }

    fun addSteps(steps: Int) {
        val updated = (_currentSteps.value + steps).coerceAtLeast(0)
        _currentSteps.value = updated
        prefs.edit().putInt("current_steps", updated).apply()
    }

    fun addProgressEntry(weightKg: Double, waistInches: Double, notesBn: String, notesEn: String) {
        val sdfBn = SimpleDateFormat("d MMMM", Locale("bn", "BD"))
        val sdfEn = SimpleDateFormat("MMM d", Locale.ENGLISH)
        val now = Date()
        val entry = ProgressEntry(
            id = "prog_${System.currentTimeMillis()}",
            dateLabelBn = sdfBn.format(now),
            dateLabelEn = sdfEn.format(now),
            weightKg = weightKg,
            waistInches = waistInches,
            notesBn = notesBn,
            notesEn = notesEn,
            isSample = false
        )
        val updated = _progressHistory.value + entry
        _progressHistory.value = updated
        saveProgressHistory(updated)

        // Also update current weight in profile
        val profile = _userProfile.value.copy(currentWeightKg = weightKg)
        updateProfile(profile)
    }

    fun resetToDemoData() {
        _loggedMeals.value = FitnessDataSource.sampleInitialMeals
        _caloricHistory.value = computeCaloricHistory(FitnessDataSource.sampleInitialMeals)
        saveMeals(FitnessDataSource.sampleInitialMeals)

        _waterIntakeLiters.value = 2.0
        prefs.edit().putFloat("water_intake", 2.0f).apply()

        _currentSteps.value = 6420
        prefs.edit().putInt("current_steps", 6420).apply()

        _progressHistory.value = FitnessDataSource.sampleProgressHistory
        saveProgressHistory(FitnessDataSource.sampleProgressHistory)

        _chatMessages.value = FitnessDataSource.initialChatHistory
        saveChatHistory(FitnessDataSource.initialChatHistory)

        val defaultProfile = UserProfile()
        updateProfile(defaultProfile)
    }

    fun sendUserMessage(question: String): ChatMessage {
        val nowStr = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date())
        val userMsg = ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            textBn = question,
            textEn = question,
            isUser = true,
            timestamp = nowStr
        )
        val current = _chatMessages.value + userMsg
        _chatMessages.value = current

        // Generate intelligent fitness coach answer
        val coachReply = generateCoachResponse(question, nowStr)
        val updated = current + coachReply
        _chatMessages.value = updated
        saveChatHistory(updated)
        return coachReply
    }

    private fun generateCoachResponse(prompt: String, timeStr: String): ChatMessage {
        val lower = prompt.lowercase()
        val totalCalories = _loggedMeals.value.sumOf { it.calories }
        val goalCalories = _userProfile.value.dailyCalorieGoal
        val remaining = (goalCalories - totalCalories).coerceAtLeast(0)

        return when {
            lower.contains("ক্যালরি") || lower.contains("calorie") -> {
                ChatMessage(
                    id = "reply_${System.currentTimeMillis()}",
                    textBn = "আপনার আজকের খাবারের হিসাব:\n\n• মোট গ্রহণ: $totalCalories ক্যালরি\n• লক্ষ্যমাত্রা: $goalCalories ক্যালরি\n• অবশিষ্ট বাজেট: $remaining ক্যালরি\n\nটিপস: বাজেট শেষ হতে চললে শসা, টমেটো সালাদ অথবা পাতলা ডাল দিয়ে পেট ভরিয়ে রাখতে পারেন।",
                    textEn = "Here is your calorie check for today:\n\n• Consumed: $totalCalories kcal\n• Daily Goal: $goalCalories kcal\n• Remaining Budget: $remaining kcal\n\nTip: If your budget is getting tight, fill up on cucumber salad or light lentil soup.",
                    isUser = false,
                    timestamp = timeStr,
                    isCoachAdvice = true
                )
            }
            lower.contains("রুটি") || lower.contains("roti") -> {
                ChatMessage(
                    id = "reply_${System.currentTimeMillis()}",
                    textBn = "রাতে রুটি খাওয়া বেশ ভালো!\n\n১টি মাঝারি আটার রুটিতে প্রায় ১১০ ক্যালরি ও ৩.৫ গ্রাম প্রোটিন থাকে।\n\nপরামর্শ:\n১. তেলের পরোটা এড়িয়ে চলুন।\n২. ঘুমানোর অন্তত ২ ঘন্টা আগে ডাল ও সবজির সাথে খান।\n৩. ১-২টির বেশি রুটি না খাওয়াই ওজন কমানোর জন্য উত্তম।",
                    textEn = "Roti at night is a wonderful choice!\n\n1 medium whole wheat roti contains ~110 kcal with 3.5g protein.\n\nTips:\n1. Avoid oily parathas.\n2. Eat with plenty of dal and green veggies at least 2 hours before bed.\n3. Keep it to 1-2 rotis for safe calorie deficit.",
                    isUser = false,
                    timestamp = timeStr,
                    isCoachAdvice = true
                )
            }
            lower.contains("ডায়েট") || lower.contains("diet") || lower.contains("plan") || lower.contains("প্ল্যান") -> {
                ChatMessage(
                    id = "reply_${System.currentTimeMillis()}",
                    textBn = "ZUKI টেকসই ৭ দিনের ডায়েট ফর্মুলা:\n\n• সকাল: আটার রুটি (১-২টি) + ১টি সিদ্ধ ডিম + শসা।\n• দুপুর: ১ কাপ লাল বা সাদা ভাত + দেশি মাছ বা চিকেন + প্রচুর শাকসবজি ও ডাল।\n• বিকাল: গ্রিন টি + ১ মুঠো ছোলা বা ১টি আপেল/কলা।\n• রাত: ১টি পাতলা রুটি বা শুধু এক বাটি ডাল ও সবজি।\n\nকোনো ক্র্যাশ ডায়েট নয়, দিনে ২.৫ লিটার পানি ও স্বাভাবিক খাবারেই ওজন কমবে!",
                    textEn = "ZUKI Sustainable Daily Diet Formula:\n\n• Breakfast: 1-2 whole wheat rotis + 1 boiled egg + cucumber.\n• Lunch: 1 cup rice + fish or lean chicken + lots of green veg & dal.\n• Evening: Green tea + handful roasted chickpeas or fruit.\n• Dinner: 1 light roti or hearty vegetable lentil soup.\n\nNo extreme starvation, just healthy portions with 2.5L water daily!",
                    isUser = false,
                    timestamp = timeStr,
                    isCoachAdvice = true
                )
            }
            lower.contains("হাঁটা") || lower.contains("walk") || lower.contains("step") || lower.contains("পা") -> {
                ChatMessage(
                    id = "reply_${System.currentTimeMillis()}",
                    textBn = "হাঁটার সেরা সময়:\n\nসকালে খালি পেটে অথবা সন্ধ্যায় ২০-৩০ মিনিট দ্রুত হাঁটা মেটাবলিজমের গতি বাড়ায়।\n\nআজকে আপনি ${_currentSteps.value} পা হেঁটেছেন। প্রতিদিন ৮,০০০ পা হাঁটার লক্ষ্য রাখলে মাসে অন্তত ১-২ কেজি চর্বি প্রাকৃতিকভাবে কমে যায়!",
                    textEn = "Best time for walking:\n\nA 20-30 minute brisk walk in the morning or early evening boosts metabolic rate.\n\nToday you've walked ${_currentSteps.value} steps. Sustaining 8,000 steps daily naturally sheds 1-2 kg of fat monthly without stress!",
                    isUser = false,
                    timestamp = timeStr,
                    isCoachAdvice = true
                )
            }
            lower.contains("পানি") || lower.contains("water") -> {
                ChatMessage(
                    id = "reply_${System.currentTimeMillis()}",
                    textBn = "পানি পানের গুরুত্ব:\n\nদৈনিক অন্তত ২.৫ লিটার পানি পান করলে মেটাবলিক রেট ৩০% পর্যন্ত সাময়িকভাবে বেড়ে যায় এবং ক্ষুধা কম লাগে।\n\nআজকে আপনি ${_waterIntakeLiters.value} লিটার পানি পান করেছেন। একবারে অতিরিক্ত না খেয়ে প্রতি ১-২ ঘণ্টা পর পর ১ গ্লাস করে পান করুন।",
                    textEn = "Importance of hydration:\n\nDrinking 2.5L of water daily boosts metabolic efficiency by up to 30% and curbs false hunger.\n\nYou have logged ${_waterIntakeLiters.value}L today. Sip a glass every 1-2 hours for best cellular hydration.",
                    isUser = false,
                    timestamp = timeStr,
                    isCoachAdvice = true
                )
            }
            else -> {
                ChatMessage(
                    id = "reply_${System.currentTimeMillis()}",
                    textBn = "ধন্যবাদ প্রশ্নটির জন্য! ZUKI AI হিসেবে আমি আপনাকে সবসময় বাস্তবসম্মত ও নিরাপদ পুষ্টি পরামর্শ দিই।\n\nমনে রাখবেন, দ্রুত ওজন কমানোর ক্র্যাশ ডায়েট পেশিক্ষয় ঘটায়। প্রতিদিন ৩০০-৫০০ ক্যালরি নিরাপদ ঘাটতি, দেশি পুষ্টিকর খাবার এবং ঘরের ওয়ার্কআউটই দীর্ঘস্থায়ী সুস্বাস্থ্যের চাবিকাঠি। আপনার কি কোনো নির্দিষ্ট খাবার বা ব্যায়াম সম্পর্কে জানার আছে?",
                    textEn = "Thank you for asking! As your ZUKI AI Coach, I always promote realistic, sustainable wellness.\n\nRemember, crash diets cause muscle loss and fatigue. A safe 300-500 kcal deficit, wholesome local foods, and consistent bodyweight workouts deliver permanent fitness. Would you like specifics on any meal or workout?",
                    isUser = false,
                    timestamp = timeStr,
                    isCoachAdvice = true
                )
            }
        }
    }

    private fun loadProfile(): UserProfile {
        return UserProfile(
            name = prefs.getString("profile_name", "Nazim Uddin") ?: "Nazim Uddin",
            age = prefs.getInt("profile_age", 28),
            heightCm = prefs.getInt("profile_height", 172),
            currentWeightKg = prefs.getFloat("profile_current_weight", 74.0f).toDouble(),
            goalWeightKg = prefs.getFloat("profile_goal_weight", 65.0f).toDouble(),
            targetMonths = prefs.getInt("profile_target_months", 4),
            dailyCalorieGoal = prefs.getInt("profile_calorie_goal", 2200),
            dailyWaterGoalLiters = prefs.getFloat("profile_water_goal", 2.5f).toDouble(),
            dailyStepGoal = prefs.getInt("profile_step_goal", 8000)
        )
    }

    private fun saveMeals(meals: List<MealLog>) {
        val array = JSONArray()
        meals.forEach { meal ->
            val obj = JSONObject().apply {
                put("id", meal.id)
                put("mealType", meal.mealType.name)
                put("foodNameBn", meal.foodNameBn)
                put("foodNameEn", meal.foodNameEn)
                put("emoji", meal.emoji)
                put("servingDescBn", meal.servingDescBn)
                put("servingDescEn", meal.servingDescEn)
                put("calories", meal.calories)
                put("protein", meal.protein)
                put("carbs", meal.carbs)
                put("fat", meal.fat)
                put("timestamp", meal.timestamp)
            }
            array.put(obj)
        }
        prefs.edit().putString("saved_meals_json", array.toString()).apply()
    }

    private fun loadMeals(): List<MealLog> {
        val jsonStr = prefs.getString("saved_meals_json", null) ?: return FitnessDataSource.sampleInitialMeals
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<MealLog>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    MealLog(
                        id = obj.getString("id"),
                        mealType = MealType.valueOf(obj.getString("mealType")),
                        foodNameBn = obj.getString("foodNameBn"),
                        foodNameEn = obj.getString("foodNameEn"),
                        emoji = obj.optString("emoji", "🍲"),
                        servingDescBn = obj.getString("servingDescBn"),
                        servingDescEn = obj.getString("servingDescEn"),
                        calories = obj.getInt("calories"),
                        protein = obj.getDouble("protein"),
                        carbs = obj.getDouble("carbs"),
                        fat = obj.getDouble("fat"),
                        timestamp = obj.getString("timestamp")
                    )
                )
            }
            list
        } catch (e: Exception) {
            FitnessDataSource.sampleInitialMeals
        }
    }

    private fun saveProgressHistory(list: List<ProgressEntry>) {
        val array = JSONArray()
        list.forEach { p ->
            val obj = JSONObject().apply {
                put("id", p.id)
                put("dateLabelBn", p.dateLabelBn)
                put("dateLabelEn", p.dateLabelEn)
                put("weightKg", p.weightKg)
                put("waistInches", p.waistInches)
                put("notesBn", p.notesBn)
                put("notesEn", p.notesEn)
                put("isSample", p.isSample)
            }
            array.put(obj)
        }
        prefs.edit().putString("saved_progress_json", array.toString()).apply()
    }

    private fun loadProgressHistory(): List<ProgressEntry> {
        val jsonStr = prefs.getString("saved_progress_json", null) ?: return FitnessDataSource.sampleProgressHistory
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<ProgressEntry>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ProgressEntry(
                        id = obj.getString("id"),
                        dateLabelBn = obj.getString("dateLabelBn"),
                        dateLabelEn = obj.getString("dateLabelEn"),
                        weightKg = obj.getDouble("weightKg"),
                        waistInches = obj.getDouble("waistInches"),
                        notesBn = obj.optString("notesBn", ""),
                        notesEn = obj.optString("notesEn", ""),
                        isSample = obj.optBoolean("isSample", false)
                    )
                )
            }
            list
        } catch (e: Exception) {
            FitnessDataSource.sampleProgressHistory
        }
    }

    private fun saveChatHistory(messages: List<ChatMessage>) {
        val array = JSONArray()
        messages.forEach { m ->
            val obj = JSONObject().apply {
                put("id", m.id)
                put("textBn", m.textBn)
                put("textEn", m.textEn)
                put("isUser", m.isUser)
                put("timestamp", m.timestamp)
                put("isCoachAdvice", m.isCoachAdvice)
            }
            array.put(obj)
        }
        prefs.edit().putString("saved_chat_json", array.toString()).apply()
    }

    private fun loadChatHistory(): List<ChatMessage> {
        val jsonStr = prefs.getString("saved_chat_json", null) ?: return FitnessDataSource.initialChatHistory
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<ChatMessage>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ChatMessage(
                        id = obj.getString("id"),
                        textBn = obj.getString("textBn"),
                        textEn = obj.getString("textEn"),
                        isUser = obj.getBoolean("isUser"),
                        timestamp = obj.getString("timestamp"),
                        isCoachAdvice = obj.optBoolean("isCoachAdvice", false)
                    )
                )
            }
            list
        } catch (e: Exception) {
            FitnessDataSource.initialChatHistory
        }
    }
}
