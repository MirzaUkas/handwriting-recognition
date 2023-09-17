package com.mirz.handwriting.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.mirz.handwriting.common.Response
import com.mirz.handwriting.domain.entities.LessonEntity
import com.mirz.handwriting.domain.repository.LessonRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LessonRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : LessonRepository {
    override suspend fun getLessons(): Response<List<LessonEntity>> {
        val response: Response<List<LessonEntity>> = try {
            Response.Loading
            val res = firestore.collection("question").get().await().map {
                it.toObject(LessonEntity::class.java).copy(id = it.id)
            }
            Response.Success(res)
        } catch (e: FirebaseFirestoreException) {
            Response.Failure(e)
        }
        return response
    }

    override suspend fun getLesson(id: String): Response<LessonEntity> {
        val response: Response<LessonEntity> = try {
            Response.Loading
            val res = firestore.collection("question").document(id).get().await()
                .toObject(LessonEntity::class.java)
            Response.Success(res)
        } catch (e: FirebaseFirestoreException) {
            Response.Failure(e)
        }
        return response
    }
}