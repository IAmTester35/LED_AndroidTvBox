package com.reecotech.androidtvbox.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StationResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("data") val data: List<Station>
)

@Serializable
data class Station(
    @SerialName("stationId") val stationId: String,
    @SerialName("stationName") val stationName: String,
    @SerialName("connectionStatus") val connectionStatus: String,
    @SerialName("parameters") val parameters: List<Parameter> = emptyList()
)

@Serializable
data class Parameter(
    @SerialName("parameterId") val parameterId: Int,
    @SerialName("parameterName") val parameterName: String,
    @SerialName("value") val value: Double? = null,
    @SerialName("alarmLevel") val alarmLevel: Int? = null,
    @SerialName("timestamp") val timestamp: String? = null,
    @SerialName("isEnable") val isEnable: Boolean? = true
)
