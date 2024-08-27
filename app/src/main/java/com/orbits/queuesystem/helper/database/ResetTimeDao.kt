package com.orbits.queuesystem.helper.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ResetTimeDao {

    @Insert
    fun addResetTime(vararg services: ResetDataDbModel)


    @Query("UPDATE ResetDataDbModel SET resetDateTime =:resetDateTime")
    fun updateResetTime(resetDateTime: String?)

    @Query("UPDATE ResetDataDbModel SET currentDateTime =:currentDateTime")
    fun updateCurrentTime(currentDateTime: String?)

    @Query("UPDATE ResetDataDbModel SET lastDateTime =:lastDateTime")
    fun updateLastDateTime(lastDateTime: String?)

    @Query("SELECT * FROM ResetDataDbModel WHERE resetDateTime BETWEEN lastDateTime AND currentDateTime")
    fun isResetDone() : Boolean



    @Query("SELECT * FROM ResetDataDbModel")
    fun getAllResetData(): List<ResetDataDbModel?>?
}