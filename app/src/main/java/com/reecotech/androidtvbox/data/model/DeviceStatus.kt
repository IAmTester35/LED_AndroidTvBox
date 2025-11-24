package com.reecotech.androidtvbox.data.model

import androidx.annotation.Keep


@Keep
data class DeviceStatus(
    val status: String? = null,
    val name: String? = null,
    val description: String? = null,
    val createDay: Long? = null,
    val updateDate: Long? = null,
    val approvedBy: String? = null
)
