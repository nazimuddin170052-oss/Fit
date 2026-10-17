package com.example.data.firestore

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class FirestoreUserProfile(
    @get:DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val age: Int = 28,
    val heightCm: Int = 172,
    val currentWeightKg: Double = 74.0,
    val goalWeightKg: Double = 65.0,
    val targetMonths: Int = 4,
    val streakDays: Int = 5,
    val dailyCalorieGoal: Int = 2200,
    val dailyWaterGoalLiters: Double = 2.5,
    val dailyStepGoal: Int = 8000,
    val activeMinutesToday: Int = 30,
    val waterIntakeMl: Int = 1250,
    @get:ServerTimestamp val createdAt: Timestamp? = null,
    @get:ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>(
            "userId" to userId,
            "name" to name,
            "age" to age,
            "heightCm" to heightCm,
            "currentWeightKg" to currentWeightKg,
            "goalWeightKg" to goalWeightKg,
            "targetMonths" to targetMonths,
            "streakDays" to streakDays,
            "dailyCalorieGoal" to dailyCalorieGoal,
            "dailyWaterGoalLiters" to dailyWaterGoalLiters,
            "dailyStepGoal" to dailyStepGoal,
            "activeMinutesToday" to activeMinutesToday,
            "waterIntakeMl" to waterIntakeMl,
            "createdAt" to (createdAt ?: FieldValue.serverTimestamp())
        )
        if (updatedAt != null) {
            map["updatedAt"] = updatedAt
        }
        return map
    }
}

@IgnoreExtraProperties
data class FirestoreMealLog(
    @get:DocumentId val id: String = "",
    val userId: String = "",
    val foodNameEn: String = "",
    val foodNameBn: String = "",
    val mealType: String = "Lunch",
    val emoji: String = "🍲",
    val servingDescBn: String = "",
    val servingDescEn: String = "",
    val calories: Int = 0,
    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,
    val timestamp: String = "",
    @get:ServerTimestamp val createdAt: Timestamp? = null,
    @get:ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>(
            "userId" to userId,
            "foodNameEn" to foodNameEn,
            "foodNameBn" to foodNameBn,
            "mealType" to mealType,
            "emoji" to emoji,
            "servingDescBn" to servingDescBn,
            "servingDescEn" to servingDescEn,
            "calories" to calories,
            "protein" to protein,
            "carbs" to carbs,
            "fat" to fat,
            "timestamp" to timestamp,
            "createdAt" to (createdAt ?: FieldValue.serverTimestamp())
        )
        if (updatedAt != null) {
            map["updatedAt"] = updatedAt
        }
        return map
    }
}

@IgnoreExtraProperties
data class FirestoreProgressEntry(
    @get:DocumentId val id: String = "",
    val userId: String = "",
    val weightKg: Double = 0.0,
    val waistInches: Double = 0.0,
    val dateLabelBn: String = "",
    val dateLabelEn: String = "",
    val notesBn: String = "",
    val notesEn: String = "",
    val isSample: Boolean = false,
    @get:ServerTimestamp val createdAt: Timestamp? = null,
    @get:ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>(
            "userId" to userId,
            "weightKg" to weightKg,
            "waistInches" to waistInches,
            "dateLabelBn" to dateLabelBn,
            "dateLabelEn" to dateLabelEn,
            "notesBn" to notesBn,
            "notesEn" to notesEn,
            "isSample" to isSample,
            "createdAt" to (createdAt ?: FieldValue.serverTimestamp())
        )
        if (updatedAt != null) {
            map["updatedAt"] = updatedAt
        }
        return map
    }
}
