package com.beniezsche.locationassignment.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.beniezsche.locationassignment.R
import com.beniezsche.locationassignment.TrackingSessionHistoryActivity
import com.beniezsche.locationassignment.db.models.TrackingSessionsWithLocations
import java.util.Calendar

class TrackingSessionsAdapter: RecyclerView.Adapter<TrackingSessionsAdapter.TrackingSessionsViewHolder>() {

    var list = ArrayList<TrackingSessionsWithLocations>()

    inner class TrackingSessionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val text = itemView.findViewById<TextView>(R.id.textView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackingSessionsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_location_history, parent, false)
        return TrackingSessionsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TrackingSessionsViewHolder, position: Int) {

        val sessionItem = list[position]

        if (sessionItem.locations.isNotEmpty()) {

            val cal = Calendar.getInstance()
            cal.timeInMillis = (sessionItem.trackingSession.sessionStart)

            holder.text.text = "Session: " + sessionItem.trackingSession.id.toString()

            holder.itemView.setOnClickListener {
                val intent = Intent(holder.itemView.context, TrackingSessionHistoryActivity::class.java)
                intent.putExtra("sessionId", sessionItem.trackingSession.id)
                intent.putExtra("startedAt", sessionItem.trackingSession.sessionStart)
                intent.putExtra("endedAt", sessionItem.trackingSession.sessionEnd)
                holder.itemView.context.startActivity(intent)
            }
        }

    }
}