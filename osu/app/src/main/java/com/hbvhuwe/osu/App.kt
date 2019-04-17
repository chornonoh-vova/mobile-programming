package com.hbvhuwe.osu

import android.app.Application
import androidx.room.Room
import com.hbvhuwe.osu.database.Database

class App: Application() {
    lateinit var database: Database

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            applicationContext,
            Database::class.java,
            "osu_database"
        ).build()
    }
}