package com.orbits.queuesystem.helper

import android.view.View
import com.orbits.queuesystem.mvvm.counters.model.CounterListDataModel
import com.orbits.queuesystem.mvvm.main.model.ServiceListDataModel

interface AlertDialogInterface {
    fun onYesClick() {}
    fun onMasterYesClick() {}
    fun onNoClick() {}
    fun onCloseDialog() {}
    fun onAddService(model:ServiceListDataModel) {}
    fun onAddCounter(model:CounterListDataModel) {}
}