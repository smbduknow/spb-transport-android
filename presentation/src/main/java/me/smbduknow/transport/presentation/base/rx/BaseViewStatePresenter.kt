package me.smbduknow.transport.presentation.base.rx

import android.support.annotation.CallSuper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import me.smbduknow.mvpblueprint.BasePresenter
import me.smbduknow.transport.presentation.base.mvp.RendererMvpView
import timber.log.Timber

abstract class  BaseViewStatePresenter<V : RendererMvpView<VS>, VS : RendererMvpView.ViewState>
    : BasePresenter<V>() {

    private lateinit var viewStateSubscription: Disposable
    private lateinit var presenterSubscription: Disposable

    internal val viewStateSubject: BehaviorSubject<VS> = BehaviorSubject.create()

    protected fun getState(): VS = viewStateSubject.value

    abstract fun onCreateObservable(): Observable<VS>


    final override fun onCreate() {
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

        viewStateSubscription = viewStateSubject.hide()
                .subscribe { getView()?.render(it) }
    }

    @CallSuper
    override fun onViewDetached() {
        viewStateSubscription.dispose()
        super.onViewDetached()
    }

    final override fun onDestroy() {
        presenterSubscription.dispose()
        viewStateSubject.onComplete()
        super.onDestroy()
    }

}