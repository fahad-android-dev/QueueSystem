package com.orbits.queuesystem.helper

import android.content.Context
import com.orbits.queuesystem.helper.Extensions.asString
import com.orbits.queuesystem.helper.models.UserResponseModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

object PrefUtils {

    /**  -----------------------      USER DATA ---------------------------------- */
    fun Context.setUserDataResponse(result: UserResponseModel?) {
        val dt = DataStoreManager(this)
        runBlocking { dt.saveUserData(result) }
    }

    fun Context.getUserDataResponse(): UserResponseModel? {
        val dt = DataStoreManager(this)
        return runBlocking {
            dt.getUserData().first()
        }
    }





    /**  -----------------------  ------------------------------------  ---------------------------------- */



}
