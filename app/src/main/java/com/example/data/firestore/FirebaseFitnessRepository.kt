package com.example.data.firestore

import android.content.Context
import com.example.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirebaseFitnessRepository(
    val db: FirebaseFirestore
) {
    // Secondary constructor resolving named database ID from string resources
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    // --- USER PROFILE ---

    fun observeUserProfile(userId: String): Flow<FirestoreUserProfile?> {
        val docRef = db.collection("users").document(userId)
        return docRef.snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(FirestoreUserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                } else {
                    null
                }
            }
            .catch { error ->
                if (error is Exception) {
                    handleFirestoreError(error, OperationType.GET, docRef.path)
                }
                throw error
            }
    }

    suspend fun saveUserProfile(profile: FirestoreUserProfile): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        return try {
            val toSave = if (profile.createdAt == null) {
                profile.copy(userId = uid)
            } else {
                profile.copy(userId = uid, updatedAt = null)
            }
            docRef.set(toSave.toMap()).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    // --- MEALS SUBCOLLECTION ---

    fun observeMeals(userId: String): Flow<List<FirestoreMealLog>> {
        val colRef = db.collection("users").document(userId).collection("meals")
        return colRef.snapshots()
            .map { snapshot ->
                snapshot.toObjects(FirestoreMealLog::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            }
            .catch { error ->
                if (error is Exception) {
                    handleFirestoreError(error, OperationType.LIST, colRef.path)
                }
                throw error
            }
    }

    suspend fun logMeal(meal: FirestoreMealLog): Result<String> {
        val uid = requireUserId()
        val colRef = db.collection("users").document(uid).collection("meals")
        val docRef = if (meal.id.isNotBlank()) colRef.document(meal.id) else colRef.document()
        return try {
            val toSave = meal.copy(id = docRef.id, userId = uid)
            docRef.set(toSave.toMap()).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, colRef.path)
            Result.failure(e)
        }
    }

    suspend fun deleteMeal(mealId: String): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("meals").document(mealId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    // --- PROGRESS SUBCOLLECTION ---

    fun observeProgress(userId: String): Flow<List<FirestoreProgressEntry>> {
        val colRef = db.collection("users").document(userId).collection("progress")
        return colRef.snapshots()
            .map { snapshot ->
                snapshot.toObjects(FirestoreProgressEntry::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            }
            .catch { error ->
                if (error is Exception) {
                    handleFirestoreError(error, OperationType.LIST, colRef.path)
                }
                throw error
            }
    }

    suspend fun logProgress(entry: FirestoreProgressEntry): Result<String> {
        val uid = requireUserId()
        val colRef = db.collection("users").document(uid).collection("progress")
        val docRef = if (entry.id.isNotBlank()) colRef.document(entry.id) else colRef.document()
        return try {
            val toSave = entry.copy(id = docRef.id, userId = uid)
            docRef.set(toSave.toMap()).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, colRef.path)
            Result.failure(e)
        }
    }
}
