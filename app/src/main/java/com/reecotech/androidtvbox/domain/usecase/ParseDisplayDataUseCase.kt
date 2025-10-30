package com.reecotech.androidtvbox.domain.usecase

import com.reecotech.androidtvbox.data.model.DisplayData
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import javax.inject.Inject

class ParseDisplayDataUseCase @Inject constructor(
    private val moshi: Moshi
) {
    operator fun invoke(jsonString: String?): List<DisplayData> {
        if (jsonString.isNullOrBlank()) {
            return emptyList()
        }
        return try {
            val listType = Types.newParameterizedType(List::class.java, DisplayData::class.java)
            val adapter = moshi.adapter<List<DisplayData>>(listType)
            adapter.fromJson(jsonString) ?: emptyList()
        } catch (e: Exception) {
            // Log the error in a real app
            e.printStackTrace()
            emptyList()
        }
    }
}
