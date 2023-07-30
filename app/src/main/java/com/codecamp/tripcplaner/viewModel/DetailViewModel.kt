package com.codecamp.tripcplaner.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor() : ViewModel() {
    private var packList = mutableListOf<String>()
    private var transportMean= mutableStateOf("")
    private lateinit var  startDate: LocalDate
    private lateinit var endDate:LocalDate
     var activities= mutableListOf<String>()

private var newTitle= mutableStateOf("")

    fun setPackList(value: MutableList<String>) {
        packList = value
    }
    fun setDates(start: LocalDate, end: LocalDate) {
        startDate = start
        endDate = end
    }
    fun getEndDate(): LocalDate {
        return endDate
    }
    fun getStartDate(): LocalDate {
        return startDate
    }
    fun getPackList(): MutableList<String> {
        return packList
    }
    fun setNewTitle(value: String) {
        val capValue= value.replaceFirstChar { char-> char.uppercase() }
        newTitle.value = capValue
    }
    fun getNewTitle(): String {
        return newTitle.value
    }

    fun setTransportMean(value: String) {
        transportMean.value = value
    }
    fun getTransportMean(): String {
        return transportMean.value
    }
}