package com.didichuxing.doraemonkit.kit.timecounter

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Counter::class], version = 2, exportSchema = false)
abstract class CounterDatabase : RoomDatabase() {
    abstract fun counterDao(): CounterDao
}
