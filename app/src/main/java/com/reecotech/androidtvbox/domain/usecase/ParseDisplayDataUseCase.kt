package com.reecotech.androidtvbox.domain.usecase

import com.reecotech.androidtvbox.data.model.DisplayData
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import timber.log.Timber
import javax.inject.Inject

sealed class ParseResult {
    data class Success(val data: List<DisplayData>) : ParseResult()
    object JsonError : ParseResult()
}

class ParseDisplayDataUseCase @Inject constructor(
    private val moshi: Moshi
) {
    operator fun invoke(jsonString: String?): ParseResult {
        if (jsonString.isNullOrBlank()) {
            return ParseResult.Success(emptyList())
        }
        return try {
            val listType = Types.newParameterizedType(List::class.java, DisplayData::class.java)
            val adapter = moshi.adapter<List<DisplayData>>(listType)
            val data = adapter.fromJson(jsonString) ?: emptyList()
            ParseResult.Success(data)
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse JSON string")
            ParseResult.JsonError
        }
    }
}
