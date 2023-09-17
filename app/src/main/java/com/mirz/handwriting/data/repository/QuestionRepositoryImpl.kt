package com.mirz.handwriting.data.repository

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mirz.handwriting.domain.repository.LessonRepository
import com.mirz.handwriting.domain.repository.QuestionRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : QuestionRepository {
    override suspend fun submitQuestion(id: String, pos: Int, answer: String) {
        try{
            auth.uid?.let {
                firestore.collection("report").document(it).set(
                    hashMapOf(
                        "questionId" to id,
                        "items" to listOf(
                            hashMapOf("answer" to answer),
                            hashMapOf("id" to pos)
                        ),
                    )
                ).await()
            }
        }catch( e: FirebaseException){
            Log.e("HERE", e.localizedMessage.toString())
        }

    }
}