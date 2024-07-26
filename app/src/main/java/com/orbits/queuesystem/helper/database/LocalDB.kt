package com.orbits.queuesystem.helper.database

import android.content.Context
import com.orbits.queuesystem.helper.Extensions.printLog

object LocalDB {

    fun Context.getDao() : AppDatabase {
       return AppDatabase.getAppDatabase(this)
    }

     /**/

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
    fun Context.deleteServerTableFromDB() {
        val db = AppDatabase.getAppDatabase(this).mainDao()
        return db?.deleteServiceTable() ?: Unit
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

    fun Context.deleteServerInDb(productEntityID: String? = "0"): ArrayList<ServiceDataDbModel?>? {
        ("Here i am delete cart id   $productEntityID").printLog()
        val db = AppDatabase.getAppDatabase(this).mainDao()
        db?.deleteService(productEntityID)
        return db?.getAllService() as ArrayList<ServiceDataDbModel?>?
    }
}