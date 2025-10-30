package com.reecotech.androidtvbox.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DisplayData(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "value") val value: String,
    @Json(name = "unit") val unit: String
)
