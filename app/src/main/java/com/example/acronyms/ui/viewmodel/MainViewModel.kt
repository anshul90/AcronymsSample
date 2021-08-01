package com.example.acronyms.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.acronyms.data.serviceRepository.MainRepository
import com.example.acronyms.utils.Resource
import kotlinx.coroutines.Dispatchers

class MainViewModel(private val mainRepository : MainRepository) : ViewModel() {

    fun getAcronyms(acronymInput : String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getAcromine(acronymInput)))
        } catch (exception : Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}
