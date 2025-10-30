package com.reecotech.androidtvbox.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeviceIdMessage(
    @Json(name = "deviceId") val deviceId: String
)
