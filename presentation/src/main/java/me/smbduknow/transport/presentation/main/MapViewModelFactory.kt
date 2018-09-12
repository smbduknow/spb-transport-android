package me.smbduknow.transport.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.smbduknow.transport.domain.MapInteractor
import javax.inject.Inject

class MapViewModelFactory @Inject constructor (
        private val mapInteractor: MapInteractor
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(mapInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}