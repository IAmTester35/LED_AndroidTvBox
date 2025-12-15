package com.reecotech.androidtvbox.data.remote

import com.reecotech.androidtvbox.data.model.StationResponse
import retrofit2.http.GET

import retrofit2.Response

interface StationApiService {
    @GET("api/v2/stations/all/latest")
    suspend fun getLatestStationData(): Response<StationResponse>
}
