package com.reecotech.androidtvbox.data.remote

import com.reecotech.androidtvbox.data.model.StationResponse
import retrofit2.http.GET

interface StationApiService {
    @GET("api/v2/stations/all/latest")
    suspend fun getLatestStationData(): StationResponse
}
