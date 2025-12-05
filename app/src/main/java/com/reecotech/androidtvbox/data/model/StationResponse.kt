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
    @SerialName("parameters") val parameters: List<Parameter>
)

@Serializable
data class Parameter(
    @SerialName("parameterId") val parameterId: Int,
    @SerialName("parameterName") val parameterName: String,
    @SerialName("value") val value: Double?,
    @SerialName("alarmLevel") val alarmLevel: Int?,
    @SerialName("timestamp") val timestamp: String?
)
