package me.smbduknow.transport.base.rx

import android.support.annotation.CallSuper
import me.smbduknow.mvpblueprint.BasePresenter
import me.smbduknow.transport.base.mvp.RendererMvpView
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.subjects.BehaviorSubject
import timber.log.Timber

abstract class  BaseViewStatePresenter<V : RendererMvpView<VS>, VS : RendererMvpView.ViewState>
    : BasePresenter<V>() {

    private lateinit var viewStateSubscription: Subscription
    private lateinit var presenterSubscription: Subscription

    internal val viewStateSubject: BehaviorSubject<VS> = BehaviorSubject.create()

    protected fun getState(): VS {
        return viewStateSubject.value
    }

    abstract fun onCreateObservable(): Observable<VS>


    override final fun onCreate() {
        super.onCreate()
        presenterSubscription = onCreateObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { viewStateSubject.onNext(it) },
                        { Timber.e(it) }
                )
    }

    @CallSuper
    override fun onViewAttached(view: V) {
        super.onViewAttached(view)

        viewStateSubscription = viewStateSubject.asObservable()
                .subscribe { getView()?.render(it) }
    }

    @CallSuper
    override fun onViewDetached() {
        viewStateSubscription.unsubscribe()
        super.onViewDetached()
    }

    override final fun onDestroy() {
        presenterSubscription.unsubscribe()
        viewStateSubject.onCompleted()
        super.onDestroy()
    }

}