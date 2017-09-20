package me.smbduknow.transport

import com.urworld.android.ui.base.mvp.RendererMvpView
import me.smbduknow.mvpblueprint.mvp.MvpPresenter


/**
 * Presenter for Main screen
 * */
interface MainMvpPresenter : MvpPresenter<MainMvpView> {

    fun onGoogleServicesCheck(available: Boolean)

    fun onMapReady()

}

/**
 * View for Main screen
 * */
interface MainMvpView : RendererMvpView<MainViewState> {

    fun checkGoogleServices()

    fun prepareMaps()

}

/**
 * ViewState model for Main screen
 * */
data class MainViewState(
        val error: Throwable? = null
) : RendererMvpView.ViewState {

}