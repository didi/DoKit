package com.didichuxing.doraemonkit.kit.timecounter

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CounterDao {
    @Query("SELECT * FROM counter")
    fun getAll(): List<Counter>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(counter: Counter)
}
