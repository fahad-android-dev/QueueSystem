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

    fun Context.getConnectionCode(): String? {
        val dt = DataStoreManager(this)
        return runBlocking {
            dt.getUserData().firstOrNull()?.data?.connection_code
        }
    }

    fun Context.getUserId(): String {
        return getUserDataResponse()?.data?.id.asString()
    }

    fun Context.getUserName(): String {
        val userModel = getUserDataResponse()
        return userModel?.data?.firstName ?: ""
    }

    fun Context.isUserLoggedIn(): Boolean {
        return this.getUserDataResponse()?.data?.id != null
    }
    fun Context.isCodeVerified(): Boolean {
        return this.getUserDataResponse()?.data?.isCodeVerified != false
    }




    /**  -----------------------  ------------------------------------  ---------------------------------- */



}
