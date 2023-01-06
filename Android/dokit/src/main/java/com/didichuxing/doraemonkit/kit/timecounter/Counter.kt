package com.didichuxing.doraemonkit.kit.timecounter

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Counter(
    @PrimaryKey(autoGenerate = true) val uid: Long,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "time") val time: Long,
    @ColumnInfo(name = "type") val type: Int,
    @ColumnInfo(name = "totalCost") val totalCost: Long,
    @ColumnInfo(name = "pauseCost") val pauseCost: Long,
    @ColumnInfo(name = "launchCost") val launchCost: Long,
    @ColumnInfo(name = "renderCost") val renderCost: Long,
    @ColumnInfo(name = "otherCost") val otherCost: Long,
)
