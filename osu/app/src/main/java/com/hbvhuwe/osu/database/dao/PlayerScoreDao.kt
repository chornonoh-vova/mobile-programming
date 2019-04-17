package com.hbvhuwe.osu.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hbvhuwe.osu.database.model.PlayerScore

@Dao
interface PlayerScoreDao {
    @Query("select * from player_score order by score desc")
    fun getAll(): LiveData<List<PlayerScore>>

    @Query("select min(score) from player_score order by score limit 10")
    fun getTop10Min(): LiveData<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(playerScore: PlayerScore)
}