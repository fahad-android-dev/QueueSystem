package com.orbits.queuesystem.helper.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CounterDao {
    /*-----------------------------------------------Counter-------------------------------------------------------------*/


    @Insert
    fun addCounter(vararg counters: CounterDataDbModel)

    /*@Query("UPDATE ServiceDataDbModel SET quantity =:strQty WHERE entityID = :productEntityID")
    fun updateProductsInCart(strQty: Int?, productEntityID: String?)
*/
    @Query(
        "UPDATE CounterDataDbModel SET counterId =:counterId,entityId =:counterEntityID"
    )
    fun updateCounterOffline(
        counterId: String?,
        counterEntityID: String?,
    )

    @Query("DELETE from CounterDataDbModel where entityId=:counterEntityID")
    fun deleteCounter(counterEntityID: String?)

    @Query("SELECT DISTINCT counterId FROM CounterDataDbModel WHERE entityId IN (:counterEntityID)")
    fun getCounterInDb(counterEntityID: String?): Int


    @Query("SELECT * FROM CounterDataDbModel")
    fun getCounterCount(): List<CounterDataDbModel>

    @Query("SELECT entityID FROM CounterDataDbModel WHERE entityID=:counterEntityID")
    fun isCounterPresent(counterEntityID: String?): Boolean

    @Query("SELECT COUNT(*) FROM CounterDataDbModel WHERE serviceAssign = :serviceAssign")
    fun isCounterAssignedToService(serviceAssign: String): Int

    @Query("SELECT * FROM CounterDataDbModel")
    fun getAllCounter(): List<CounterDataDbModel?>

    @Query("DELETE FROM CounterDataDbModel")
    fun deleteCounterTable()


    /*-----------------------------------------------Counter-------------------------------------------------------------*/
}