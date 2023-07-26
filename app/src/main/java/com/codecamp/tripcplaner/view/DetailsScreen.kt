package com.codecamp.tripcplaner.view

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.codecamp.tripcplaner.viewModel.DetailViewModel


@Composable
fun DetailsScreen(navController: NavController, viewModel: DetailViewModel){

    val list= viewModel.getList();

Text(text = list.toString())



}