package com.orbits.queuesystem.mvvm.main.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.orbits.queuesystem.R
import com.orbits.queuesystem.databinding.ActivityMainBinding
import com.orbits.queuesystem.helper.AlertDialogInterface
import com.orbits.queuesystem.helper.BaseActivity
import com.orbits.queuesystem.helper.CommonInterfaceClickEvent
import com.orbits.queuesystem.helper.Constants
import com.orbits.queuesystem.helper.Dialogs
import com.orbits.queuesystem.helper.Extensions.asInt
import com.orbits.queuesystem.helper.Extensions.asString
import com.orbits.queuesystem.helper.JsonConfig.createJsonData
import com.orbits.queuesystem.helper.JsonConfig.createServiceJsonDataWithModel
import com.orbits.queuesystem.helper.JsonConfig.createServiceJsonDataWithTransaction
import com.orbits.queuesystem.helper.MessageListener
import com.orbits.queuesystem.helper.ServerService
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
import com.orbits.queuesystem.helper.database.LocalDB.getTransactionFromDbWithIssuedStatus
import com.orbits.queuesystem.helper.database.LocalDB.getCounterIdForService
import com.orbits.queuesystem.helper.database.LocalDB.getCurrentServiceToken
import com.orbits.queuesystem.helper.database.LocalDB.getServiceById
import com.orbits.queuesystem.helper.database.LocalDB.getTransactionByToken
import com.orbits.queuesystem.helper.database.LocalDB.getTransactionFromDbWithCalledStatus
import com.orbits.queuesystem.helper.database.LocalDB.isCounterAssigned
import com.orbits.queuesystem.mvvm.counters.view.CounterListActivity
import com.orbits.queuesystem.mvvm.main.adapter.ServiceListAdapter
import com.orbits.queuesystem.mvvm.main.model.DisplayListDataModel
import com.orbits.queuesystem.mvvm.main.model.ServiceListDataModel
import com.orbits.queuesystem.mvvm.main.model.TransactionListDataModel
import java.io.OutputStream
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.CopyOnWriteArrayList

class MainActivity : BaseActivity(), MessageListener {
    private lateinit var binding: ActivityMainBinding
    private var adapter = ServiceListAdapter()
    private var arrListService = ArrayList<ServiceListDataModel?>()
    private lateinit var tcpServer: TCPServer
    private lateinit var socket: Socket
    private lateinit var webSocketClient: WebSocketClient
    private var outStream: OutputStream? = null
    private val arrListClients = CopyOnWriteArrayList<String>()
    private var arrListDisplays = ArrayList<DisplayListDataModel?>()
    val gson = Gson()
    var serviceId = ""
    var serviceType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        startServerService()
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

    private fun startServerService(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
        val intent = Intent(this@MainActivity, ServerService::class.java)
        intent.action = ServerService.Actions.START.toString()
        startService(intent)
        println("Service started")

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
        synchronized(arrListClients) {
            if (!json.isJsonNull) {

                println("Received json in activity: $json")

                if (json.has(Constants.TICKET_TYPE)){
                    manageTicketData(json)
                }else if(json.has(Constants.CONNECTION)){
                    arrListClients.forEach {
                        sendMessageToWebSocketClient(it ?: "", createJsonData())
                    }
                }else if (json.has(Constants.DISPLAY_ID)){
                    manageCounterDisplayData(json)
                }else {
                    manageKeypadData(json)
                }

            } else {
                socket.close()
            }
        }
    }

    private fun manageCounterDisplayData(json: JsonObject){
        println("THIS IS DISPLAY TYPE MODULE ::")
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
                    startKeypadTime = null,
                    endKeypadTime = null,
                    status = model?.get("status")?.asString ?: ""

                )
                val dbModel =
                    parseInTransactionDbModel(updateModel, updateModel.id ?: "")
                addTransactionInDB(dbModel)

                println("here is transactions 111 ${getAllTransactionFromDB()}")
                println("here is transactions with status 1 ::  ${ getTransactionFromDbWithCalledStatus(serviceId)}")

                sendMessageToWebSocketClient(
                    model?.get("displayId")?.asString ?: "",
                    createServiceJsonDataWithTransaction(
                        getTransactionFromDbWithCalledStatus(serviceId)
                    )
                )
            }

        } else {
            println("here is transaction with status 1 in dislpay  ${getTransactionFromDbWithCalledStatus(json.get("serviceId")?.asString ?: "")}")
            sendMessageToWebSocketClient(
                json.get("displayId")?.asString ?: "",
                createServiceJsonDataWithTransaction(
                    getTransactionFromDbWithCalledStatus(json.get("serviceId")?.asString ?: "")
                )
            )
            val model = DisplayListDataModel(
                id = json.get("displayId")?.asString ?: "",
                counterId = json.get("counterId")?.asString ?: "",
                serviceId = json.get("serviceId")?.asString ?: ""

            )
            arrListDisplays.add(model)
        }
    }

    private fun manageKeypadData(json: JsonObject){
        println("THIS IS KEYPAD TYPE MODULE ::")
        if (json.has(Constants.TRANSACTION)) {
            val model =  json.getAsJsonObject("transaction")
            serviceId = model?.get("serviceId")?.asString ?: ""
            serviceType = model?.get("serviceType")?.asString ?: ""
            val service = getServiceById(serviceId.asInt())
            println("here is service id $serviceId")
            if ((serviceId.isNotEmpty() && isCounterAssigned(serviceType)) && (getCurrentServiceToken(serviceId) <= service?.tokenEnd.asInt())) {
                val updateModel = TransactionListDataModel(
                    id = model?.get("id")?.asString ?: "",
                    counterId = model?.get("counterId")?.asString ?: "",
                    serviceId = serviceId,
                    entityID = model?.get("entityID")?.asString ?: "",
                    serviceAssign = serviceType,
                    token = model?.get("token")?.asString ?: "",
                    ticketToken = model?.get("ticketToken")?.asString ?: "",
                    keypadToken = model?.get("keypadToken")?.asString ?: "",
                    issueTime = model?.get("issueTime")?.asString ?: "",
                    startKeypadTime = model?.get("startKeypadTime")?.asString ?: "",
                    endKeypadTime = model?.get("endKeypadTime")?.asString ?: "",
                    status = model?.get("status")?.asString ?: ""

                )
                val dbModel = parseInTransactionDbModel(updateModel, updateModel.id ?: "")
                addTransactionInDB(dbModel)

                println("here is transactions 0000 ${getAllTransactionFromDB()}")
                println("here is transactions with status ${ getTransactionFromDbWithIssuedStatus(serviceId)}")

                if (model.get("status")?.asString == "1"){
                    if ((getTransactionFromDbWithCalledStatus(serviceId) != null)){
                        println("here is status of transaction ${model.get("status")?.asString}")
                        sendMessageToWebSocketClient(
                            json.get("counterId")?.asString ?: "",
                            createServiceJsonDataWithTransaction(
                                getTransactionFromDbWithCalledStatus(serviceId)
                            )
                        )
                    }
                }else {
                    if ((getTransactionFromDbWithIssuedStatus(serviceId) != null)){
                        println("here is status of transaction ${model.get("status")?.asString}")
                        sendMessageToWebSocketClient(
                            json.get("counterId")?.asString ?: "",
                            createServiceJsonDataWithTransaction(
                                getTransactionFromDbWithIssuedStatus(serviceId)
                            )
                        )
                    }
                }



                if (arrListDisplays.isNotEmpty()){
                    println("here is display list  $arrListDisplays")
                    arrListDisplays.forEach {
                        println("here is display ids  ${it?.id}")
                        if (it?.counterId == (model?.get("counterId")?.asString ?: "")){
                            sendMessageToWebSocketClient(
                                it.id ?: "",
                                createServiceJsonDataWithTransaction(
                                    getTransactionFromDbWithCalledStatus(model?.get("serviceId")?.asString ?: "")
                                )
                            )
                        }
                    }
                }
            }

        }
        else {
            if (json.has("tokenNo")){
                if ((getTransactionByToken(json.get("tokenNo")?.asString ?: "") != null)){
                    println("here is transaction with token ::: ${getTransactionByToken(json.get("tokenNo")?.asString ?: "")}")
                    sendMessageToWebSocketClient(
                        json.get("counterId")?.asString ?: "",
                        createServiceJsonDataWithTransaction(
                            getTransactionByToken(json.get("tokenNo")?.asString ?: "")
                        )
                    )
                }

            }else {
                println("here is to check for counter 2")
                sendMessageToWebSocketClient(
                    json.get("counterId")?.asString ?: "",
                    createServiceJsonDataWithTransaction(
                        getTransactionFromDbWithIssuedStatus(json.get("serviceId")?.asString ?: "")
                    )
                )
            }
        }
    }

    private fun manageTicketData(json: JsonObject){
        println("THIS IS TICKET TYPE MODULE :: ")
        if (json.has(Constants.SERVICE_TYPE)) {
            serviceId = json.get("serviceId")?.asString ?: ""
            serviceType = json.get("serviceType")?.asString ?: ""
            println("here is service id $serviceId")
            val service = getServiceById(serviceId.asInt())
            println("here is is the last token :::: ${service?.tokenEnd}")
            println("here is is the service :::: $service")
            println("here is is the current token :::: ${getCurrentServiceToken(serviceId)}")
           // (serviceId.isNotEmpty() && isCounterAssigned(serviceType)) && (getCurrentServiceToken(serviceId) <= service?.tokenEnd.asInt())
            if (serviceId.isNotEmpty() && isCounterAssigned(serviceType)) {
                val model = TransactionListDataModel(
                    counterId = getCounterIdForService(serviceType),
                    serviceId = serviceId,
                    entityID = serviceId,
                    serviceAssign = serviceType,
                    token = getCurrentServiceToken(serviceId).asString(),
                    ticketToken = getCurrentServiceToken(serviceId).asString(),
                    keypadToken = getCurrentServiceToken(serviceId).asString(),
                    issueTime = getCurrentTimeFormatted(),
                    startKeypadTime = null,
                    endKeypadTime = null,
                    status = "0"

                )
                val dbModel =
                    parseInTransactionDbModel(model, model.id ?: "")
                addTransactionInDB(dbModel)
                println("here is transaction id ${getAllTransactionFromDB()}")
                sendMessageToWebSocketClient(
                    json.get("ticketId")?.asString ?: "",
                    createServiceJsonDataWithModel(serviceId, dbModel)
                )
                println("here is current token for service ${getCurrentServiceToken(serviceId)}")
                println("here is end token for service ${service?.tokenEnd.asInt()}")
                if (getCurrentServiceToken(serviceId) == service?.tokenEnd.asInt()){
                    println("here is manage for tokens 111")
                    addServiceTokenToDB(
                        serviceId,
                        service?.tokenStart.asInt()
                    )

                }else {
                    println("here is manage for tokens 222")
                    addServiceTokenToDB(
                        serviceId,
                        getCurrentServiceToken(serviceId).plus(1)
                    )
                }
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
                    /*runOnUiThread {
                        Toast.makeText(this@MainActivity, "Client Connected", Toast.LENGTH_SHORT)
                            .show()
                    }*/
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
        try {
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
        } catch (e: Exception) {
           e.printStackTrace()
        }
    }

    fun getCurrentTimeFormatted(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())

        val currentTime = Date()

        return dateFormat.format(currentTime)
    }
}