package com.beniezsche.locationassignment.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.beniezsche.locationassignment.db.models.TrackingSession
import com.beniezsche.locationassignment.db.models.TrackingSessionsWithLocations

@Dao
interface TrackingSessionDao {

    @Insert
    suspend fun insertSession(trackingSession: TrackingSession):Long

    @Query("SELECT * FROM tracking_sessions ORDER BY sessionStart DESC")
    fun getAllTrackingSession(): List<TrackingSessionsWithLocations>

    @Query("UPDATE tracking_sessions SET sessionEnd = :newSessionEnd WHERE id = :sessionId")
    fun updateSessionEnd(sessionId: Long, newSessionEnd: Long)

    // Add more queries as needed
}