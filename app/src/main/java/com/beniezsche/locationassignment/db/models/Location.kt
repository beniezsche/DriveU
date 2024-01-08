package com.beniezsche.locationassignment.db.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "locations", foreignKeys = [ForeignKey(entity = TrackingSession::class, parentColumns = ["id"], childColumns = ["sessionId"], onDelete = ForeignKey.CASCADE)])
data class Location(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val longitude: Double,
    val latitude: Double,
    val timestamp: String,
    val sessionId: Long

)
