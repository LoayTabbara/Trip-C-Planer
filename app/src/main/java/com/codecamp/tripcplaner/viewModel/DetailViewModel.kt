package com.codecamp.tripcplaner.viewModel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor() : ViewModel() {
    var packList = mutableListOf<String>()
    fun setList(value: MutableList<String>) {
        packList = value
    }

    fun getList(): MutableList<String> {
        return packList
    }
}