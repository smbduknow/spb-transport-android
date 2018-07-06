package me.smbduknow.transport.presentation.main

import android.Manifest
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
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
import javax.inject.Inject


class MainActivity : BasePresenterActivity<MainMvpPresenter, MainMvpView>(), OnMapReadyCallback, MainMvpView {

    private var mapAdapter: MapAdapter? = null

    private val suggestAdapter by lazy { SuggestAdapter() }

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

        map_search_bar_suggests_list.apply {
            adapter = suggestAdapter
            layoutManager = LinearLayoutManager(context).apply { isAutoMeasureEnabled = true }
            setHasFixedSize(true)
        }

        map_search_edit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                presenter.onSearchQuery(s.toString())
            }
        })


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

        suggestAdapter.setItems(viewState.queryResults
                .filter { arrayOf("bus", "trolley", "tram").contains(it.typeLabel) })

    }

    override fun moveToPosition(target:LatLng) {
        mapAdapter?.animateCamera(target)
    }

}
