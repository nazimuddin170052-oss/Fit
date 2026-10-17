package com.example.data.model

enum class AppLanguage {
    BN, EN
}

enum class NavigationTab {
    HOME, FOOD, WORKOUT, PROGRESS, PROFILE
}

enum class FoodCategory(val labelBn: String, val labelEn: String) {
    ALL("সব খাবার", "All"),
    BANGLADESHI("বাংলাদেশী ঐতিহ্য", "Bangladeshi"),
    BREAKFAST("সকালের নাস্তা", "Breakfast"),
    LUNCH("দুপুরের খাবার", "Lunch"),
    DINNER("রাতের খাবার", "Dinner"),
    SNACKS("হালকা নাস্তা", "Snacks")
}

enum class ServingUnit(val labelBn: String, val labelEn: String, val gramEquivalent: Double) {
    GRAM("গ্রাম", "g", 1.0),
    CUP("কাপ", "cup", 150.0),
    PIECE("পিস", "pc", 50.0),
    BOWL("বাটি", "bowl", 200.0),
    SPOON("চামচ", "tbsp", 15.0)
}

data class FoodItem(
    val id: String,
    val nameBn: String,
    val nameEn: String,
    val emoji: String,
    val defaultServingBn: String,
    val defaultServingEn: String,
    val unit: ServingUnit,
    val defaultQuantity: Double,
    val caloriesPerUnit: Double, // kcal per 1 unit of ServingUnit
    val proteinPerUnit: Double,  // grams
    val carbsPerUnit: Double,    // grams
    val fatPerUnit: Double,      // grams
    val fiberPerUnit: Double,    // grams
    val category: FoodCategory,
    val isBangladeshi: Boolean = true,
    val noteBn: String = "",
    val noteEn: String = ""
) {
    fun calculate(quantity: Double): Nutrients {
        val factor = if (defaultQuantity > 0) quantity / defaultQuantity else 1.0
        return Nutrients(
            calories = (caloriesPerUnit * factor).toInt(),
            protein = ((proteinPerUnit * factor * 10).toInt()) / 10.0,
            carbs = ((carbsPerUnit * factor * 10).toInt()) / 10.0,
            fat = ((fatPerUnit * factor * 10).toInt()) / 10.0,
            fiber = ((fiberPerUnit * factor * 10).toInt()) / 10.0
        )
    }
}

data class Nutrients(
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val fiber: Double
)

enum class MealType(val titleBn: String, val titleEn: String, val timeHint: String) {
    BREAKFAST("সকালের নাস্তা", "Breakfast", "08:30 AM"),
    LUNCH("দুপুরের খাবার", "Lunch", "01:30 PM"),
    DINNER("রাতের খাবার", "Dinner", "08:30 PM"),
    SNACK("হালকা নাস্তা", "Snack", "05:00 PM")
}

data class MealLog(
    val id: String,
    val mealType: MealType,
    val foodNameBn: String,
    val foodNameEn: String,
    val emoji: String = "🍲",
    val servingDescBn: String,
    val servingDescEn: String,
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val timestamp: String
)

enum class WorkoutCategory(val labelBn: String, val labelEn: String) {
    ALL("সব ব্যায়াম", "All"),
    FAT_LOSS("ফ্যাট লস", "Fat Loss"),
    BEGINNER("বিগিনার", "Beginner"),
    HOME("ঘরে ব্যায়াম", "Home"),
    LOW_IMPACT("হাঁটু-বান্ধব / লো ইমপ্যাক্ট", "Low-Impact")
}

data class ExerciseItem(
    val id: String,
    val nameBn: String,
    val nameEn: String,
    val sets: Int,
    val repsOrDurationBn: String,
    val repsOrDurationEn: String,
    val restSeconds: Int = 20,
    val tipBn: String,
    val tipEn: String
)

data class WorkoutPlan(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val category: WorkoutCategory,
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val levelBn: String,
    val levelEn: String,
    val descriptionBn: String,
    val descriptionEn: String,
    val exercises: List<ExerciseItem>,
    val isFeatured: Boolean = false,
    val equipmentBn: String = "কোনো সরঞ্জাম প্রয়োজন নেই",
    val equipmentEn: String = "No equipment needed"
)

data class ProgressEntry(
    val id: String,
    val dateLabelBn: String,
    val dateLabelEn: String,
    val weightKg: Double,
    val waistInches: Double,
    val notesBn: String = "",
    val notesEn: String = "",
    val isSample: Boolean = false
)

data class ChatMessage(
    val id: String,
    val textBn: String,
    val textEn: String,
    val isUser: Boolean,
    val timestamp: String,
    val isCoachAdvice: Boolean = false,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val actionSuggestions: List<Pair<String, String>> = emptyList() // Pair<BN, EN>
)

data class UserProfile(
    val name: String = "Nazim Uddin",
    val age: Int = 28,
    val heightCm: Int = 172,
    val currentWeightKg: Double = 74.0,
    val goalWeightKg: Double = 65.0,
    val targetMonths: Int = 4,
    val streakDays: Int = 5,
    val dailyCalorieGoal: Int = 2200,
    val dailyWaterGoalLiters: Double = 2.5,
    val dailyStepGoal: Int = 8000,
    val activeMinutesToday: Int = 32
)

data class DailyCalorieEntry(
    val id: String,
    val dayLabelBn: String,
    val dayLabelEn: String,
    val dateLabelBn: String,
    val dateLabelEn: String,
    val caloriesConsumed: Int,
    val calorieGoal: Int,
    val proteinGrams: Double,
    val carbsGrams: Double,
    val fatGrams: Double,
    val isToday: Boolean = false
)
