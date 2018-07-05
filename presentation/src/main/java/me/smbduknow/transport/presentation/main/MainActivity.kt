package me.smbduknow.transport.presentation.main

import android.Manifest
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.core.view.isVisible
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
import me.smbduknow.transport.presentation.misc.dismissKeyboard
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

//        map_overlay.visibility = View.GONE
////        map_search_edit.setOnClickListener {
////            map_overlay.visibility = if(map_overlay.isVisible) View.GONE else View.VISIBLE
////        }

        map_search_edit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                presenter.onSearchQuery(s.toString())
            }
        })

        map_search_bar_suggests_test.setOnClickListener {
            presenter.onSuggestSelected(0)
            map_search_edit.clearComposingText()
            dismissKeyboard(map_search_edit)
        }

        nearbyAction = PermissedAction(Manifest.permission.ACCESS_FINE_LOCATION,
                { presenter?.onRequestUserLocation() },
                { Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show() }
        )

        App.graph.injectTo(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapAdapter = MapAdapter(this, googleMap).apply {
            setOnCameraMoveListener ( presenter::onMapCameraChanged )
//            setOnVehicleClickListener ( presenter::onVehicleSelected )
        }
        presenter?.onMapReady()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        nearbyAction.handlePermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun render(viewState: MainViewState) {
        mapAdapter?.moveCamera(viewState.mapState.target, viewState.mapState.zoom, viewState.mapState.bearing)
        mapAdapter?.recycleMarkers(fullRefresh = true)
        mapAdapter?.setMarkers(viewState.vehicles)
        viewState.userLocation?.let { mapAdapter?.setUserMarker(it) }

        with(viewState.queryResults.isNotEmpty()) {
            map_overlay.isVisible = this
            map_search_bar_suggests_wrapper.isVisible = this
        }

        viewState.queryResults.firstOrNull()?.let {
            map_search_bar_suggests_test.text = "${it.label} ${it.typeLabel}"
        }

    }

    override fun moveToPosition(target:LatLng) {
        mapAdapter?.animateCamera(target)
    }

}
