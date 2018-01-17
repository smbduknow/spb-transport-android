package me.smbduknow.transport.data.playservices

import android.content.Context
import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.Api
import com.google.android.gms.common.api.GoogleApiClient
import rx.Observable
import rx.Observer
import rx.Subscriber
import rx.subscriptions.Subscriptions

abstract class PlayServiceObservable<T>(
        val ctx: Context,
        vararg services: Api<out Api.ApiOptions.NotRequiredOptions>
) : Observable.OnSubscribe<T> {

    private val services: List<Api<out Api.ApiOptions.NotRequiredOptions>> = services.toList()

    override fun call(subscriber: Subscriber<in T>) {

        val apiClient = createApiClient(subscriber)
        try {
            apiClient.connect()
        } catch (ex: Throwable) {
            subscriber.onError(ex)
        }

        // do on unsubscribe
        subscriber.add(Subscriptions.create {
            if (apiClient.isConnected || apiClient.isConnecting) {
                onUnsubscribed(apiClient)
                apiClient.disconnect()
            }
        })
    }

    protected abstract fun onApiClientReady(apiClient: GoogleApiClient, observer: Observer<in T>)

    protected fun onUnsubscribed(apiClient: GoogleApiClient) {}


    private fun createApiClient(observer: Subscriber<in T>): GoogleApiClient {
        val callbackListener = ApiClientConnectionCallbacks()
        val apiClientBuilder = GoogleApiClient.Builder(ctx).apply {
            addConnectionCallbacks(callbackListener)
            addConnectionCallbacks(callbackListener)
            for (service in services) addApi(service)
        }
        val apiClient = apiClientBuilder.build()

        callbackListener.onConnect = { onApiClientReady(apiClient, observer) }
        callbackListener.onError = { observer.onError(it) }

        return apiClient
    }



    private class ApiClientConnectionCallbacks : GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

        var onConnect: () -> Unit = {}
        var onError: (e: Throwable) -> Unit = {}

        override fun onConnected(bundle: Bundle?) {
            try {
                onConnect()
            } catch (e: Throwable) {
                onError(e)
            }
        }

        override fun onConnectionSuspended(cause: Int) {
            onError(Exception("Google API error (code $cause)"))
        }

        override fun onConnectionFailed(connectionResult: ConnectionResult) {
            onError(Exception("Error connecting to GoogleApiClient. ${connectionResult.errorMessage}"))
        }
    }

}
