package com.mirz.handwriting.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.mirz.handwriting.common.Response
import com.mirz.handwriting.domain.entities.LessonEntity
import com.mirz.handwriting.domain.entities.ReportEntity
import com.mirz.handwriting.domain.repository.LessonRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LessonRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
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
            val answers = mutableListOf<HashMap<String, Any>>()
            auth.uid?.let {
                firestore.collection("report").document(it).collection("answered_question")
                    .document(id).get().addOnSuccessListener { report ->
                        val data = report.toObject(ReportEntity::class.java)
                        data?.answers?.let { ans ->
                            answers.addAll(ans)
                        }
                    }.await()
            }
            val res = firestore.collection("question").document(id).get().await()
                .toObject(LessonEntity::class.java)
            val items = res?.items.orEmpty().toMutableList()

            items.forEachIndexed { questionIndex, question ->//0 //1
                answers.forEachIndexed { answerIndex, hashMap ->//00//01 /10//11
                    if (questionIndex == answerIndex) {
                        items[questionIndex] = question.copy(lastAnswer = hashMap["answer"]?.toString())
                    }
                }
            }
            Response.Success(res?.copy(id = id, items = items))
        } catch (e: FirebaseFirestoreException) {
            Response.Failure(e)
        }
        return response
    }
}