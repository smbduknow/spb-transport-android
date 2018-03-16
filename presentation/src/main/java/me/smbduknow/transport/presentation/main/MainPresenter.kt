package me.smbduknow.transport.presentation.main

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import me.smbduknow.transport.domain.MapInteractor
import me.smbduknow.transport.domain.model.Coordinates
import me.smbduknow.transport.presentation.base.rx.BaseViewStatePresenter
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class MainPresenter @Inject constructor (
        private val mapInteractor: MapInteractor
) : BaseViewStatePresenter<MainMvpView, MainViewState>(), MainMvpPresenter {

    private val mapBoundsSubject : PublishSubject<LatLngBounds> = PublishSubject.create()
    private val locationSubject : PublishSubject<Boolean> = PublishSubject.create()


    override fun onCreateObservable(): Observable<MainViewState> {

        val initObservable = Observable.just(LatLng(59.9342802, 30.3350986))
                .map { MainViewState(it) }

        val locationObservable = locationSubject.hide()
                .switchMapSingle { requestLocation() }

        val vehiclesObservable = mapBoundsSubject.hide()
                .debounce(200, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .doOnNext { bounds -> mapInteractor.setBounds(
                        Coordinates(bounds.southwest.latitude, bounds.southwest.longitude),
                        Coordinates(bounds.northeast.latitude, bounds.northeast.longitude)
                ) }
                .switchMapSingle { requestData() }

        return initObservable.concatWith(Observable.merge(
                locationObservable,
                vehiclesObservable
        ))
    }

    override fun onMapBoundsChanged(bounds: LatLngBounds) = mapBoundsSubject.onNext(bounds)
    override fun onRequestUserLocation() = locationSubject.onNext(true)



    private fun requestData() = mapInteractor.getVehicles()
            .map { vehicles -> MainViewState(getState().userLocation, vehicles) }
            .onErrorReturn { getState().copy(error = it) }
            .subscribeOn(Schedulers.io())

    private fun requestLocation() = mapInteractor.getUserLocation()
            .map { LatLng(it.lat, it.lon) }
            .map { location -> MainViewState(location, getState().vehicles) }
            .onErrorReturn { getState().copy(error = it) }
            .subscribeOn(Schedulers.io())
}