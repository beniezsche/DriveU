package com.beniezsche.locationassignment.db.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.beniezsche.locationassignment.db.daos.LocationDao
import com.beniezsche.locationassignment.db.daos.TrackingSessionDao
import com.beniezsche.locationassignment.db.models.Location
import com.beniezsche.locationassignment.db.models.TrackingSession

@Database(entities = [Location::class, TrackingSession::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao

    abstract fun trackingSessionDao(): TrackingSessionDao



    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase? {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}