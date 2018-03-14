package me.smbduknow.transport.presentation.main

import com.google.android.gms.maps.model.LatLng
import me.smbduknow.transport.domain.MapInteractor
import me.smbduknow.transport.domain.model.Coordinates
import me.smbduknow.transport.domain.model.MapScope
import me.smbduknow.transport.presentation.base.rx.BaseViewStatePresenter
import rx.Observable
import rx.schedulers.Schedulers
import rx.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class MainPresenter @Inject constructor (
        private val mapInteractor: MapInteractor
) : BaseViewStatePresenter<MainMvpView, MainViewState>(), MainMvpPresenter {

    private val mapReadySubject : PublishSubject<Unit> = PublishSubject.create()
    private val mapScopeSubject : PublishSubject<MapScope> = PublishSubject.create()
    private val locationSubject : PublishSubject<Unit> = PublishSubject.create()


    override fun onCreateObservable(): Observable<MainViewState> {

        val locationObservable = locationSubject.asObservable()
                .switchMap { requestLocation() }

        val vehiclesObservable = mapScopeSubject.asObservable()
                .debounce(200, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .doOnNext { mapInteractor.setMapScope(it) }
                .switchMap { requestData() }

        val initObservable = mapReadySubject.asObservable()
                .first()
                .map { MainViewState(MapScope(
                        Coordinates(59.9342802, 30.3350986),
                        Coordinates(59.9342802, 30.3350986),
                        Coordinates(59.9342802, 30.3350986),
                        0f, 13.5f
                )) }

        return initObservable.concatWith(Observable.merge(
                vehiclesObservable,
                locationObservable
        ))
    }

    override fun onMapReady() = mapReadySubject.onNext(null)
    override fun onMapBoundsChanged(scope: MapScope) = mapScopeSubject.onNext(scope)
    override fun onRequestUserLocation() = locationSubject.onNext(null)

    private fun requestData() = mapInteractor.getVehicles()
            .map { vehicles -> MainViewState(getState().mapScope, getState().userLocation, vehicles) }
            .onErrorReturn { error -> MainViewState(getState().mapScope, getState().userLocation, getState().vehicles, error) }
            .subscribeOn(Schedulers.io())

    private fun requestLocation() = mapInteractor.getUserLocation()
            .map { LatLng(it.lat, it.lon) }
            .map { location -> MainViewState(getState().mapScope, location, getState().vehicles) }
            .onErrorReturn { error -> MainViewState(getState().mapScope, getState().userLocation, getState().vehicles, error) }
            .subscribeOn(Schedulers.io())
}