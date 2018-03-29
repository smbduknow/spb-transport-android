package me.smbduknow.transport.presentation.main

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import me.smbduknow.mvpblueprint.mvp.MvpPresenter
import me.smbduknow.transport.domain.model.Vehicle
import me.smbduknow.transport.presentation.base.mvp.RendererMvpView
import me.smbduknow.transport.presentation.model.MapState


/**
 * Presenter for Main screen
 * */
interface MainMvpPresenter : MvpPresenter<MainMvpView> {

    fun onMapReady()

    fun onMapCameraChanged(target: LatLng,
                           bounds: LatLngBounds,
                           zoom: Float,
                           bearing: Float)

    fun onRequestUserLocation()

}

/**
 * View for Main screen
 * */
interface MainMvpView : RendererMvpView<MainViewState> {

}

/**
 * ViewState model for Main screen
 * */
data class MainViewState(
        val mapState: MapState,
        val userLocation: LatLng? = null,
        val vehicles: List<Vehicle> = emptyList(),
        val error: Throwable? = null
) : RendererMvpView.ViewState