package com.codecamp.tripcplaner.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor() : ViewModel() {
    private var packList = mutableMapOf<String,Boolean>()
    private var transportMean= mutableStateOf("")
    private lateinit var  startDate: LocalDateTime
    private lateinit var endDate:LocalDateTime
    var activities= mutableListOf<String>()
    var cities= mutableListOf<String>()

    private var newTitle= mutableStateOf("")
    fun clearVM(){
        packList = mutableMapOf<String,Boolean>()
        transportMean= mutableStateOf("")
        startDate= LocalDateTime.now()
        endDate= LocalDateTime.now()
        activities= mutableListOf<String>()
        cities= mutableListOf<String>()
    }
    fun setPackList(value: MutableMap<String, Boolean>) {
        packList = value
    }
    fun setDates(start: LocalDateTime, end: LocalDateTime) {
        startDate = start
        endDate = end
    }
    fun getEndDate(): LocalDateTime{
        return endDate
    }
    fun getStartDate(): LocalDateTime {
        return startDate
    }
    fun getPackList(): MutableMap<String,Boolean> {
        return packList
    }
    fun setNewTitle(value: String) {
        val capValue= value.replaceFirstChar { char-> char.uppercase() }
        newTitle.value = capValue
    }
    fun getNewTitle(): String {
        return newTitle.value
    }

    fun setMeansOfTransport(value: String) {
        transportMean.value = value
    }
    fun getTransportMean(): String {
        return transportMean.value
    }
}