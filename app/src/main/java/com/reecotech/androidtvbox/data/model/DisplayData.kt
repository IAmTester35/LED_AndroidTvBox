package com.reecotech.androidtvbox.data.model

import com.google.gson.annotations.SerializedName

data class DisplayData(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("value") val value: String,
    @SerializedName("unit") val unit: String
)
