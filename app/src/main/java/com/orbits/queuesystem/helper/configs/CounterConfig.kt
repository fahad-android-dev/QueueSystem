package com.orbits.queuesystem.helper.configs

import android.content.Context
import com.orbits.queuesystem.helper.database.CounterDataDbModel
import com.orbits.queuesystem.mvvm.counters.model.CounterListDataModel

object CounterConfig {

    fun Context.parseInCounterDbModel(
        model: CounterListDataModel?,
        entityId: String,
    ): CounterDataDbModel {
        return CounterDataDbModel(
            id = 0,
            counterId = model?.id ?: "",
            entityID = entityId,
            counterType = model?.counterType,
            counterName = model?.name,
            counterNameAr = model?.nameAr,
            serviceAssign = model?.counterType,
            serviceAssignAr = model?.counterType,
            counterDisplayName = null,
            counterActive = null,
            serviceId = model?.serviceId


        )
    }


    fun Context.parseInCounterModelArraylist(it: ArrayList<CounterDataDbModel?>?): ArrayList<CounterListDataModel?> {
        val counterItems = ArrayList<CounterListDataModel?>()
        if (it != null) {
            for (i in 0 until it.size) {
                val a = it[i]
                /* //Product images
                 val images = ArrayList<String>()
                 a?.image?.let { it1 -> images.add(it1) }*/

                val counterItem = CounterListDataModel(
                    id = a?.entityID,
                    counterId = a?.counterId,
                    name = a?.counterName,
                    nameAr = a?.counterNameAr,
                    counterType = a?.counterType,
                    serviceId = a?.serviceId

                )


                counterItems.add(counterItem)
            }
        }
        return counterItems
    }
}