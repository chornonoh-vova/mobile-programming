package com.hbvhuwe.osu.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hbvhuwe.osu.database.dao.PlayerScoreDao
import com.hbvhuwe.osu.database.model.PlayerScore

@Database(entities = [PlayerScore::class], version = 1)
abstract class Database: RoomDatabase() {
    abstract fun playerScoreDao(): PlayerScoreDao
}