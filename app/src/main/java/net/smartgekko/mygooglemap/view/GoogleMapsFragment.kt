package net.smartgekko.mygooglemap.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import net.smartgekko.mygooglemap.*
import net.smartgekko.mygooglemap.viewmodel.AppState
import net.smartgekko.mygooglemap.viewmodel.GoogleMapsViewModel

class GoogleMapsFragment : Fragment() {
    private lateinit var viewModel: GoogleMapsViewModel
    private lateinit var map: GoogleMap
    private val markers: ArrayList<Marker> = arrayListOf()
    private lateinit var searchAddress: EditText
    private lateinit var buttonClear: TextView
    private lateinit var buttonAnty: TextView
    private lateinit var loadingLayout: LinearLayout
    private lateinit var rootLayout: ConstraintLayout
    private lateinit var seekBar: SeekBar
    private lateinit var currentPos: LatLng

    private val callback = OnMapReadyCallback { googleMap ->
        val initialPlace = LatLng(INIT_LAT, INIT_LNG)
        map = googleMap
        map.mapType=GoogleMap.MAP_TYPE_NORMAL
        markers.clear()

        setMarker(initialPlace)?.let { markers.add(it) }

        moveCameratoMarkerWithZoom(initialPlace, INIT_ZOOM)

        map.setOnMapClickListener { latLng ->
            clearMap()
            setMarker(latLng)?.let { markers.add(it) }
            moveCameratoMarkerWithZoom(latLng, seekBar.progress.toFloat())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        searchAddress = container!!.findViewById(R.id.searchAddress)
        buttonClear = container.findViewById(R.id.buttonClear)
        buttonAnty = container.findViewById(R.id.buttonAnty)
        loadingLayout = container.findViewById(R.id.loadingLayout)
        rootLayout = container.findViewById(R.id.rootLayout)
        seekBar = container.findViewById(R.id.seekBar)
        return inflater.inflate(R.layout.fragment_google_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        viewModel = ViewModelProvider(this).get(GoogleMapsViewModel::class.java)
        viewModel.getLiveData().observe(viewLifecycleOwner, { renderData(it) })
        lifecycle.addObserver(viewModel)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                map.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        currentPos,
                        progress.toFloat()
                    )
                )
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        searchAddress.setImeOptions(EditorInfo.IME_ACTION_SEARCH)
        searchAddress.setRawInputType(InputType.TYPE_CLASS_TEXT)
        searchAddress.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (searchAddress.text.toString().isNotBlank()) {
                    clearMap()
                    viewModel.getLocationByAddress(searchAddress.text.toString())
                } else {
                    showSnack("Search text field is empty.\nPlease input text line for location search")
                }
                val imm =
                    activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(v.windowToken, 0)
                true
            } else {
                false
            }
        }

        buttonClear.setOnClickListener {
            searchAddress.setText("")
            resetMap()
            /*
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.toggleSoftInput(SHOW_FORCED,0)
             */
        }

        buttonAnty.setOnClickListener {
            if (markers.size == 1) {
                goToAntipod()
            } else {
                showSnack("Where are no initial point setted.\nPlease use New search, or tap on Initial point")
            }
        }
    }


    private fun clearMap() {
        markers.clear()
        map.clear()
        currentPos = LatLng(INIT_LAT, INIT_LNG)
        seekBar.progress = INIT_ZOOM.toInt()
    }

    private fun resetMap() {
        markers.clear()
        map.clear()
        currentPos = LatLng(INIT_LAT, INIT_LNG)
        seekBar.progress = INIT_ZOOM.toInt()
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                currentPos,
                INIT_ZOOM
            )
        )
    }

    private fun goToAntipod() {
        val aLat = 0 - map.cameraPosition.target.latitude
        val aLng = 0 - (180 - map.cameraPosition.target.longitude)
        val antiPoint = LatLng(aLat, aLng)
        setMarker(antiPoint)?.let { markers.add(it) }
        moveCameratoMarkerWithZoom(antiPoint, INIT_ZOOM)
        seekBar.progress = INIT_ZOOM.toInt()
    }

    private fun setMarker(
        location: LatLng
    ): Marker? {
        val currentMarker = map.addMarker(
            MarkerOptions()
                .position(location)
                .title("")
                .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromDrawable(R.drawable.ic_map_marker)))
        )
        currentPos=location
        return currentMarker
    }

    private fun moveCameratoMarkerWithZoom(location: LatLng, zoom: Float) {
        view?.post {
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    location,
                    zoom
                )
            )
        }
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Success -> {
                val addressData = appState.mapObject.displayName
                val lon = appState.mapObject.lon
                val lat = appState.mapObject.lat
                loadingLayout.visibility = View.GONE
                setData(lon, lat, addressData)
            }
            is AppState.Loading -> {
                loadingLayout.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                loadingLayout.visibility = View.GONE
                appState.error.message?.let { setDataError(it) }
            }
        }
    }

    private fun setData(lon: Double, lat: Double, addressData: String) {
        searchAddress.setText(addressData)
        setMarker(LatLng(lat, lon))?.let { markers.add(it) }
        moveCameratoMarkerWithZoom(LatLng(lat, lon), seekBar.progress.toFloat())
    }

    private fun setDataError(errorMsg: String) {
        showSnack("Search error ( $errorMsg ).\nPlease input correct text for location search")
    }

    private fun getBitmapFromDrawable(drawableIcon: Int): Bitmap {
        val drawable = context?.let { ContextCompat.getDrawable(it, drawableIcon) }
        drawable!!.setBounds(
            0,
            0,
            drawable.intrinsicWidth,
            drawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.draw(canvas)

        return bitmap
    }

    private fun showSnack(msg: String) {
        Snackbar.make(
            rootLayout,
            msg,
            Snackbar.LENGTH_LONG
        ).show()
    }
}


