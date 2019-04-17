package com.hbvhuwe.osu.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_score")
data class PlayerScore(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "score") val score: Int,
    @ColumnInfo(name = "photo_path") val photoPath: String
)
