package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.firestore.FirebaseFitnessRepository
import com.example.data.firestore.FirestoreMealLog
import com.example.data.firestore.FirestoreProgressEntry
import com.example.data.firestore.FirestoreUserProfile
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class FitnessRepositoryRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun saveAndObserveUserProfile_authenticatedOwner_succeeds() = runBlocking {
    val uid: String = signInTestUser(ALICE_EMAIL)
    val repository = FirebaseFitnessRepository(firestore)

    val profile = FirestoreUserProfile(
      userId = uid,
      name = "Alice Uddin",
      currentWeightKg = 72.5,
      goalWeightKg = 64.0
    )

    val saveResult = withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.saveUserProfile(profile)
    }
    assertTrue(saveResult.isSuccess)

    val observed = withTimeout(FLOW_TIMEOUT_MS) {
      repository.observeUserProfile(uid).first { it != null }
    }
    assertEquals("Alice Uddin", observed?.name)
    assertEquals(72.5, observed?.currentWeightKg ?: 0.0, 0.01)
  }

  @Test
  fun logAndObserveMeal_authenticatedOwner_succeeds() = runBlocking {
    val uid: String = signInTestUser(ALICE_EMAIL)
    val repository = FirebaseFitnessRepository(firestore)

    val meal = FirestoreMealLog(
      userId = uid,
      foodNameEn = "Beef Tehari",
      foodNameBn = "বিফ তেহারি",
      calories = 620,
      protein = 32.0,
      carbs = 70.0,
      fat = 22.0
    )

    val logResult = withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.logMeal(meal)
    }
    assertTrue(logResult.isSuccess)
    val mealId = logResult.getOrThrow()
    assertTrue(mealId.isNotEmpty())

    val meals = withTimeout(FLOW_TIMEOUT_MS) {
      repository.observeMeals(uid).first { list -> list.any { it.id == mealId } }
    }
    assertTrue(meals.any { it.foodNameEn == "Beef Tehari" })
  }

  @Test
  fun logAndObserveProgress_authenticatedOwner_succeeds() = runBlocking {
    val uid: String = signInTestUser(ALICE_EMAIL)
    val repository = FirebaseFitnessRepository(firestore)

    val progress = FirestoreProgressEntry(
      userId = uid,
      weightKg = 73.0,
      waistInches = 32.0,
      dateLabelEn = "17 Oct 2026",
      dateLabelBn = "১৭ অক্টোবর"
    )

    val logResult = withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.logProgress(progress)
    }
    assertTrue(logResult.isSuccess)
    val progressId = logResult.getOrThrow()
    assertTrue(progressId.isNotEmpty())

    val progressList = withTimeout(FLOW_TIMEOUT_MS) {
      repository.observeProgress(uid).first { list -> list.any { it.id == progressId } }
    }
    assertTrue(progressList.any { it.id == progressId })
  }

  @Test
  fun observeMeals_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
    val uid: String = signInTestUser(ALICE_EMAIL)
    auth.signOut()
    val repository = FirebaseFitnessRepository(firestore)

    try {
      withTimeout(FLOW_TIMEOUT_MS) {
        repository.observeMeals(uid).first()
      }
      fail("Expected PERMISSION_DENIED exception")
    } catch (e: Throwable) {
      val firestoreException = (e as? FirebaseFirestoreException)
        ?: (e.cause as? FirebaseFirestoreException)
        ?: (e.cause?.cause as? FirebaseFirestoreException)
      assertNotNull("Expected FirebaseFirestoreException in cause chain", firestoreException)
      assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException?.code)
    }
  }

  @Test
  fun observeProgress_crossUserAccess_failsWithPermissionDenied() = runBlocking {
    val aliceUid: String = signInTestUser(ALICE_EMAIL)
    val aliceRepo = FirebaseFitnessRepository(firestore)

    val progress = FirestoreProgressEntry(
      userId = aliceUid,
      weightKg = 73.0,
      waistInches = 32.0
    )
    withTimeout(DEFAULT_TIMEOUT_MS) {
      aliceRepo.logProgress(progress).getOrThrow()
    }

    signInTestUser(BOB_EMAIL)
    val bobRepo = FirebaseFitnessRepository(firestore)

    try {
      withTimeout(FLOW_TIMEOUT_MS) {
        bobRepo.observeProgress(aliceUid).first()
      }
      fail("Expected PERMISSION_DENIED exception on cross-user read")
    } catch (e: Throwable) {
      val firestoreException = (e as? FirebaseFirestoreException)
        ?: (e.cause as? FirebaseFirestoreException)
        ?: (e.cause?.cause as? FirebaseFirestoreException)
      assertNotNull("Expected FirebaseFirestoreException in cause chain", firestoreException)
      assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException?.code)
    }
  }

  private companion object {
    const val ALICE_EMAIL = "alice@example.com"
    const val BOB_EMAIL = "bob@example.com"
    const val DEFAULT_TIMEOUT_MS = 5000L
    const val FLOW_TIMEOUT_MS = 4000L
  }
}
