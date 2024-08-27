package com.orbits.queuesystem.helper.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ResetDataDbModel")
data class ResetDataDbModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "resetDateTime") val resetDateTime: String?,
    @ColumnInfo(name = "currentDateTime") val currentDateTime: String,
    @ColumnInfo(name = "lastDateTime") val lastDateTime: String?,
)