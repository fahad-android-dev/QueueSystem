package com.orbits.queuesystem.helper.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(
    entities = [ServiceDataDbModel::class,CounterDataDbModel::class,TransactionDataDbModel::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class,)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mainDao(): MainDao?
    abstract fun counterDao(): CounterDao?
    abstract fun transactionDao(): TransactionDao?

    companion object {
        private const val DATABASE_NAME = "queuesystem.db"

        private var INSTANCE: AppDatabase? = null
        fun getAppDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, DATABASE_NAME
                )
                    // allow queries on the main thread.
                    // Don't do this on a real app! See PersistenceBasicSample for an example.
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE == null
        }
    }
}