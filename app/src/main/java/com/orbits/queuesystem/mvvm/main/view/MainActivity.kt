package com.orbits.queuesystem.mvvm.main.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.orbits.queuesystem.R
import com.orbits.queuesystem.databinding.ActivityMainBinding
import com.orbits.queuesystem.helper.AlertDialogInterface
import com.orbits.queuesystem.helper.BaseActivity
import com.orbits.queuesystem.helper.ClientDataModel
import com.orbits.queuesystem.helper.CommonInterfaceClickEvent
import com.orbits.queuesystem.helper.Constants
import com.orbits.queuesystem.helper.Dialogs
import com.orbits.queuesystem.helper.Extensions.asString
import com.orbits.queuesystem.helper.JsonConfig.createJsonData
import com.orbits.queuesystem.helper.JsonConfig.createServiceJsonData
import com.orbits.queuesystem.helper.JsonConfig.createServiceJsonDataWithModel
import com.orbits.queuesystem.helper.JsonConfig.createServiceJsonDataWithTransaction
import com.orbits.queuesystem.helper.MessageListener
import com.orbits.queuesystem.helper.ServiceConfig.parseInServiceDbModel
import com.orbits.queuesystem.helper.ServiceConfig.parseInServiceModelArraylist
import com.orbits.queuesystem.helper.TCPServer
import com.orbits.queuesystem.helper.TransactionConfig.parseInTransactionDbModel
import com.orbits.queuesystem.helper.WebSocketClient
import com.orbits.queuesystem.helper.database.LocalDB.addServiceInDB
import com.orbits.queuesystem.helper.database.LocalDB.addServiceTokenToDB
import com.orbits.queuesystem.helper.database.LocalDB.addTransactionInDB
import com.orbits.queuesystem.helper.database.LocalDB.deleteServiceInDb
import com.orbits.queuesystem.helper.database.LocalDB.getAllServiceFromDB
import com.orbits.queuesystem.helper.database.LocalDB.getAllTransactionFromDB
import com.orbits.queuesystem.helper.database.LocalDB.getTransactionFromDbWithStatus
import com.orbits.queuesystem.helper.database.LocalDB.getCounterIdForService
import com.orbits.queuesystem.helper.database.LocalDB.getCurrentServiceToken
import com.orbits.queuesystem.helper.database.LocalDB.isCounterAssigned
import com.orbits.queuesystem.mvvm.counters.view.CounterListActivity
import com.orbits.queuesystem.mvvm.main.adapter.ServiceListAdapter
import com.orbits.queuesystem.mvvm.main.model.ServiceListDataModel
import com.orbits.queuesystem.mvvm.main.model.TransactionListDataModel
import java.io.OutputStream
import java.net.Socket

class MainActivity : BaseActivity(), MessageListener {
    private lateinit var binding: ActivityMainBinding
    private var adapter = ServiceListAdapter()
    private var arrListService = ArrayList<ServiceListDataModel?>()
    private lateinit var tcpServer: TCPServer
    private lateinit var socket: Socket
    private lateinit var webSocketClient: WebSocketClient
    private var outStream: OutputStream? = null
    private var arrListClients = ArrayList<String?>()
    val gson = Gson()
    var serviceId = ""
    var serviceType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initializeToolbar()
        initializeSocket()
        initializeFields()
        onClickListeners()

    }

    private fun initializeToolbar() {
        setUpToolbar(
            binding.layoutToolbar,
            title = getString(R.string.app_name),
            isBackArrow = false,
            toolbarClickListener = object : CommonInterfaceClickEvent {
                override fun onToolBarListener(type: String) {
                    if (type == Constants.TOOLBAR_ICON_ONE) {
                        val intent = Intent(this@MainActivity, CounterListActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        )
    }

    private fun initializeSocket() {
        tcpServer = TCPServer(8085, this, this@MainActivity)
        Thread {
            tcpServer.start()
        }.start()

        webSocketClient = WebSocketClient(8085)
        webSocketClient.start()

    }

    private fun initializeFields() {
        binding.rvServiceList.adapter = adapter
        setData(parseInServiceModelArraylist(getAllServiceFromDB()))
    }

    private fun setData(data: ArrayList<ServiceListDataModel?>) {
        arrListService.clear()
        arrListService.addAll(data)
        adapter.onClickEvent = object : CommonInterfaceClickEvent {
            override fun onItemClick(type: String, position: Int) {
                if (type == "deleteService") {
                    Dialogs.showCustomAlert(
                        activity = this@MainActivity,
                        msg = getString(R.string.are_you_sure_you_want_to_delete_this_service),
                        yesBtn = getString(R.string.yes),
                        noBtn = getString(R.string.no),
                        alertDialogInterface = object : AlertDialogInterface {
                            override fun onYesClick() {
                                deleteServiceInDb(arrListService[position]?.id)
                                arrListService.remove(arrListService[position])
                                adapter.setData(arrListService)
                            }
                        }
                    )
                }
            }
        }
        adapter.setData(arrListService)
    }

    private fun onClickListeners() {
        binding.btnAddService.setOnClickListener {
            Dialogs.showAddServiceDialog(this, true, object : AlertDialogInterface {
                override fun onAddService(model: ServiceListDataModel) {
                    val dbModel = parseInServiceDbModel(model, model.serviceId ?: "")
                    addServiceInDB(dbModel)
                    setData(parseInServiceModelArraylist(getAllServiceFromDB()))
                }
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(message: String) {

    }

    override fun onMessageJsonReceived(json: JsonObject) {
        runOnUiThread {
            if (!json.isJsonNull) {

                println("Received json in activity: $json")
                println("Received all Clients: $arrListClients")
                arrListClients.forEach {
                    if (it == "101") {
                        println("here is 111")
                        if (json.has(Constants.TICKET_TYPE)) {
                            manageTicketData(json)
                        }
                    } else {
                        if (json.has(Constants.KEYPAD_SERVICE_TYPE)) {
                            manageKeypadData(json)
                        }else if (json.has(Constants.CONNECTION)){
                            println("here is 2222")
                            sendMessageToWebSocketClient(it ?: "", createJsonData())
                        }
                    }
                }

            } else {
                socket.close()
            }

        }
    }

    private fun manageKeypadData(json: JsonObject){
        println("THIS IS KEYPAD TYPE MODULE ::")
        if (json.has(Constants.TRANSACTION)) {
            val model =  json.getAsJsonObject("transaction")
            serviceId = model?.get("serviceId")?.asString ?: ""
            serviceType = model?.get("serviceType")?.asString ?: ""
            println("here is service id $serviceId")
            if (serviceId.isNotEmpty() && isCounterAssigned(serviceType)) {
                val updateModel = TransactionListDataModel(
                    id = model?.get("id")?.asString ?: "",
                    counterId = model?.get("counterId")?.asString ?: "",
                    serviceId = serviceId,
                    entityID = model?.get("entityID")?.asString ?: "",
                    serviceAssign = serviceType,
                    token = model?.get("token")?.asString ?: "",
                    ticketToken = model?.get("ticketToken")?.asString ?: "",
                    keypadToken = model?.get("keypadToken")?.asString ?: "",
                    issueTime = null,
                    startTime = null,
                    endTime = null,
                    status = "1"

                )
                val dbModel =
                    parseInTransactionDbModel(updateModel, updateModel.id ?: "")
                addTransactionInDB(dbModel)

                println("here is transactions 0000 ${getAllTransactionFromDB()}")
                println("here is transactions with status ${ getTransactionFromDbWithStatus()}")

                sendMessageToWebSocketClient(
                    model?.get("serviceId")?.asString ?: "",
                    createServiceJsonDataWithTransaction(
                        getTransactionFromDbWithStatus()
                    )
                )
            }

        } else {
            sendMessageToWebSocketClient(
                json.get("serviceId")?.asString ?: "",
                createServiceJsonDataWithTransaction(
                    getTransactionFromDbWithStatus()
                )
            )
        }
    }

    private fun manageTicketData(json: JsonObject){
        println("THIS IS TICKET TYPE MODULE :: ")
        if (json.has(Constants.SERVICE_TYPE)) {
            serviceId = json.get("serviceId")?.asString ?: ""
            serviceType = json.get("serviceType")?.asString ?: ""
            println("here is service id $serviceId")
            if (serviceId.isNotEmpty() && isCounterAssigned(serviceType)) {
                val model = TransactionListDataModel(
                    counterId = getCounterIdForService(serviceType),
                    serviceId = serviceId,
                    entityID = serviceId,
                    serviceAssign = serviceType,
                    token = getCurrentServiceToken(serviceId).asString(),
                    ticketToken = getCurrentServiceToken(serviceId).asString(),
                    keypadToken = getCurrentServiceToken(serviceId).asString(),
                    issueTime = null,
                    startTime = null,
                    endTime = null,
                    status = "0"

                )
                val dbModel =
                    parseInTransactionDbModel(model, model.id ?: "")
                addTransactionInDB(dbModel)
                println("here is transaction id ${getAllTransactionFromDB()}")
                sendMessageToWebSocketClient(
                    "101",
                    createServiceJsonDataWithModel(serviceId, dbModel)
                )
                addServiceTokenToDB(
                    serviceId,
                    getCurrentServiceToken(serviceId).plus(1)
                )
                println(
                    "here is current token 111 ${
                        getCurrentServiceToken(
                            serviceId
                        )
                    }"
                )
                serviceId = ""
                println("here is transactions 1111 ${getAllTransactionFromDB()}")
            }
        } else {
            sendMessageToWebSocketClient(
                json.get("ticketId")?.asString ?: "",
                createJsonData()
            )
        }
    }


    override fun onClientConnected(clientSocket: Socket?, clientList: List<String?>) {
        Thread {
            try {
                outStream = clientSocket?.getOutputStream()
                if (clientSocket != null) {
                    socket = clientSocket
                    arrListClients.clear()
                    arrListClients.addAll(clientList)
                    println("here is all list after client added $arrListClients")
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Client Connected", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                println("Connected to server")
            } catch (e: Exception) {

                e.printStackTrace()
            }
        }.start()
    }

    override fun onClientDisconnected() {

    }

    private fun sendMessageToWebSocketClient(clientId: String, jsonObject: JsonObject) {
        val clientHandler = TCPServer.WebSocketManager.getClientHandler(clientId)
        if (clientHandler != null && clientHandler.isWebSocket) {
            Thread {
                val jsonMessage = gson.toJson(jsonObject)
                println("here is new 222 $clientId")
                clientHandler.sendMessageToClient(clientId, jsonMessage)
            }.start()
            // Optionally handle success or error
        } else {
            // Handle case where clientHandler is not found or not a WebSocket client
        }
    }
}