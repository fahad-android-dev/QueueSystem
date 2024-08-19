package com.orbits.queuesystem.helper.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TransactionDao {


    @Insert
    fun addTransaction(vararg services: TransactionDataDbModel)

    @Query(
        "UPDATE TransactionDataDbModel" +
                " SET token =:token, status=:status ,startKeypadTime=:startKeypadTime,endKeypadTime=:endKeypadTime ,issueTime =:issueTime" +
                " WHERE id =:id"
    )
    fun updateTransactionOffline(
        token: String?,
        status:String?,
        id: String?,
        startKeypadTime: String?,
        issueTime: String?,
        endKeypadTime: String?,
    )

    @Query("SELECT * FROM TransactionDataDbModel WHERE token = :token LIMIT 1")
    fun getTransactionByToken(token: String): TransactionDataDbModel?

    @Query("SELECT token FROM TransactionDataDbModel WHERE issueTime=:issueTime")
    fun isTransactionPresent(issueTime: String?): Boolean

    @Query("SELECT * FROM TransactionDataDbModel")
    fun getAllTransaction(): List<TransactionDataDbModel?>

    @Query("SELECT * FROM TransactionDataDbModel WHERE status = 0 AND serviceId = :serviceId ORDER BY issueTime LIMIT 1")
    fun getTransactionByIssuedStatus(serviceId:String): TransactionDataDbModel?


    @Query("SELECT * FROM TransactionDataDbModel WHERE status = 1 AND serviceId = :serviceId ORDER BY issueTime LIMIT 1")
    fun getTransactionByCalledStatus(serviceId:String): TransactionDataDbModel?


    @Query("UPDATE TransactionDataDbModel SET token = :tokenNo WHERE entityID = :entityID")
    fun updateTransactionToken(entityID: String, tokenNo: Int)

    @Query("SELECT * FROM TRANSACTIONDATADBMODEL WHERE ticketToken = :ticketToken")
    fun getAllTransactionsByToken(ticketToken: String): List<TransactionDataDbModel?>


}