package com.orbits.queuesystem.helper.database

import android.content.Context
import com.orbits.queuesystem.helper.Extensions.asString
import com.orbits.queuesystem.helper.Extensions.printLog

object LocalDB {


    fun Context.getDao() : AppDatabase {
       return AppDatabase.getAppDatabase(this)
    }

    /*-----------------------------------------------Service-------------------------------------------------------------*/



    fun Context.addServiceInDB(services: ServiceDataDbModel): ArrayList<ServiceDataDbModel?> {
        val db = AppDatabase.getAppDatabase(this).mainDao()
        if (db?.isServicePresent(services.entityID) == true) {
            db.updateServiceOffline(
                services.entityID,
                services.tokenStart,
                services.tokenEnd,
                services.tokenNo,
                services.serviceName,
                services.serviceNameAr,
            )
        } else {
            db?.addService(services)
        }
        ("${db?.getAllService()}").printLog()
        return db?.getAllService() as ArrayList<ServiceDataDbModel?>
    }

    fun Context.getAllServiceFromDB(): ArrayList<ServiceDataDbModel?>? {
        val db = AppDatabase.getAppDatabase(this).mainDao()
        return db?.getAllService() as ArrayList<ServiceDataDbModel?>?
    }

    fun Context.getServiceById(serviceId: Int): ServiceDataDbModel? {
        val services = getAllServiceFromDB()
        return services?.find { it?.id == serviceId }
    }

    fun Context.getStartServiceToken(entityId: String): Int {
        val db = AppDatabase.getAppDatabase(this).mainDao()
        return db?.getStartServiceTokenInDb(entityId) ?: 0
    }
    fun Context.getLastTokenInService(serviceID: String?) : Int {
        val db = AppDatabase.getAppDatabase(this).mainDao()
        return db?.getTokenEnd(serviceID ?: "") ?: 0
    }

    fun Context.getCurrentServiceToken(entityId: String): Int {
        val db = AppDatabase.getAppDatabase(this).mainDao()
        return db?.getCurrentServiceTokenInDb(entityId) ?: 0
    }

    fun Context.getKeypadServiceToken(entityId: String): Int {
        val db = AppDatabase.getAppDatabase(this).mainDao()
        return db?.getKeypadServiceTokenInDb(entityId) ?: 0
    }

    fun Context.deleteServiceInDb(productEntityID: String? = "0"): ArrayList<ServiceDataDbModel?>? {
        ("Here i am delete cart id   $productEntityID").printLog()
        val db = AppDatabase.getAppDatabase(this).mainDao()
        db?.deleteService(productEntityID)
        return db?.getAllService() as ArrayList<ServiceDataDbModel?>?
    }

    fun Context.addServiceTokenToDB(serviceId: String, newToken: Int) {
        val db = AppDatabase.getAppDatabase(this).mainDao()
        db?.updateServiceToken(serviceId, newToken)
    }




    /*-----------------------------------------------Service-------------------------------------------------------------*/


    /*-----------------------------------------------Counter-------------------------------------------------------------*/


    fun Context.isCounterPresentInApp(counterID: String?) : Boolean {
        val db = AppDatabase.getAppDatabase(this).counterDao()
        return db?.isCounterPresent(counterID) ?: false
    }

    fun Context.isCounterAssigned(serviceId: String): Boolean {
        val db = AppDatabase.getAppDatabase(this).counterDao()
        return (db?.isCounterAssignedToService(serviceId) ?: 0) > 0
    }

    fun Context.addCounterInDB(counters: CounterDataDbModel): ArrayList<CounterDataDbModel?> {
        val db = AppDatabase.getAppDatabase(this).counterDao()
        if (db?.isCounterPresent(counters.entityID) == true) {
            db.updateCounterOffline(
                counters.counterId,
                counterName = counters.counterName,
                counterNameAr = counters.counterNameAr,
                serviceAssign = counters.serviceAssign,
                serviceId = counters.serviceId
            )
        } else {
            db?.addCounter(counters)
        }
        ("${db?.getAllCounter()}").printLog()
        return db?.getAllCounter() as ArrayList<CounterDataDbModel?>
    }

    fun Context.getCounterIdForService(serviceId: String): String {
        val db = AppDatabase.getAppDatabase(this).counterDao()
        // Query your database to find the counter ID associated with the service ID
        // Return the counter ID or null if not found
        return db?.getCounterIdByServiceId(serviceId) ?: ""
    }

    fun Context.getAllCounterFromDB(): ArrayList<CounterDataDbModel?>? {
        val db = AppDatabase.getAppDatabase(this).counterDao()
        return db?.getAllCounter() as ArrayList<CounterDataDbModel?>?
    }

    fun Context.getCounterFromDB(counterId: String?): CounterDataDbModel? {
        val db = AppDatabase.getAppDatabase(this).counterDao()
        return db?.getCounterById(counterId ?: "")
    }


    /*-----------------------------------------------Counter-------------------------------------------------------------*/



    /*-----------------------------------------------Transaction-------------------------------------------------------------*/


    fun Context.isTransactionPresentInApp(entityId: String?) : Boolean {
        val db = AppDatabase.getAppDatabase(this).transactionDao()
        return db?.isTransactionPresent(entityId) ?: false
    }

    fun Context.addTransactionInDB(data: TransactionDataDbModel): ArrayList<TransactionDataDbModel?> {
        val db = AppDatabase.getAppDatabase(this).transactionDao()
        if (db?.isTransactionPresent(data.issueTime) == true) {
            db.updateTransactionOffline(
                data.token,
                data.status,
                data.id.asString(),
                data.startKeypadTime,
                data.issueTime,
                data.endKeypadTime,
            )
        } else {
            db?.addTransaction(data)
        }
        return db?.getAllTransaction() as ArrayList<TransactionDataDbModel?>
    }


    fun Context.getAllTransactionFromDB(): ArrayList<TransactionDataDbModel?>? {
        val db = AppDatabase.getAppDatabase(this).transactionDao()
        return db?.getAllTransaction() as ArrayList<TransactionDataDbModel?>?
    }

    fun Context.getRequiredTransactionFromDB(): ArrayList<TransactionDataDbModel?>? {
        val db = AppDatabase.getAppDatabase(this).transactionDao()
        return db?.getRequiredTransactions() as ArrayList<TransactionDataDbModel?>?
    }

    fun Context.getAllTransactionCount(serviceId: String): ArrayList<TransactionDataDbModel?>? {
        val db = AppDatabase.getAppDatabase(this).transactionDao()
        return db?.getAllTransactionCount(serviceId) as ArrayList<TransactionDataDbModel?>?
    }

    fun Context.getTransactionFromDbWithIssuedStatus(serviceId: String?): TransactionDataDbModel? {
        val db = AppDatabase.getAppDatabase(this).transactionDao()
        return db?.getTransactionByIssuedStatus(serviceId ?: "")
    }

    fun Context.getTransactionFromDbWithDisplayStatus(serviceId: String?): TransactionDataDbModel? {
        val db = AppDatabase.getAppDatabase(this).transactionDao()
        return db?.getTransactionByDisplayStatus(serviceId ?: "")
    }

    fun Context.getTransactionFromDbWithCalledStatus(serviceId: String?): TransactionDataDbModel? {
        val db = AppDatabase.getAppDatabase(this).transactionDao()
        return db?.getTransactionByCalledStatus(serviceId ?: "")
    }

    fun Context.getLastTransactionFromDbWithStatusOne(serviceId: String?): TransactionDataDbModel? {
        val db = AppDatabase.getAppDatabase(this).transactionDao()
        return db?.getLastTransactionByStatusOne(serviceId ?: "")
    }

    fun Context.getTransactionByToken(token: String,serviceId: String): TransactionDataDbModel? {
        val db = AppDatabase.getAppDatabase(this).transactionDao()
        return db?.getTransactionByToken(token,serviceId)
    }

    fun Context.getTransactionsByService(serviceId: String?): ArrayList<TransactionDataDbModel?>? {
        val db = AppDatabase.getAppDatabase(this).transactionDao()
        return db?.getTransactionsByServiceId(serviceId ?: "")  as ArrayList<TransactionDataDbModel?>?
    }

    fun Context.resetAllTransactionInDb() {
        val db = AppDatabase.getAppDatabase(this).transactionDao()
        db?.resetAllTransactions()
        db?.updateServiceToken()
    }



    /*-----------------------------------------------Transaction-------------------------------------------------------------*/





    /*-----------------------------------------------Users-------------------------------------------------------------*/


    fun Context.addUserInDB(data: UserDataDbModel): ArrayList<UserDataDbModel?> {
        val db = AppDatabase.getAppDatabase(this).userDao()
        if (db?.isUserPresent(data.userId) == true) {
            db?.updateUserOffline(
                data.userId,
                data.id.asString(),
            )
        } else {
            db?.addUser(data)
        }
        return db?.getAllUsers() as ArrayList<UserDataDbModel?>
    }


    fun Context.getAllUserFromDB(): ArrayList<UserDataDbModel?>? {
        val db = AppDatabase.getAppDatabase(this).userDao()
        return db?.getAllUsers() as ArrayList<UserDataDbModel?>?
    }

    fun Context.deleteUserInDb(id: String? = "0"): ArrayList<UserDataDbModel?>? {
        ("Here i am user id   $id").printLog()
        val db = AppDatabase.getAppDatabase(this).userDao()
        db?.deleteUser(id)
        return db?.getAllUsers() as ArrayList<UserDataDbModel?>?
    }



    /*-----------------------------------------------Users-------------------------------------------------------------*/

    /*-----------------------------------------------Reset Time-------------------------------------------------------------*/


    fun Context.addResetData(data: ResetDataDbModel): ArrayList<ResetDataDbModel?> {
        val db = AppDatabase.getAppDatabase(this).resetTimeDao()
        db?.addResetTime(data)
        return db?.getAllResetData() as ArrayList<ResetDataDbModel?>
    }

    fun Context.updateResetDateTime(resetDateTime: String){
        val db = AppDatabase.getAppDatabase(this).resetTimeDao()
        db?.updateResetTime(resetDateTime)
    }

    fun Context.updateCurrentDateTimeInDb(currentDateTime: String){
        val db = AppDatabase.getAppDatabase(this).resetTimeDao()
        db?.updateCurrentTime(currentDateTime)
    }

    fun Context.updateLastDateTimeInDb(lastDateTime: String){
        val db = AppDatabase.getAppDatabase(this).resetTimeDao()
        db?.updateLastDateTime(lastDateTime)
    }

    fun Context.isResetDoneInDb(): Boolean {
        val db = AppDatabase.getAppDatabase(this).resetTimeDao()
        return db?.isResetDone() ?: false
    }


    fun Context.getAllResetData(): ArrayList<ResetDataDbModel?>? {
        val db = AppDatabase.getAppDatabase(this).resetTimeDao()
        return db?.getAllResetData() as ArrayList<ResetDataDbModel?>
    }

    fun Context.getResetData(): ResetDataDbModel? {
        val db = AppDatabase.getAppDatabase(this).resetTimeDao()
        return db?.getResetData()
    }




    /*-----------------------------------------------Reset Time-------------------------------------------------------------*/
}