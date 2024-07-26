package com.orbits.queuesystem.helper.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Converters {
    companion object {
        @TypeConverter
        @JvmStatic
        fun fromString(value: String): ArrayList<CounterDataDbModel> {
            val listType = object : TypeToken<ArrayList<CounterDataDbModel>>() {}.type
            return Gson().fromJson(value, listType)
        }

        @TypeConverter
        @JvmStatic
        fun fromArrayList(list: ArrayList<CounterDataDbModel>): String {
            val gson = Gson()
            return gson.toJson(list)
        }

        @TypeConverter
        @JvmStatic
        fun fromStringAttribute(value: String): ArrayList<CounterDataDbModel> {
            val listType = object : TypeToken<ArrayList<CounterDataDbModel>>() {}.type
            return Gson().fromJson(value, listType)
        }

        @TypeConverter
        @JvmStatic
        fun fromArrayListAttribute(list: ArrayList<CounterDataDbModel>): String {
            val gson = Gson()
            return gson.toJson(list)
        }
    }
}