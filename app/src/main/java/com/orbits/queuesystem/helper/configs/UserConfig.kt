package com.orbits.queuesystem.helper.configs

import android.content.Context
import com.orbits.queuesystem.helper.Extensions.asInt
import com.orbits.queuesystem.helper.Extensions.asString
import com.orbits.queuesystem.helper.database.UserDataDbModel
import com.orbits.queuesystem.mvvm.users.model.UserListDataModel

object UserConfig {

    fun Context.parseInUserDbModel(
        model: UserListDataModel?,
        id: String,
    ): UserDataDbModel {
        return UserDataDbModel(
            id = id.asInt(),
            userId = model?.userId ?: "",
            userName = model?.userName ?: "",
            password = model?.password ?: "",
        )
    }


    fun Context.parseInUserModelArraylist(it: ArrayList<UserDataDbModel?>?): ArrayList<UserListDataModel?> {
        val userItems = ArrayList<UserListDataModel?>()
        if (it != null) {
            for (i in 0 until it.size) {
                val a = it[i]
                val userItem = UserListDataModel(
                    id = a?.id.asString(),
                    userId = a?.userId,
                    userName = a?.userName,
                    password = a?.password,
                )


                userItems.add(userItem)
            }
        }
        return userItems
    }
}