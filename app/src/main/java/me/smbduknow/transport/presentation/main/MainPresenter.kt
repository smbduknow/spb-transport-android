package me.smbduknow.transport.presentation.main

import com.google.android.gms.maps.model.LatLngBounds
import me.smbduknow.transport.domain.MapInteractor
import me.smbduknow.transport.domain.model.Coordinates
import me.smbduknow.transport.presentation.base.rx.BaseViewStatePresenter
import rx.Observable
import rx.schedulers.Schedulers
import rx.subjects.PublishSubject


class MainPresenter : BaseViewStatePresenter<MainMvpView, MainViewState>(),
        MainMvpPresenter {

    private val mapInteractor = MapInteractor()

    private val mapReadySubject : PublishSubject<Unit> = PublishSubject.create()
    private val mapBoundsSubject : PublishSubject<LatLngBounds> = PublishSubject.create()
    private val locationSubject : PublishSubject<Unit> = PublishSubject.create()


    override fun onCreateObservable(): Observable<MainViewState> {

//        val locationObservable = locationSubject.asObservable()
//                .switchMap {  }

        val vehiclesObservable = mapBoundsSubject.asObservable()
                .doOnNext { bounds -> mapInteractor.setBounds(
                        Coordinates(bounds.southwest.latitude, bounds.southwest.longitude),
                        Coordinates(bounds.northeast.latitude, bounds.northeast.longitude)
                ) }
                .switchMap { requestData() }

        val mapReadyObservable = mapReadySubject.asObservable()
                .first()
                .map { MainViewState() }

        return Observable.concat(mapReadyObservable, vehiclesObservable)
    }

    override fun onMapReady() = mapReadySubject.onNext(null)
    override fun onMapBoundsChanged(bounds: LatLngBounds) = mapBoundsSubject.onNext(bounds)
    override fun onRequestUserLocation() = locationSubject.onNext(null)

    private fun requestData() = mapInteractor.getVehicles()
            .map { vehicles -> MainViewState(vehicles) }
            .startWith( MainViewState() )
            .onErrorReturn { error -> MainViewState(emptyList(), error) }
            .subscribeOn(Schedulers.io())

}