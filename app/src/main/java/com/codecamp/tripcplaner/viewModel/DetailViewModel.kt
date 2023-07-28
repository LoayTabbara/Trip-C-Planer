package com.codecamp.tripcplaner.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor() : ViewModel() {
    private var packList = mutableListOf<String>()
    private var isDeleted= mutableStateOf(false)
    private var activity= mutableStateOf("Walk")


    fun setIsDeleted(value: Boolean) {
        isDeleted.value = value
    }
    fun getIsDeleted(): Boolean {
        return isDeleted.value
    }
    fun setPackList(value: MutableList<String>) {
        packList = value
    }

    fun getPackList(): MutableList<String> {
        return packList
    }
    fun setActivity(value: String) {
        activity.value = value
    }
    fun getActivity(): String {
        return activity.value
    }
}