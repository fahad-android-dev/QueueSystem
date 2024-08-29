package com.orbits.queuesystem.helper.interfaces

import com.orbits.queuesystem.mvvm.counters.model.CounterListDataModel
import com.orbits.queuesystem.mvvm.main.model.ServiceListDataModel
import com.orbits.queuesystem.mvvm.users.model.UserListDataModel

interface AlertDialogInterface {
    fun onYesClick() {}
    fun onMasterYesClick() {}
    fun onNoClick() {}
    fun onCloseDialog() {}
    fun onAddService(model:ServiceListDataModel) {}
    fun onUpdateService(model:ServiceListDataModel) {}
    fun onAddCounter(model:CounterListDataModel) {}
    fun onUpdateCounter(model:CounterListDataModel) {}
    fun onAddUser(model:UserListDataModel) {}
    fun onTimeSelected(hour:String,minute:String) {}
}