package me.smbduknow.transport.presentation.main

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import me.smbduknow.transport.domain.MapInteractor
import me.smbduknow.transport.domain.model.Coordinates
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MapViewModel @Inject constructor(
        private val mapInteractor: MapInteractor
) : ViewModel() {

    val stateLiveData = MutableLiveData<MapViewState>()

    private val mapBoundsSubject : PublishSubject<LatLngBounds> = PublishSubject.create()
    private val locationSubject : PublishSubject<Boolean> = PublishSubject.create()

    init {
        stateLiveData.value = MapViewState(LatLng(59.9342802, 30.3350986))

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


        Observable.merge(locationObservable, vehiclesObservable)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { stateLiveData.value = it},
                        { Timber.e(it) }
                )
    }


    fun onUpdateMapScope(bounds: LatLngBounds) = mapBoundsSubject.onNext(bounds)

    fun onRequestUserLocation() = locationSubject.onNext(true)

    private fun getPrevState() = stateLiveData.value!!

    private fun requestData() = mapInteractor.getVehicles()
            .map { vehicles -> MapViewState(getPrevState().userLocation, vehicles) }
            .onErrorReturn { getPrevState().copy(error = it) }
            .subscribeOn(Schedulers.io())

    private fun requestLocation() = mapInteractor.getUserLocation()
            .map { LatLng(it.lat, it.lon) }
            .map { location -> MapViewState(location, getPrevState().vehicles) }
            .onErrorReturn { getPrevState().copy(error = it) }
            .subscribeOn(Schedulers.io())
}