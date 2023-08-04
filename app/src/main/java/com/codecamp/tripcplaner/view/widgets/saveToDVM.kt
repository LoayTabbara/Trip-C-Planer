package com.codecamp.tripcplaner.view.widgets

import com.codecamp.tripcplaner.viewModel.DetailViewModel
import com.codecamp.tripcplaner.viewModel.TravelInfoViewModel

fun saveToDVM(id: Int, travelInfoViewModel: TravelInfoViewModel, detailViewModel: DetailViewModel) {


    val trip = travelInfoViewModel.tripRepo.getById(id)

    detailViewModel.cities = trip.cities.keys.toMutableList()
    detailViewModel.activities = trip.activities
    detailViewModel.setDates(trip.startDate, trip.endDate)
    detailViewModel.setMeansOfTransport(trip.transportType)
    detailViewModel.setPackList(trip.packingList)
    detailViewModel.setNewTitle(trip.title)
}