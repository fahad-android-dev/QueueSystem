package com.orbits.queuesystem.mvvm.main.view

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.orbits.queuesystem.R
import com.orbits.queuesystem.databinding.ActivityMainBinding
import com.orbits.queuesystem.helper.AlertDialogInterface
import com.orbits.queuesystem.helper.BaseActivity
import com.orbits.queuesystem.helper.ClientDataModel
import com.orbits.queuesystem.helper.CommonInterfaceClickEvent
import com.orbits.queuesystem.helper.Constants
import com.orbits.queuesystem.helper.Dialogs
import com.orbits.queuesystem.helper.Extensions.asInt
import com.orbits.queuesystem.helper.Extensions.asString
import com.orbits.queuesystem.helper.MessageListener
import com.orbits.queuesystem.helper.PrefUtils.getUserDataResponse
import com.orbits.queuesystem.helper.PrefUtils.setUserDataResponse
import com.orbits.queuesystem.helper.ServiceConfig.parseInServiceModelArraylist
import com.orbits.queuesystem.helper.ServiceConfig.parseInServiceDbModel
import com.orbits.queuesystem.helper.TCPServer
import com.orbits.queuesystem.helper.UserResponseModel
import com.orbits.queuesystem.helper.WebSocketClient
import com.orbits.queuesystem.helper.database.LocalDB.addServiceInDB
import com.orbits.queuesystem.helper.database.LocalDB.addServiceTokenToDB
import com.orbits.queuesystem.helper.database.LocalDB.getAllServiceFromDB
import com.orbits.queuesystem.helper.database.LocalDB.getStartServiceToken
import com.orbits.queuesystem.helper.database.ServiceDataDbModel
import com.orbits.queuesystem.mvvm.counters.view.CounterListActivity
import com.orbits.queuesystem.mvvm.main.adapter.ServiceListAdapter
import com.orbits.queuesystem.mvvm.main.model.ServiceListDataModel
import java.io.OutputStream
import java.net.Socket

class MainActivity : BaseActivity() , MessageListener {
    private lateinit var binding: ActivityMainBinding
    private var adapter = ServiceListAdapter()
    private var arrListService = ArrayList<ServiceListDataModel?>()
    private lateinit var tcpServer: TCPServer
    private lateinit var socket: Socket
    private lateinit var webSocketClient: WebSocketClient
    private var clientModel = ClientDataModel()
    private var outStream: OutputStream? = null
    private var arrListClients = ArrayList<String>()
    val gson = Gson()
    var serviceId = ""

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

    private fun initializeSocket(){
        tcpServer = TCPServer(8085,this)
        Thread {
            tcpServer.start()
        }.start()

        webSocketClient = WebSocketClient(8085)
        webSocketClient.start()

    }

    private fun initializeFields(){
        binding.rvServiceList.adapter = adapter
        setData(parseInServiceModelArraylist(getAllServiceFromDB()))
    }

    private fun setData(data: ArrayList<ServiceListDataModel?>) {
        arrListService.clear()
        arrListService.addAll(data)
        adapter.onClickEvent = object : CommonInterfaceClickEvent {
            override fun onItemClick(type: String, position: Int) {

            }
        }
        adapter.setData(arrListService)
    }

    private fun onClickListeners(){
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
            if (!json.isJsonNull){
                println("Received json in activity: $json")
                if (json.has("serviceType")){
                    serviceId = json.get("serviceId")?.asString ?: ""
                    println("here is service id $serviceId")
                }

            } else {
                socket.close()
            }

        }
    }

    fun Context.createJsonData(): JsonObject {
        val itemsArray = JsonArray().apply {
            val services = getAllServiceFromDB()
            services?.forEach { service ->
                add(service?.toJsonObject())
            }
        }

        return JsonObject().apply {
            add("items", itemsArray)
        }
    }

    private fun Context.createServiceJsonData(): JsonObject {
        val model = getAllServiceFromDB()?.find { it?.id.asString() == serviceId }
        println("here is start token ${getStartServiceToken(model?.entityID ?: "")}")
        return JsonObject().apply {
            addProperty("startToken", model?.tokenStart)
            addProperty("endToken", model?.tokenEnd)
            addProperty("serviceName", model?.serviceName)
            addProperty("serviceName", model?.serviceName)
            addProperty("tokenNo", getStartServiceToken(model?.entityID ?: ""))
        }
    }


    private fun ServiceDataDbModel.toJsonObject(): JsonObject {
        return JsonObject().apply {
            addProperty("id", entityID)
            addProperty("name", serviceName)
            addProperty("tokenStart", tokenStart)
            addProperty("tokenEnd", tokenEnd)
        }
    }

    override fun onClientConnected(clientSocket: Socket?, clientList: List<String>) {
        Thread {
            try {
                outStream = clientSocket?.getOutputStream()
                if (clientSocket != null) {
                    socket = clientSocket
                    runOnUiThread {
                        Toast.makeText(this@MainActivity,"Client Connected", Toast.LENGTH_SHORT).show()
                        println("here is client list fahad ${clientList}")
                        arrListClients.clear()
                        arrListClients.addAll(clientList)
                        println("here is client list fahad 111 ${arrListClients}")
                        arrListClients.forEach {
                            if (serviceId.isNotEmpty()){
                                sendMessageToWebSocketClient(it, createServiceJsonData())
                                addServiceTokenToDB(serviceId,getStartServiceToken(serviceId).plus(1))
                                serviceId = ""
                            } else {
                                sendMessageToWebSocketClient(it, createJsonData())
                            }
                        }
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
            Thread{
                val jsonMessage = gson.toJson(jsonObject)
                clientHandler.sendMessageToClient(clientId, jsonMessage)
            }.start()
            // Optionally handle success or error
        } else {
            // Handle case where clientHandler is not found or not a WebSocket client
        }
    }
}