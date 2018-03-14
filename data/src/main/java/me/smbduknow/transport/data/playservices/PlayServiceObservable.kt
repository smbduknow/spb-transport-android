package me.smbduknow.transport.data.playservices

import android.content.Context
import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.Api
import com.google.android.gms.common.api.GoogleApiClient
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.Disposables


abstract class PlayServiceObservable<T>(
        val ctx: Context,
        vararg services: Api<out Api.ApiOptions.NotRequiredOptions>
) : ObservableOnSubscribe<T> {

    private val services: List<Api<out Api.ApiOptions.NotRequiredOptions>> = services.toList()

    override fun subscribe(subscriber: ObservableEmitter<T>) {

        val apiClient = createApiClient(subscriber)
        try {
            apiClient.connect()
        } catch (ex: Throwable) {
            subscriber.onError(ex)
        }

        // do on unsubscribe
        subscriber.setDisposable(Disposables.fromAction {
            if (apiClient.isConnected || apiClient.isConnecting) {
                onUnsubscribed(apiClient)
                apiClient.disconnect()
            }
        })
    }

    protected abstract fun onApiClientReady(apiClient: GoogleApiClient, observer: ObservableEmitter<in T>)

    protected fun onUnsubscribed(apiClient: GoogleApiClient) {}


    private fun createApiClient(observer: ObservableEmitter<in T>): GoogleApiClient {
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
