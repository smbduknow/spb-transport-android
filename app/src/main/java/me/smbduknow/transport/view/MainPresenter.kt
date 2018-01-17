package me.smbduknow.transport.view

import com.google.android.gms.maps.model.LatLngBounds
import me.smbduknow.transport.base.rx.BaseViewStatePresenter
import me.smbduknow.transport.domain.MapInteractor
import rx.Observable
import rx.schedulers.Schedulers
import rx.subjects.PublishSubject
import java.util.*


class MainPresenter : BaseViewStatePresenter<MainMvpView, MainViewState>(),
        MainMvpPresenter {

    private val mapInteractor = MapInteractor()

    private val mapReadySubject : PublishSubject<Unit> = PublishSubject.create()
    private val vehiclesSubject : PublishSubject<LatLngBounds> = PublishSubject.create()
    private val locationSubject : PublishSubject<Unit> = PublishSubject.create()

    override fun onCreateObservable(): Observable<MainViewState> {

//        val locationObservable = locationSubject.asObservable()
//                .switchMap {  }

        val vehiclesObservable = vehiclesSubject.asObservable()
                .map { bounds -> String.format(Locale.US, "%.4f,%.4f,%.4f,%.4f",
                        bounds.southwest.longitude, bounds.southwest.latitude,
                        bounds.northeast.longitude, bounds.northeast.latitude
                ) }
                .switchMap { box -> requestData(listOf("bus","trolley","tram"), box) }

        val mapReadyObservable = mapReadySubject.asObservable()
                .first()
                .map { MainViewState() }

        return Observable.concat(mapReadyObservable, vehiclesObservable)
    }

    override fun onMapReady() = mapReadySubject.onNext(null)

    override fun onRequestVehicles(bounds: LatLngBounds) = vehiclesSubject.onNext(bounds)

    override fun onRequestUserLocation() = locationSubject.onNext(null)

    private fun requestData(types: List<String>, box: String): Observable<MainViewState>
            = mapInteractor.getVehicles(types, box)
            .map { vehicles -> MainViewState(vehicles) }
            .startWith( MainViewState() )
            .onErrorReturn { error -> MainViewState(emptyList(), error) }
            .subscribeOn(Schedulers.io())

}