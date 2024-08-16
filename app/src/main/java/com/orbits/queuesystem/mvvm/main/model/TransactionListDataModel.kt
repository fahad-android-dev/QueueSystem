package com.orbits.queuesystem.mvvm.main.model

data class TransactionListDataModel(
    val id: String? = null,
    val counterId: String? = null,
    val serviceId: String? = null,
    val entityID: String? = null,
    val serviceAssign: String? = null,
    val token: String? = null,
    val ticketToken: String? = null,
    val keypadToken: String? = null,
    val issueTime: String? = null,
    val startKeypadTime: String? = null,
    val endKeypadTime: String? = null,
    val status: String? = null,
    )