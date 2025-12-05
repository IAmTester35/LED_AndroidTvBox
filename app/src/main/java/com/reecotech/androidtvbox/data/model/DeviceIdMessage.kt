package com.reecotech.androidtvbox.data.model

import com.google.gson.annotations.SerializedName

data class DeviceIdMessage(
    @SerializedName("deviceId") val deviceId: String
)
