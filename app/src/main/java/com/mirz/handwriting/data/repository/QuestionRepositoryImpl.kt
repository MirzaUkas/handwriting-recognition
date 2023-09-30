package com.mirz.handwriting.data.repository

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.digitalink.Ink
import com.mirz.handwriting.common.Response
import com.mirz.handwriting.domain.entities.PointEntity
import com.mirz.handwriting.domain.entities.ReportDetailEntity
import com.mirz.handwriting.domain.entities.ReportEntity
import com.mirz.handwriting.domain.repository.QuestionRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class QuestionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore, private val auth: FirebaseAuth
) : QuestionRepository {
    override suspend fun submitQuestion(
        id: String, pos: Int, answer: String, correct: Boolean, points: List<Ink.Point>
    ): Response<Any> {
        val response: Response<Any> = try {
            Response.Loading
            val answers = mutableListOf<ReportDetailEntity>()
            auth.uid?.let { uid ->
                firestore.collection("report").document(uid).collection("answered_question")
                    .document(id)
                    .get().addOnSuccessListener { report ->
                        val data = report.toObject(ReportEntity::class.java)
                        data?.answers?.let { ans ->
                            answers.addAll(ans)
                        }
                    }.await()

                if (pos + 1 <= answers.size) {
                    val retryCount = answers[pos].retryCount ?: 0
                    answers[pos] = ReportDetailEntity(
                        answer = answer,
                        answerId = pos,
                        correct = correct,
                        points = points.map {
                            PointEntity(it.x, it.y)
                        },
                        retryCount = if (!correct) retryCount + 1 else retryCount,
                    )


                } else {
                    answers.add(
                        pos, ReportDetailEntity(
                            answer = answer,
                            answerId = pos,
                            correct = correct,
                            points = points.map {
                                PointEntity(it.x, it.y)
                            },
                            retryCount = if (!correct) 1 else 0,
                        )
                    )
                }

                firestore.collection("report").document(uid).collection("answered_question")
                    .document(id).set(
                        hashMapOf(
                            "answers" to answers
                        )
                    ).await()

            }
            Response.Success(Any())
        } catch (e: FirebaseException) {
            Response.Success(e.localizedMessage)
        }

        return response
    }
}