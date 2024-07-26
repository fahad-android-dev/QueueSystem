package com.orbits.queuesystem.helper.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity
data class ServiceDataDbModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "serviceId") val serviceId: String,
    @ColumnInfo(name = "entityId") val entityID: String?,
    @ColumnInfo(name = "serviceName") val serviceName: String?,
    @ColumnInfo(name = "serviceNameAr") val serviceNameAr: String?,
    @ColumnInfo(name = "tokenStart") val tokenStart: String?,
    @ColumnInfo(name = "tokenEnd") val tokenEnd: String?,
    @ColumnInfo(name = "displayName") val displayName: String?,
    @ColumnInfo(name = "displayNameAr") var displayNameAr: Int?,
    @ColumnInfo(name = "serviceActive") val serviceActive: String?,
    @ColumnInfo(name = "tokenNo") val tokenNo: String?,
    @ColumnInfo(name = "currentToken") val currentToken: String?,
)
