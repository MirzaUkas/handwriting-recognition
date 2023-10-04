package com.mirz.handwriting.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.mirz.handwriting.common.Response
import com.mirz.handwriting.domain.entities.LessonEntity
import com.mirz.handwriting.domain.entities.ReportDetailEntity
import com.mirz.handwriting.domain.entities.ReportEntity
import com.mirz.handwriting.domain.entities.UserEntity
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
            val user = firestore.collection("user").document(auth.currentUser?.uid.toString()).get()
                .await().toObject(UserEntity::class.java)

            val res = firestore.collection("question").whereEqualTo("createdBy", user?.mentor).get().await().map {
                it.toObject(LessonEntity::class.java).copy(id = it.id)
            }.sortedBy { it.level }
            Response.Success(res)
        } catch (e: FirebaseFirestoreException) {
            Response.Failure(e)
        }
        return response
    }

    override suspend fun getLesson(id: String): Response<LessonEntity> {
        val response: Response<LessonEntity> = try {
            // Get Reports
            val reports = mutableListOf<ReportDetailEntity>()
            auth.uid?.let {
                firestore.collection("report").document(it).collection("answered_question")
                    .document(id).get().addOnSuccessListener { report ->
                        val data = report.toObject(ReportEntity::class.java)
                        data?.answers?.let { ans ->
                            reports.addAll(ans)
                        }
                    }.await()
            }


            val res = firestore.collection("question").document(id).get().await()
                .toObject(LessonEntity::class.java)
            val items = res?.items.orEmpty().toMutableList()

            items.forEachIndexed { questionIndex, question ->
                reports.forEachIndexed { answerIndex, report ->
                    if (questionIndex == answerIndex) {
                        items[questionIndex] = question.copy(report = report)
                    }
                }
            }
            Response.Success(res?.copy(id = id, items = items))
        } catch (e: FirebaseFirestoreException) {
            Response.Failure(e)
        }
        return response
    }

    override suspend fun getReport(): Response<List<LessonEntity>> {
        val response: Response<List<LessonEntity>> = try {
            val updatedLessons = mutableListOf<LessonEntity>()
            // Get User
            val user = firestore.collection("user").document(auth.currentUser?.uid.toString()).get()
                .await().toObject(UserEntity::class.java)

            // Get Questions
            firestore.collection("question").whereEqualTo("createdBy", user?.mentor).get().await().map {
                it.toObject(LessonEntity::class.java).copy(id = it.id)
            }.forEachIndexed { index, lesson ->
                // Get Reports
                val reports = mutableListOf<ReportDetailEntity>()

                auth.uid?.let {
                    firestore.collection("report").document(it).collection("answered_question")
                        .document(lesson.id.orEmpty()).get().addOnSuccessListener { report ->
                            val data = report.toObject(ReportEntity::class.java)
                            data?.answers?.let { ans ->
                                reports.addAll(ans)
                            }
                        }.await()
                }

                // Update Answers
                val items = lesson.items.orEmpty().toMutableList()
                items.forEachIndexed { questionIndex, question ->
                    reports.forEachIndexed { answerIndex, report ->
                        if (questionIndex == answerIndex) {
                            items[questionIndex] = question.copy(report = report)
                        }
                    }
                }

                // Store Updated Lesson
                updatedLessons.add(index, lesson.copy(items = items))
            }

            Response.Success(updatedLessons.sortedBy { it.level })
        } catch (e: FirebaseFirestoreException) {
            Response.Failure(e)
        }

        return response
    }
}