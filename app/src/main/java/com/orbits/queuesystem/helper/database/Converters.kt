package com.orbits.queuesystem.helper.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Converters {
    companion object {
        /*-----------------------------------------------Service-------------------------------------------------------------*/

        @TypeConverter
        @JvmStatic
        fun fromString(value: String): ArrayList<ServiceDataDbModel> {
            val listType = object : TypeToken<ArrayList<ServiceDataDbModel>>() {}.type
            return Gson().fromJson(value, listType)
        }

        @TypeConverter
        @JvmStatic
        fun fromArrayList(list: ArrayList<ServiceDataDbModel>): String {
            val gson = Gson()
            return gson.toJson(list)
        }

        /*-----------------------------------------------Service-------------------------------------------------------------*/


        /*-----------------------------------------------Counter-------------------------------------------------------------*/

        @TypeConverter
        @JvmStatic
        fun fromCounterString(value: String): ArrayList<CounterDataDbModel> {
            val listType = object : TypeToken<ArrayList<CounterDataDbModel>>() {}.type
            return Gson().fromJson(value, listType)
        }

        @TypeConverter
        @JvmStatic
        fun fromCounterArrayList(list: ArrayList<CounterDataDbModel>): String {
            val gson = Gson()
            return gson.toJson(list)
        }


        /*-----------------------------------------------Counter-------------------------------------------------------------*/



        /*-----------------------------------------------Transaction-------------------------------------------------------------*/

        @TypeConverter
        @JvmStatic
        fun fromTransactionString(value: String): ArrayList<TransactionDataDbModel> {
            val listType = object : TypeToken<ArrayList<TransactionDataDbModel>>() {}.type
            return Gson().fromJson(value, listType)
        }

        @TypeConverter
        @JvmStatic
        fun fromTransactionArrayList(list: ArrayList<TransactionDataDbModel>): String {
            val gson = Gson()
            return gson.toJson(list)
        }


        /*-----------------------------------------------Transaction-------------------------------------------------------------*/

    }
}