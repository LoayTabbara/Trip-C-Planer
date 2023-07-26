package com.codecamp.tripcplaner.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor() : ViewModel() {
    private var packList = mutableListOf<String>()
    private var longPressed= mutableStateOf(false)


    fun setLongPressed(value: Boolean) {
        longPressed.value = value
    }
    fun getLongPressed(): Boolean {
        return longPressed.value
    }
    fun setList(value: MutableList<String>) {
        packList = value
    }

    fun getList(): MutableList<String> {
        return packList
    }
}