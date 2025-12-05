package com.reecotech.androidtvbox.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DisplayData(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("value") val value: String,
    @SerialName("unit") val unit: String
)
