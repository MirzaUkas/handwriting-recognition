package com.mirz.handwriting.data.repository

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mirz.handwriting.common.Response
import com.mirz.handwriting.domain.entities.ReportEntity
import com.mirz.handwriting.domain.repository.QuestionRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class QuestionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore, private val auth: FirebaseAuth
) : QuestionRepository {
    override suspend fun submitQuestion(
        id: String, pos: Int, answer: String, correct: Boolean
    ): Response<Any> {
        val response: Response<Any> = try {
            Response.Loading
            val answers = mutableListOf<HashMap<String, Any>>()
            auth.uid?.let {
                firestore.collection("report").document(it).collection("answered_question")
                    .document(id)
                    .get().addOnSuccessListener { report ->
                        val data = report.toObject(ReportEntity::class.java)
                        data?.answers?.let { ans ->
                            answers.addAll(ans)
                        }
                    }.await()

                if (pos + 1 <= answers.size) {
                    val retryCount = answers[pos]["retryCount"]?.toString()?.toInt() ?: 0
                    answers[pos] = hashMapOf(
                        "answer" to answer,
                        "answerId" to pos,
                        "correct" to correct,
                        "retryCount" to retryCount + 1,
                    )
                } else {
                    answers.add(
                        pos,
                        hashMapOf(
                            "answer" to answer,
                            "answerId" to pos,
                            "correct" to correct,
                            "retryCount" to 1,
                        )
                    )
                }

                firestore.collection("report").document(it).collection("answered_question")
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