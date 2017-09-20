package me.smbduknow.transport.base.mvp

import android.support.annotation.UiThread
import me.smbduknow.mvpblueprint.mvp.MvpView

/**
 * Base interface for MvpViews that should store
 * */
interface RendererMvpView<in T : RendererMvpView.ViewState> : MvpView {

    /**
     * Implement this for describe ViewState model
     * */
    interface ViewState

    /**
     * Render current ViewState to UI
     * */
    @UiThread
    fun render(viewState: T)

}