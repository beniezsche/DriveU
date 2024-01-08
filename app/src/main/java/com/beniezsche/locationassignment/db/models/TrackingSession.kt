package com.beniezsche.locationassignment.db.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation


@Entity(tableName = "tracking_sessions")
class TrackingSession {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    @ColumnInfo
    var sessionStart: Long = 0
    @ColumnInfo
    var sessionEnd: Long = 0
}

data class TrackingSessionsWithLocations(
    @Embedded val trackingSession: TrackingSession,
    @Relation(
        parentColumn = "id",
        entityColumn = "sessionId"
    )
    val locations: List<Location>
)