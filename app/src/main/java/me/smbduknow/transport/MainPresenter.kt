package me.smbduknow.transport

import com.urworld.android.ui.base.rx.BaseViewStatePresenter
import rx.Observable
import rx.subjects.PublishSubject


class MainPresenter : BaseViewStatePresenter<MainMvpView, MainViewState>(),
        MainMvpPresenter {

    private val googleServicesCheckedSubject : PublishSubject<Boolean> = PublishSubject.create()
    private val mapReadySubject : PublishSubject<Boolean> = PublishSubject.create()

    override fun onCreateObservable(): Observable<MainViewState> {

        val mapReadyObservable = mapReadySubject.asObservable()
                .switchMap { requestData() }

        return mapReadyObservable
    }

    override fun onGoogleServicesCheck(available: Boolean) = googleServicesCheckedSubject.onNext(available)

    override fun onMapReady() = mapReadySubject.onNext(true)

    private fun requestData(): Observable<MainViewState> {
        return Observable.just(MainViewState())
    }

}