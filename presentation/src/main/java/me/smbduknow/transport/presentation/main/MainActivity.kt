package me.smbduknow.transport.presentation.main

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import dagger.Lazy
import kotlinx.android.synthetic.main.activity_main.*
import me.smbduknow.mvpblueprint.BasePresenterActivity
import me.smbduknow.mvpblueprint.PresenterFactory
import me.smbduknow.transport.App
import me.smbduknow.transport.R
import me.smbduknow.transport.presentation.misc.PermissedAction
import javax.inject.Inject


class MainActivity : BasePresenterActivity<MainMvpPresenter, MainMvpView>(), OnMapReadyCallback, MainMvpView {

    private var mapAdapter: MapAdapter? = null

    private lateinit var nearbyAction: PermissedAction

    @Inject
    lateinit var lazyPresenter: Lazy<MainMvpPresenter>

    override fun onCreatePresenterFactory() = object : PresenterFactory<MainMvpPresenter>() {
        override fun create() = lazyPresenter.get()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        map_zoom_in.setOnClickListener { mapAdapter?.zoomIn() }
        map_zoom_out.setOnClickListener { mapAdapter?.zoomOut() }
        map_geolocation.setOnClickListener { nearbyAction.invoke(this) }

        nearbyAction = PermissedAction(Manifest.permission.ACCESS_FINE_LOCATION,
                { presenter?.onRequestUserLocation() },
                { Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show() }
        )

        App.graph.injectTo(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapAdapter = MapAdapter(this, googleMap).apply {
            setOnCameraMoveListener ( presenter::onMapCameraChanged )
        }
        presenter?.onMapReady()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        nearbyAction.handlePermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun render(viewState: MainViewState) {
        mapAdapter?.moveCamera(viewState.mapState.target, viewState.mapState.zoom, viewState.mapState.bearing)
        mapAdapter?.recycleMarkers()
        mapAdapter?.setMarkers(viewState.vehicles)
        viewState.userLocation?.let { mapAdapter?.setUserMarker(it) }
    }

    override fun moveToPosition(target:LatLng) {
        mapAdapter?.animateCamera(target)
    }

}
