package com.orbits.queuesystem.helper.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {


    @Insert
    fun addUser(vararg services: UserDataDbModel)

    @Query(
        "UPDATE UserDataDbModel" +
                " SET userId =:userId WHERE id =:id"
    )

    fun updateUserOffline(
        userId: String?,
        id: String?,
    )

    @Query("SELECT * FROM UserDataDbModel")
    fun getAllUsers(): List<UserDataDbModel?>

    @Query("SELECT userId FROM UserDataDbModel WHERE userId=:userId")
    fun isUserPresent(userId: String?): Boolean

}