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
        """
    UPDATE CounterDataDbModel 
    SET counterName = :counterName, 
        counterNameAr = :counterNameAr, 
        counterType = :serviceAssign,
        serviceId = :serviceId
    WHERE counterId = :counterId
    """
    )
    fun updateCounterOffline(
        counterId: String?,
        counterName: String?,
        counterNameAr: String?,
        serviceAssign: String?,
        serviceId: String?,
    )

    @Query("DELETE from CounterDataDbModel where entityId=:counterEntityID")
    fun deleteCounter(counterEntityID: String?)

    @Query("SELECT DISTINCT counterId FROM CounterDataDbModel WHERE entityId IN (:counterEntityID)")
    fun getCounterInDb(counterEntityID: String?): Int


    @Query("SELECT * FROM CounterDataDbModel")
    fun getCounterCount(): List<CounterDataDbModel>

    @Query("SELECT entityID FROM CounterDataDbModel WHERE entityID=:counterEntityID")
    fun isCounterPresent(counterEntityID: String?): Boolean

    @Query("SELECT COUNT(*) FROM CounterDataDbModel WHERE serviceId = :serviceId")
    fun isCounterAssignedToService(serviceId: String?): Int

    @Query("SELECT * FROM CounterDataDbModel")
    fun getAllCounter(): List<CounterDataDbModel?>

    @Query("SELECT * FROM CounterDataDbModel WHERE counterId = :counterId LIMIT 1")
    fun getCounterById(counterId: String): CounterDataDbModel?

    @Query("DELETE FROM CounterDataDbModel")
    fun deleteCounterTable()

    @Query("SELECT counterId FROM CounterDataDbModel WHERE serviceId = :serviceId")
    fun getCounterIdByServiceId(serviceId: String): String?


    /*-----------------------------------------------Counter-------------------------------------------------------------*/
}