package com.mirz.handwriting.navigation

import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import com.mirz.handwriting.common.parcelable
import com.mirz.handwriting.domain.entities.QuestionEntity


class QuestionType : NavType<QuestionEntity>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): QuestionEntity? {
        return bundle.parcelable(key)
    }

    override fun parseValue(value: String): QuestionEntity {
        return Gson().fromJson(value, QuestionEntity::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: QuestionEntity) {
        bundle.putParcelable(key, value)
    }
}