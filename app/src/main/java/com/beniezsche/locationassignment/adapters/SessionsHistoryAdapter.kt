package com.beniezsche.locationassignment.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.beniezsche.locationassignment.R
import com.beniezsche.locationassignment.TrackingSessionHistoryActivity
import com.beniezsche.locationassignment.db.models.Location
import com.beniezsche.locationassignment.db.models.TrackingSessionsWithLocations
import java.util.Calendar

class SessionsHistoryAdapter: RecyclerView.Adapter<SessionsHistoryAdapter.TrackingSessionsViewHolder>() {

    var list = ArrayList<Location>()

    inner class TrackingSessionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val latitude = itemView.findViewById<TextView>(R.id.latitude)
        val longitude = itemView.findViewById<TextView>(R.id.longitude)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackingSessionsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_session_history, parent, false)
        return TrackingSessionsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TrackingSessionsViewHolder, position: Int) {

        val locationItem = list[position]

        holder.latitude.text = "Lat: " + locationItem.latitude
        holder.longitude.text = "Long: " + locationItem.longitude

    }
}