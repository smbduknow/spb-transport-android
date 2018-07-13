package me.smbduknow.transport.presentation.main

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import me.smbduknow.transport.domain.MapInteractor
import me.smbduknow.transport.domain.model.Coordinates
import me.smbduknow.transport.domain.model.Route
import me.smbduknow.transport.presentation.base.rx.BaseViewStatePresenter
import me.smbduknow.transport.presentation.model.MapState
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class MainPresenter @Inject constructor (
        private val mapInteractor: MapInteractor
) : BaseViewStatePresenter<MainMvpView, MainViewState>(), MainMvpPresenter {


    private val mapReadySubject : PublishSubject<Boolean> = PublishSubject.create()
    private val mapBoundsSubject : PublishSubject<LatLngBounds> = PublishSubject.create()
    private val locationSubject : PublishSubject<Boolean> = PublishSubject.create()

    private val mapStateSubject : BehaviorSubject<MapState> = BehaviorSubject.create()
    private val querySubject : PublishSubject<String> = PublishSubject.create()

    override fun onCreateObservable(): Observable<MainViewState> {

        val initObservable = Observable.just(LatLng(59.9342802, 30.3350986))
                .map { MapState(it, 0f, 13f) }
                .map { MainViewState(it) }

        val mapReadyObservable = mapReadySubject.hide()
                .filter{viewStateSubject.hasValue()}
                .map { getState().copy(mapState = mapStateSubject.value) }

        val locationObservable = locationSubject.hide()
                .switchMapSingle { requestLocation() }

        val vehiclesObservable = mapBoundsSubject.hide()
                .debounce(200, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .doOnNext { bounds -> mapInteractor.setBounds(
                        Coordinates(bounds.southwest.latitude, bounds.southwest.longitude),
                        Coordinates(bounds.northeast.latitude, bounds.northeast.longitude)
                ) }
                .switchMapSingle { requestVehicles() }

        val searchObservable = querySubject.hide()
                .debounce(300, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .flatMap { query ->
                    Single.concatArray(
                            Single.just(getState().copy(query = query)),
                            requestSuggestions(query)
                    ).toObservable()
                }

        return initObservable.concatWith(Observable.merge(
                mapReadyObservable,
                vehiclesObservable,
                locationObservable,
                searchObservable
        ))
    }

    override fun onMapReady() = mapReadySubject.onNext(true)
    override fun onMapCameraChanged(target: LatLng, bounds: LatLngBounds, zoom: Float, bearing: Float) {
        mapBoundsSubject.onNext(bounds)
        mapStateSubject.onNext(MapState(target, bearing, zoom))
    }

    override fun onSuggestQuery(q: String) {
        querySubject.onNext(q)
    }

    override fun onSuggestSelected(route: Route) {
        mapInteractor.setSelectedRoutes(listOf(route.id))
        querySubject.onNext("")
    }
    override fun onRequestUserLocation() = locationSubject.onNext(true)


    // private

    private fun requestVehicles() = mapInteractor.getVehicles()
            .map { vehicles -> getState().copy(mapState = mapStateSubject.value, vehicles = vehicles) }
            .onErrorReturn { error -> getState().copy(error = error) }
            .subscribeOn(Schedulers.io())

    private fun requestLocation() = mapInteractor.getUserLocation()
            .map { LatLng(it.lat, it.lon) }
            .doOnSuccess { view?.moveToPosition(it) }
            .map { location -> getState().copy(mapState = mapStateSubject.value, userLocation = location) }
            .onErrorReturn { error -> getState().copy(error = error) }
            .subscribeOn(Schedulers.io())

    private fun requestSuggestions(q:String) =
            if(q.isNotBlank()) {
                mapInteractor.searchRouteSuggestions(q).map { getState().copy(query = q, queryResults = it) }
            } else {
                Single.just(getState().copy(query = q, queryResults = emptyList()))
            }
}