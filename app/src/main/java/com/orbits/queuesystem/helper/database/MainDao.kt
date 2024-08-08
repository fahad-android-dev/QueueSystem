package com.orbits.queuesystem.helper.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MainDao {

    /*-----------------------------------------------Service-------------------------------------------------------------*/

    @Insert
    fun addService(vararg services: ServiceDataDbModel)

    /*@Query("UPDATE ServiceDataDbModel SET quantity =:strQty WHERE entityID = :productEntityID")
    fun updateProductsInCart(strQty: Int?, productEntityID: String?)
*/
    @Query(
        "UPDATE ServiceDataDbModel SET serviceId =:serviceId,entityId =:serviceEntityID"
    )
    fun updateServiceOffline(
        serviceId: String?,
        serviceEntityID: String?,
    )

    @Query("DELETE from ServiceDataDbModel where entityId=:serviceEntityID")
    fun deleteService(serviceEntityID: String?)

    @Query("SELECT DISTINCT serviceId FROM ServiceDataDbModel WHERE entityId IN (:productEntityID)")
    fun getServiceInDb(productEntityID: String?): Int

    @Query("SELECT DISTINCT tokenStart FROM ServiceDataDbModel WHERE entityId IN (:serviceEntityID)")
    fun getStartServiceTokenInDb(serviceEntityID: String?): Int

    @Query("SELECT DISTINCT tokenNo FROM ServiceDataDbModel WHERE entityId IN (:serviceEntityID)")
    fun getCurrentServiceTokenInDb(serviceEntityID: String?): Int

    @Query("SELECT DISTINCT keypadToken FROM ServiceDataDbModel WHERE entityId IN (:serviceEntityID)")
    fun getKeypadServiceTokenInDb(serviceEntityID: String?): Int


    @Query("SELECT * FROM ServiceDataDbModel")
    fun getServiceCount(): List<ServiceDataDbModel>

    @Query("SELECT entityID FROM ServiceDataDbModel WHERE entityID=:serviceEntityID")
    fun isServicePresent(serviceEntityID: String?): Boolean

    @Query("SELECT * FROM ServiceDataDbModel")
    fun getAllService(): List<ServiceDataDbModel?>

    @Query("DELETE FROM ServiceDataDbModel")
    fun deleteServiceTable()

    @Query("UPDATE ServiceDataDbModel SET tokenNo = :tokenNo WHERE entityID = :entityID")
    fun updateServiceToken(entityID: String, tokenNo: Int)

    @Query("UPDATE ServiceDataDbModel SET keypadToken = :keypadToken WHERE entityID = :entityID")
    fun updateKeypadServiceToken(entityID: String, keypadToken: Int)

    @Query("SELECT serviceId FROM ServiceDataDbModel WHERE serviceName = :serviceAssign LIMIT 1 ")
    fun getServiceIdByServiceAssign(serviceAssign: String): String?



    /*-----------------------------------------------Service-------------------------------------------------------------*/


}