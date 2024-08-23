package com.orbits.queuesystem.helper.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "UserDataDbModel")
data class UserDataDbModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "userId") val userId: String,
    @ColumnInfo(name = "userName") val userName: String?,
    @ColumnInfo(name = "password") val password: String?,
)
