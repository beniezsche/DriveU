package com.beniezsche.locationassignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beniezsche.locationassignment.adapters.TrackingSessionsAdapter
import com.beniezsche.locationassignment.db.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SessionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sessions)

        val adapter = TrackingSessionsAdapter()
        val recyclerView = findViewById<RecyclerView>(R.id.sessionsList)

        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = adapter


        CoroutineScope(Dispatchers.IO).launch {
            val list = AppDatabase.getDatabase(this@SessionsActivity)?.trackingSessionDao()?.getAllTrackingSession()

            if (list != null) {

                adapter.list.addAll(list)
                adapter.notifyDataSetChanged()

            }

        }


    }
}