package com.beniezsche.locationassignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beniezsche.locationassignment.adapters.SessionsHistoryAdapter
import com.beniezsche.locationassignment.db.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class TrackingSessionHistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session_history)

        val sessionId = intent.getLongExtra("sessionId", 0)
        val startedAt = intent.getLongExtra("startedAt", 0)
        val endedAt = intent.getLongExtra("endedAt", 0)

        val adapter = SessionsHistoryAdapter()
        val recyclerView = findViewById<RecyclerView>(R.id.pointsList)

        val started = findViewById<TextView>(R.id.started)
        val ended = findViewById<TextView>(R.id.ended)

        val cal = Calendar.getInstance()

        cal.timeInMillis = startedAt


        started.text = getTimestampString(cal)

        cal.timeInMillis = endedAt
        ended.text = getTimestampString(cal)

        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = adapter


        CoroutineScope(Dispatchers.IO).launch {
            val list = AppDatabase.getDatabase(this@TrackingSessionHistoryActivity)?.locationDao()?.getAllLocations(sessionId)

            if (list != null) {

                adapter.list.addAll(list)
                adapter.notifyDataSetChanged()

            }
        }
    }

    fun getTimestampString(cal: Calendar) : String {
        return String.format("%02d", cal.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", cal.get(Calendar.MINUTE)) +
                ":" + String.format("%02d", cal.get(Calendar.SECOND)) + " " +

                String.format("%02d", cal.get(Calendar.DAY_OF_MONTH)) + "/" + String.format("%02d", (cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR)
    }
}