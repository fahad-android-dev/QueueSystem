package com.orbits.queuesystem.helper

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.orbits.queuesystem.helper.Extensions.asString
import com.orbits.queuesystem.helper.database.CounterDataDbModel
import com.orbits.queuesystem.helper.database.LocalDB.getAllCounterFromDB
import com.orbits.queuesystem.helper.database.LocalDB.getAllServiceFromDB
import com.orbits.queuesystem.helper.database.LocalDB.getAllTransactionFromDB
import com.orbits.queuesystem.helper.database.LocalDB.getCurrentServiceToken
import com.orbits.queuesystem.helper.database.LocalDB.getKeypadServiceToken
import com.orbits.queuesystem.helper.database.LocalDB.getStartServiceToken
import com.orbits.queuesystem.helper.database.ServiceDataDbModel
import com.orbits.queuesystem.helper.database.TransactionDataDbModel

object JsonConfig {

    val gson = Gson()

    fun Context.createJsonData(): JsonObject {
        val itemsArray = JsonArray().apply {
            val services = getAllServiceFromDB()
            services?.forEach { service ->
                add(service?.toJsonObject())
            }
        }

        val counterArray = JsonArray().apply {
            val counters = getAllCounterFromDB()
            counters?.forEach { counter ->
                add(counter?.toCounterJsonObject())
            }
        }

        return JsonObject().apply {
            add("items", itemsArray)
            add("counters", counterArray)
        }
    }


    fun Context.createAllJsonData(): JsonObject {
        val itemsArray = JsonArray().apply {
            val services = getAllServiceFromDB()
            services?.forEach { service ->
                add(service?.toJsonObject())
            }
        }

        val counterArray = JsonArray().apply {
            val counters = getAllCounterFromDB()
            counters?.forEach { counter ->
                add(counter?.toCounterJsonObject())
            }
        }

        val combinedArray = JsonArray()

        itemsArray.forEach { combinedArray.add(it) }

        // Add all elements from counterArray to combinedArray
        counterArray.forEach { combinedArray.add(it) }


        return JsonObject().apply {
            add("items", combinedArray)
        }
    }


     fun Context.createServiceJsonData(serviceId : String): JsonObject {
        println("here is all services ${getAllServiceFromDB()}")
        val model = getAllServiceFromDB()?.find { it?.entityID == serviceId }
        println("here is service id start  ${model?.id.asString()}")
        println("here is all service   ${model}")
        println("here is start token ${getCurrentServiceToken(model?.id.asString())}")
        println("here is tokens ${saveCalledTokens(model?.id.asString())}")
         return JsonObject().apply {
            addProperty("startToken", model?.tokenStart)
            addProperty("endToken", model?.tokenEnd)
            addProperty("serviceId", model?.id)
            addProperty("serviceName", model?.serviceName)
            addProperty("serviceName", model?.serviceName)
            addProperty("tokenNo", getCurrentServiceToken(model?.entityID ?: ""))
        }
    }

    fun Context.createServiceJsonDataWithModel(serviceId : String,transactionModel: TransactionDataDbModel): JsonObject {
        val model = getAllServiceFromDB()?.find { it?.entityID == serviceId }
        println("here is start token ${getCurrentServiceToken(model?.id.asString())}")
        val jsonModel = gson.toJson(transactionModel)
        return JsonObject().apply {
            addProperty("startToken", model?.tokenStart)
            addProperty("endToken", model?.tokenEnd)
            addProperty("serviceName", model?.serviceName)
            addProperty("serviceName", model?.serviceName)
            add(Constants.TRANSACTION,  gson.fromJson(jsonModel, JsonObject::class.java))
        }
    }


    fun Context.createServiceJsonDataWithKeypadTokens(serviceId : String): JsonObject {
        val model = getAllServiceFromDB()?.find { it?.entityID == serviceId }
        return JsonObject().apply {
            addProperty("startToken", model?.tokenStart)
            addProperty("endToken", model?.tokenEnd)
            addProperty("serviceName", model?.serviceName)
            addProperty("tokenNo", getKeypadServiceToken(model?.entityID ?: ""))
           // add("tokens", toKeypadJsonArray(saveCalledTokens(serviceId)))
        }
    }


    fun Context.createServiceJsonDataWithTransactions(transactions : ArrayList<TransactionDataDbModel?>): JsonObject {
        println("here is transactions 0000 ${transactions}")
        return JsonObject().apply {
            add(Constants.TRANSACTION,transactions.toKeypadJsonArray())
        }
    }

    fun Context.createServiceJsonDataWithTransaction(transactionModel: TransactionDataDbModel?): JsonObject {
        println("here is transaction model ${transactionModel}")
        val jsonModel = gson.toJson(transactionModel)
        return JsonObject().apply {
            add(Constants.TRANSACTION,  gson.fromJson(jsonModel, JsonObject::class.java))
        }
    }



    fun ServiceDataDbModel.toJsonObject(): JsonObject {
        return JsonObject().apply {
            addProperty("id", entityID)
            addProperty("name", serviceName)
            addProperty("tokenStart", tokenStart)
            addProperty("tokenEnd", tokenEnd)
        }
    }

     fun CounterDataDbModel.toCounterJsonObject(): JsonObject {
        return JsonObject().apply {
            addProperty("id", entityID)
            addProperty("name", counterName)
            addProperty("counterType", counterType)
            addProperty("counterId", counterId)
            addProperty("serviceId", serviceId)
        }
    }


    fun ArrayList<TransactionDataDbModel?>.toKeypadJsonArray(): JsonArray {
        return JsonArray().apply {
            this@toKeypadJsonArray.forEach { transaction ->
                transaction?.let { add(gson.toJsonTree(it)) }
            }
        }
    }


    fun Context.saveCalledTokens(serviceEntityID: String?) : List<Int> {
        val startToken = getStartServiceToken(serviceEntityID ?: "")
        val currentToken = getCurrentServiceToken(serviceEntityID ?: "")

        // Generate the list of tokens
        val tokens = (startToken..currentToken).toList()

        return tokens

    }

}