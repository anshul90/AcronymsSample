package com.example.acronyms.data.serviceRepository

import com.example.acronyms.data.api.ApiHelper

class MainRepository(private val apiHelper : ApiHelper) {
    suspend fun getAcromine(sf : String) = apiHelper.getAcromine(sf)
}
