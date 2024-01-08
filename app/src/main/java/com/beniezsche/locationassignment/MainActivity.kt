package com.beniezsche.locationassignment


import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.beniezsche.locationassignment.db.database.AppDatabase
import com.beniezsche.locationassignment.db.models.TrackingSession
import com.beniezsche.locationassignment.services.LocationService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var button: AppCompatButton
    private var isTracking = false
    private val MY_PERMISSIONS_REQUEST_LOCATION = 101;

    private lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.trackingButton)

        toolbar = findViewById(R.id.toolbar)

        toolbar.setOnMenuItemClickListener(object : Toolbar.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                if (item?.itemId == R.id.locationHistory) {
                    val intent = Intent(this@MainActivity, SessionsActivity::class.java)
                    startActivity(intent)
                    return true
                }
                return true
            }

        })

        for(notification in getSystemService(NotificationManager::class.java).activeNotifications) {
            if(notification.notification.channelId == "Location-Assignment-Background") {
                isTracking = true
                button.text = "Stop Tracking Location"
                button.setBackgroundColor(getColor(R.color.red))
            }
        }

        button.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf( android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.POST_NOTIFICATIONS), MY_PERMISSIONS_REQUEST_LOCATION);
            }
            else {
                if (isTracking) {
                    stopTracking()
                }
                else {
                    startTracking()
                }
            }
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }



    override fun onMapReady(googleMap: GoogleMap) {
        // Add a marker in null island,
        // and move the map's camera to the same location.
        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(0.0, 0.0))
                .title("Marker")
        )

        // Register a BroadcastReceiver to receive updates from the service
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                // Handle the received data here
                val lat = intent.getDoubleExtra("lat", 0.0)
                val long = intent.getDoubleExtra("long", 0.0)
                // Update UI or perform any other actions

                val currentLoc = LatLng(lat, long)

                // Move the camera instantly to Sydney with a zoom of 15.
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 16f))

                // Zoom in, animating the camera.
                googleMap.animateCamera(CameraUpdateFactory.zoomIn())

                googleMap.clear()

                val circle: Circle = googleMap.addCircle(
                    CircleOptions()
                        .center(currentLoc)
                        .radius(3.5)
                        .strokeColor(Color.RED)
                        .fillColor(Color.RED)
                )
            }
        }

        val filter = IntentFilter("location-update-event")
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            startTracking()
        }
    }

    fun startTracking() {

        val session = TrackingSession()
        session.sessionStart = System.currentTimeMillis()

        CoroutineScope(Dispatchers.IO).launch {
            val id = AppDatabase.getDatabase(this@MainActivity)?.trackingSessionDao()?.insertSession(session)
            val intent = Intent(this@MainActivity, LocationService::class.java)

            intent.putExtra("session_id", id)

            startForegroundService(intent)
            isTracking = true

            runOnUiThread {
                startTrackingState()
            }
        }
    }

    fun stopTracking() {
        val intent = Intent(this, LocationService::class.java)
        stopService(intent)
        isTracking = false
        stopTrackingState()
    }

    fun stopTrackingState() {
        button.text = "Start Tracking Location"
        button.setBackgroundColor(getColor(R.color.blue))
    }

    fun startTrackingState() {
        button.text = "Stop Tracking Location"
        button.setBackgroundColor(getColor(R.color.red))
    }

    override fun onResume() {
        super.onResume()
        for(notification in getSystemService(NotificationManager::class.java).activeNotifications) {
            if(notification.notification.channelId == "Location-Assignment-Background") {
                isTracking = true
                startTrackingState()
            }
        }
    }
}