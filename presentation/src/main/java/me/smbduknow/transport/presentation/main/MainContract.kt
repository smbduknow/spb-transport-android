//package me.smbduknow.transport.presentation.main
//
//import com.google.android.gms.maps.model.LatLng
//import com.google.android.gms.maps.model.LatLngBounds
//import me.smbduknow.mvpblueprint.mvp.MvpPresenter
//import me.smbduknow.transport.domain.model.Route
//import me.smbduknow.transport.domain.model.Vehicle
//import me.smbduknow.transport.presentation.base.mvp.RendererMvpView
//import me.smbduknow.transport.presentation.model.MapState
//
//
///**
// * Presenter for Main screen
// * */
//interface MainMvpPresenter : MvpPresenter<MainMvpView> {
//
//    fun onMapReady()
//
//    fun onMapCameraChanged(target: LatLng,
//                           bounds: LatLngBounds,
//                           zoom: Float,
//                           bearing: Float)
//
//    fun onSuggestQuery(q: String)
//    fun onSuggestSelected(route: Route)
//
//    fun onRequestUserLocation()
//
//}
//
///**
// * View for Main screen
// * */
//interface MainMvpView : RendererMvpView<MainViewState> {
//
//    fun moveToPosition(target: LatLng)
//
//}
//
///**
// * ViewState model for Main screen
// * */
//data class MainViewState(
//        val mapState: MapState,
//        val userLocation: LatLng? = null,
//        val vehicles: List<Vehicle> = emptyList(),
//        val query: String = "",
//        val queryResults: List<Route> = emptyList(),
//        val error: Throwable? = null
//) : RendererMvpView.ViewState