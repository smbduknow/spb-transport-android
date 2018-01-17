package me.smbduknow.transport.view

import com.google.android.gms.maps.model.LatLngBounds
import me.smbduknow.mvpblueprint.mvp.MvpPresenter
import me.smbduknow.transport.base.mvp.RendererMvpView
import me.smbduknow.transport.model.Vehicle


/**
 * Presenter for Main screen
 * */
interface MainMvpPresenter : MvpPresenter<MainMvpView> {

    fun onMapReady()

    fun onRequestVehicles(bounds: LatLngBounds)

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
        val vehicles: List<Vehicle> = emptyList(),
        val error: Throwable? = null
) : RendererMvpView.ViewState