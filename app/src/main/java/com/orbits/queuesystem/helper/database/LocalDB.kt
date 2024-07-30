package com.orbits.queuesystem.helper.database

import android.content.Context
import com.orbits.queuesystem.helper.Extensions.printLog

object LocalDB {


    fun Context.getDao() : AppDatabase {
       return AppDatabase.getAppDatabase(this)
    }

    /*-----------------------------------------------Service-------------------------------------------------------------*/

    fun Context.isServicePresentInApp(serviceID: String?) : Boolean {
        val db = AppDatabase.getAppDatabase(this).mainDao()
        return db?.isServicePresent(serviceID) ?: false
    }

    fun Context.addServiceInDB(services: ServiceDataDbModel): ArrayList<ServiceDataDbModel?> {
        val db = AppDatabase.getAppDatabase(this).mainDao()
        if (db?.isServicePresent(services.entityID) == true) {
            val qty = db.getServiceInDb(services.entityID)
            db.updateServiceOffline(services.serviceId, services.entityID)
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

    fun Context.deleteServiceTableFromDB() {
        val db = AppDatabase.getAppDatabase(this).mainDao()
        return db?.deleteServiceTable() ?: Unit
    }

    fun Context.getStartServiceToken(entityId: String): Int {
        val db = AppDatabase.getAppDatabase(this).mainDao()
        return db?.getStartServiceTokenInDb(entityId) ?: 0
    }

    /*fun Context.updateServiceInDb(
        serviceId: String? = "0",
    ): ArrayList<ServiceDataDbModel?> {
        ("Here i am update cart qty   $serviceId").printLog()
        val db = AppDatabase.getAppDatabase(this).cartDao()
        if (db?.isServicePresent(serviceId) == true) {
            db.updateServiceOffline(serviceId, services.entityID)
        }
        return db?.getAllService() as ArrayList<ServiceDataDbModel?>
    }*/

    fun Context.deleteServiceInDb(productEntityID: String? = "0"): ArrayList<ServiceDataDbModel?>? {
        ("Here i am delete cart id   $productEntityID").printLog()
        val db = AppDatabase.getAppDatabase(this).mainDao()
        db?.deleteService(productEntityID)
        return db?.getAllService() as ArrayList<ServiceDataDbModel?>?
    }

    fun Context.addServiceTokenToDB(serviceId: String, newToken: Int) {
        val db = AppDatabase.getAppDatabase(this).mainDao()
        // Update the token for the specific service
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
            val qty = db.getCounterInDb(counters.entityID)
            db.updateCounterOffline(counters.counterId, counters.entityID)
        } else {
            db?.addCounter(counters)
        }
        ("${db?.getAllCounter()}").printLog()
        return db?.getAllCounter() as ArrayList<CounterDataDbModel?>
    }

    fun Context.getAllCounterFromDB(): ArrayList<CounterDataDbModel?>? {
        val db = AppDatabase.getAppDatabase(this).counterDao()
        return db?.getAllCounter() as ArrayList<CounterDataDbModel?>?
    }

    fun Context.deleteCounterTableFromDB() {
        val db = AppDatabase.getAppDatabase(this).counterDao()
        return db?.deleteCounterTable() ?: Unit
    }


    fun Context.deleteCounterInDb(productEntityID: String? = "0"): ArrayList<CounterDataDbModel?>? {
        ("Here i am delete counter id   $productEntityID").printLog()
        val db = AppDatabase.getAppDatabase(this).counterDao()
        db?.deleteCounter(productEntityID)
        return db?.getAllCounter() as ArrayList<CounterDataDbModel?>?
    }

    /*-----------------------------------------------Counter-------------------------------------------------------------*/
}