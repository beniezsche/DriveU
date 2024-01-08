package com.beniezsche.locationassignment.db.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.beniezsche.locationassignment.db.models.Location

@Dao
interface LocationDao {

    @Insert
    suspend fun insertLocation(location: Location)

    @Query("SELECT * FROM locations WHERE sessionId = :sessionId  ORDER BY timestamp DESC")
    fun getAllLocations(sessionId: Long): List<Location>

}