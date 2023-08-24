package com.codecamp.tripcplaner.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor() : ViewModel()  {
    private var isDark= mutableStateOf(false)


    fun setIsDark(colorValue:Boolean){
        isDark.value=colorValue
    }
    fun getIsDark(): Boolean{
        return isDark.value
    }
}