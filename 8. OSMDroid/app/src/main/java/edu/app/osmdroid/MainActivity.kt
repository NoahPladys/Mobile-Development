package edu.app.osmdroid

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.google.android.material.snackbar.Snackbar
import edu.app.osmdroid.databinding.ActivityMainBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.osmdroid.config.Configuration
import org.osmdroid.config.Configuration.getInstance
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.OverlayItem
import java.io.IOException
import java.lang.StringBuilder
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var searchField: TextView
    private lateinit var searchButton: Button
    private lateinit var clearButton: Button
    private lateinit var mapView: MapView
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Configuration.getInstance().userAgentValue = BuildConfig.LIBRARY_PACKAGE_NAME
        checkPermissions()

        mapView = binding.mapView
        searchButton = binding.searchButton
        clearButton = binding.clearButton
        searchField = binding.searchField

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        val mReceive: MapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                Snackbar.make(binding.root, "Lat= + $p.latitude.toString(), Lon=$p.longitude.toString()", Snackbar.LENGTH_LONG).show()
                return true
            }
            override fun longPressHelper(p: GeoPoint): Boolean {
                Snackbar.make(binding.root, "Long press", Snackbar.LENGTH_LONG).show()
                return true
            }
        }
        mapView.overlays.add(MapEventsOverlay(mReceive))
        mapView.controller.setZoom(16.0)

        // default = Ellermanstraat
        val center = GeoPoint(51.2301298, 4.4117949)
        mapView.controller.setCenter(center)
        addMarker(center)
        mapView.invalidate() // redraw

        searchButton.setOnClickListener {
            if(searchField.text.toString() == "")
                return@setOnClickListener;

            val client = OkHttpClient()

            val urlSearch = getString(R.string.urlSearch)
            val stringSearch = URLEncoder.encode(searchField.text.toString(), "UTF-8")

            val request = Request.Builder()
                .url("$urlSearch)$stringSearch&format=json")
                .build()

            client.newCall(request).enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Snackbar.make(binding.root, e.toString(), Snackbar.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")

                        val parser: Parser = Parser.default()
                        val jsonString = StringBuilder(response.body!!.string())

                        val addresses = parser.parse(jsonString) as JsonArray<JsonObject>
                        val lat = addresses[0]["lat"] as String
                        val lon = addresses[0]["lon"] as String

                        runOnUiThread {
                            val address = GeoPoint(lat.toDouble(), lon.toDouble())
                            addMarker(address)
                            mapView.controller.setCenter(address)
                        }
                    }
                }
            })
            hideSoftKeyBoard()
        }

        clearButton.setOnClickListener {
            mapView.overlays.clear()
            mapView.invalidate()
        }

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Snackbar.make(binding.root, "GPS not enabled", Snackbar.LENGTH_LONG).show()
        } else {
            locationListener = MyLocationListener()
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                10f,
                locationListener
            )
        }
    }

    private fun hideSoftKeyBoard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if(imm.isAcceptingText) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    private fun addMarker(g: GeoPoint) {
        val myLocationOverlayItem = OverlayItem("Here", "Current Position", g)
        val myCurrentLocationMarker: Drawable? = ResourcesCompat.getDrawable(
            resources, R.drawable.marker, null
        )
        myLocationOverlayItem.setMarker(myCurrentLocationMarker)
        val items = ArrayList<OverlayItem>()
        items.add(myLocationOverlayItem)
        val mOverlay = ItemizedOverlayWithFocus(this, items,
            object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem?> {
                override fun onItemSingleTapUp(index: Int, item: OverlayItem?): Boolean {
                    Snackbar.make(binding.root, "You tapped me!", Snackbar.LENGTH_LONG).show()
                    return true
                }
                override fun onItemLongPress(index: Int, item: OverlayItem?): Boolean {
                    return true
                }
            })
        mapView.overlays.add(mOverlay)
        mapView.invalidate()
    }

    // START PERMISSION CHECK
    private val REQUESTCODE = 100
    private fun checkPermissions() {
        val permissions: MutableList<String> = ArrayList()
        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }*/
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (permissions.isNotEmpty()) {
            val params = permissions.toTypedArray()
            requestPermissions(params, REQUESTCODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUESTCODE -> {
                val perms: MutableMap<String, Int> = HashMap()
                // fill with results
                var i = 0
                while(i < permissions.size) {
                    perms[permissions[i]] = grantResults[i]
                    i++
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    } // END PERMISSION CHECK

    private inner class MyLocationListener : LocationListener {
        override fun onLocationChanged(loc: Location) {
            //mapView.controller.setCenter(GeoPoint(loc.latitude, loc.longitude))
            //mapView.invalidate()
            Snackbar.make(binding.root, "Location changed to $loc.latitude / $loc.longitude", Snackbar.LENGTH_LONG).show()
        }
    }
}