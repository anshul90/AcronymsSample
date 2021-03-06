package com.example.acronyms.data.api

import com.example.acronyms.data.model.AcronymsResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API service
 */
interface ApiService {
    @GET("software/acromine/dictionary.py")
    suspend fun getAcromine(@Query("sf") sf: String): List<AcronymsResponse>
}
