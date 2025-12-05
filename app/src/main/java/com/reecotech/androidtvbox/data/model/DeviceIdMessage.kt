package com.reecotech.androidtvbox.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceIdMessage(
    @SerialName("deviceId") val deviceId: String
)
