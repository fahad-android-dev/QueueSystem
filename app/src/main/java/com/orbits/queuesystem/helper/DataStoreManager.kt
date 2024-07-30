package com.orbits.queuesystem.helper

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.orbits.queuesystem.helper.DataStoreManager.PreferencesKeys.APP
import com.orbits.queuesystem.helper.DataStoreManager.PreferencesKeys.USER_REMEMBER_DATA
import com.orbits.queuesystem.helper.DataStoreManager.PreferencesKeys.USER_RESPONSE_DATA
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val MERCHANT_DATASTORE = "QUEUE"
private val Context.dataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(name = MERCHANT_DATASTORE)

class DataStoreManager(val context: Context) {
    private val instance = context.dataStore

    private object PreferencesKeys {
        val USER_RESPONSE_DATA = stringPreferencesKey("response_data")
        val USER_REMEMBER_DATA = stringPreferencesKey("user_remember_data")
        val APP = stringPreferencesKey("application")
    }


    suspend fun saveAppConfig(responseModel: AppConfigModel) {
        instance.edit { preferences ->
            preferences[APP] = Gson().toJson(responseModel)
        }
    }

    suspend fun getAppConfig(): Flow<AppConfigModel> {
        return instance.data.map { preferences ->
            Gson().fromJson(preferences[APP] ?: "" , AppConfigModel::class.java)
        }
    }

    suspend fun saveUserData(responseModel: UserResponseModel?) {
        instance.edit { preferences ->
            preferences[USER_RESPONSE_DATA] = Gson().toJson(responseModel)
        }
    }


    fun getUserData(): Flow<UserResponseModel?> {
        return instance.data.map { preferences ->
            val gson = Gson()
            val responseData = preferences[USER_RESPONSE_DATA] ?: ""
            val dataObject = gson.fromJson(responseData, UserResponseModel::class.java)
            dataObject
        }
    }



    suspend fun clearDataStore() = instance.edit {
        it.remove(USER_RESPONSE_DATA)
    }

    suspend fun clearRememberDataStore() = instance.edit {
        it.remove(USER_REMEMBER_DATA)
    }
}