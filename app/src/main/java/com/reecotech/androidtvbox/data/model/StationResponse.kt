package com.reecotech.androidtvbox.data.model

import com.google.gson.annotations.SerializedName

data class StationResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<Station>
)

data class Station(
    @SerializedName("stationId") val stationId: String,
    @SerializedName("stationName") val stationName: String,
    @SerializedName("connectionStatus") val connectionStatus: String,
    @SerializedName("parameters") val parameters: List<Parameter>
)

data class Parameter(
    @SerializedName("parameterId") val parameterId: Int,
    @SerializedName("parameterName") val parameterName: String,
    @SerializedName("value") val value: Double?,
    @SerializedName("timestamp") val timestamp: String?
)
