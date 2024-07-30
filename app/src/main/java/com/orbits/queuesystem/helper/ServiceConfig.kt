package com.orbits.queuesystem.helper

import android.content.Context
import com.orbits.queuesystem.helper.database.ServiceDataDbModel
import com.orbits.queuesystem.mvvm.main.model.ServiceListDataModel

object ServiceConfig {

    fun Context.parseInServiceDbModel(
        model: ServiceListDataModel?,
        entityId: String,
    ): ServiceDataDbModel {
        return ServiceDataDbModel(
            id = 0,
            serviceId = model?.id ?: "",
            entityID = entityId,
            serviceName = model?.name,
            serviceNameAr = model?.nameAr,
            tokenStart = model?.tokenStart,
            tokenEnd = model?.tokenEnd,
            displayName = null,
            displayNameAr = null,
            serviceActive = null,
            tokenNo = null,
            currentToken = model?.currentToken,
            isSelected = false
        )
    }


    fun Context.parseInServiceModelArraylist(it: ArrayList<ServiceDataDbModel?>?): ArrayList<ServiceListDataModel?> {
        val serviceItems = ArrayList<ServiceListDataModel?>()
        if (it != null) {
            for (i in 0 until it.size) {
                val a = it[i]
               /* //Product images
                val images = ArrayList<String>()
                a?.image?.let { it1 -> images.add(it1) }*/

                val serviceItem = ServiceListDataModel(
                    id = a?.entityID,
                    serviceId = a?.serviceId,
                    name =  a?.serviceName,
                    nameAr =  a?.serviceNameAr,
                    tokenStart =  a?.tokenStart,
                    tokenEnd = a?.tokenEnd,
                    currentToken = a?.currentToken
                )


                serviceItems.add(serviceItem)
            }
        }
        return serviceItems
    }
}