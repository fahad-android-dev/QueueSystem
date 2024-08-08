package com.orbits.queuesystem.helper.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "CounterDataDbModel")
data class CounterDataDbModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "counterId") val counterId: String,
    @ColumnInfo(name = "entityID") val entityID: String?,
    @ColumnInfo(name = "counterType") val counterType: String?,
    @ColumnInfo(name = "counterName") val counterName: String?,
    @ColumnInfo(name = "counterNameAr") val counterNameAr: String?,
    @ColumnInfo(name = "serviceAssign") val serviceAssign: String?,
    @ColumnInfo(name = "serviceId") val serviceId: String?,
    @ColumnInfo(name = "serviceAssignAr") val serviceAssignAr: String?,
    @ColumnInfo(name = "counterDisplayName") var counterDisplayName: Int?,
    @ColumnInfo(name = "counterActive") val counterActive: String?,
)
