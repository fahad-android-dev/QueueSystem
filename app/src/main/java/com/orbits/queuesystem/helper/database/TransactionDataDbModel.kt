package com.orbits.queuesystem.helper.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "TransactionDataDbModel")
data class TransactionDataDbModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "counterId") val counterId: String,
    @ColumnInfo(name = "serviceId") val serviceId: String?,
    @ColumnInfo(name = "entityID") val entityID: String?,
    @ColumnInfo(name = "counterType") val counterType: String?,
    @ColumnInfo(name = "serviceAssign") val serviceAssign: String?,
    @ColumnInfo(name = "token") val token: String?,
    @ColumnInfo(name = "ticketToken") val ticketToken: String?,
    @ColumnInfo(name = "keypadToken") val keypadToken: String?,
    @ColumnInfo(name = "issueTime") val issueTime: String?,
    @ColumnInfo(name = "startTime") val startTime: String?,
    @ColumnInfo(name = "endTime") val endTime: String?,
    @ColumnInfo(name = "status") val status: String?,
)
