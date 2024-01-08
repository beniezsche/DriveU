package com.beniezsche.locationassignment.services

import android.R
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.beniezsche.locationassignment.db.database.AppDatabase
import com.beniezsche.locationassignment.db.models.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class LocationService : Service() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback
    var sessionId: Long = 0

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)

        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getDatabase(this@LocationService)?.trackingSessionDao()?.updateSessionEnd(sessionId, System.currentTimeMillis())
        }

    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        sessionId = intent!!.getLongExtra("session_id", 0)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val locationRequest: LocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(1000) // 1 second
            .setFastestInterval(1000) // 1 second

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                // Handle the location
                val latitude: Double = location!!.latitude
                val longitude: Double = location.longitude
//                    tvLocation.text = "lat: "  + latitude + " long: " + longitude;
                val locationIntent = Intent("location-update-event")
                // Add data to the intent if needed
                locationIntent.putExtra("lat", latitude)
                locationIntent.putExtra("long", longitude)
                // Broadcast the intent

                CoroutineScope(Dispatchers.IO).launch {
                    AppDatabase.getDatabase(this@LocationService)?.locationDao()?.insertLocation(
                        Location(sessionId = sessionId, latitude = latitude, longitude = longitude, timestamp = Date().toString())
                    )
                }

                LocalBroadcastManager.getInstance(this@LocationService).sendBroadcast(locationIntent)

            }
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)

        val CHANNELID = "Location-Assignment-Background"
        val channel = NotificationChannel(
            CHANNELID,
            CHANNELID,
            NotificationManager.IMPORTANCE_HIGH
        )

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        val notification: Notification.Builder = Notification.Builder(this, CHANNELID)
            .setContentText("Your Location is being tracked")
            .setContentTitle("Location Tracking On")
            .setSmallIcon(R.drawable.ic_menu_mylocation)
            .setOngoing(true)

        startForeground(1001, notification.build())
        return super.onStartCommand(intent, flags, startId)

    }
}