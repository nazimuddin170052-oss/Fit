package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.datasource.FitnessDataSource
import com.example.data.model.AppLanguage
import com.example.data.model.WorkoutCategory
import com.example.data.repository.FitnessRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context matches ZUKI Fit AI`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ZUKI Fit AI", appName)
  }

  @Test
  fun `bangladeshi foods database contains rice, roti, dal, fish, chicken`() {
    val foods = FitnessDataSource.bangladeshiFoods
    assertTrue(foods.any { it.id == "rice_white" })
    assertTrue(foods.any { it.id == "roti_atta" })
    assertTrue(foods.any { it.id == "dal_musur" })
    assertTrue(foods.any { it.id == "fish_rui" })
    assertTrue(foods.any { it.id == "chicken_curry" })
  }

  @Test
  fun `workout database includes low impact beginner options`() {
    val workouts = FitnessDataSource.workouts
    assertTrue(workouts.any { it.category == WorkoutCategory.LOW_IMPACT })
    assertTrue(workouts.any { it.category == WorkoutCategory.FAT_LOSS })
  }

  @Test
  fun `food calculation correctly computes nutrients for servings`() {
    val rice = FitnessDataSource.bangladeshiFoods.first { it.id == "rice_white" }
    val calc = rice.calculate(2.0) // 2 cups
    assertEquals(412, calc.calories)
    assertTrue(calc.protein > 8.0)
    assertTrue(calc.carbs > 80.0)
  }

  @Test
  fun `repository defaults to Bengali language and loads streak`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = FitnessRepository(context)
    assertEquals(AppLanguage.BN, repo.language.value)
    assertTrue(repo.userProfile.value.streakDays >= 1)
  }

  @Test
  fun `caloric history contains 7 daily entries and reflects calorie budget`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = FitnessRepository(context)
    val history = repo.caloricHistory.value
    assertEquals(7, history.size)
    val today = history.find { it.isToday }
    assertTrue(today != null)
    assertTrue((today?.caloriesConsumed ?: 0) > 0)
    assertEquals(repo.userProfile.value.dailyCalorieGoal, today?.calorieGoal)
  }
}

