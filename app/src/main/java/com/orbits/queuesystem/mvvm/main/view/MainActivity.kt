package com.orbits.queuesystem.mvvm.main.view

import NetworkMonitor
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.orbits.queuesystem.R
import com.orbits.queuesystem.databinding.ActivityMainBinding
import com.orbits.queuesystem.databinding.NavHeaderLayoutBinding
import com.orbits.queuesystem.helper.interfaces.AlertDialogInterface
import com.orbits.queuesystem.helper.BaseActivity
import com.orbits.queuesystem.helper.interfaces.CommonInterfaceClickEvent
import com.orbits.queuesystem.helper.Constants
import com.orbits.queuesystem.helper.Dialogs
import com.orbits.queuesystem.helper.Extensions
import com.orbits.queuesystem.helper.Extensions.asInt
import com.orbits.queuesystem.helper.Extensions.asString
import com.orbits.queuesystem.helper.Extensions.getCurrentDateTime
import com.orbits.queuesystem.helper.Extensions.getNextDate
import com.orbits.queuesystem.helper.Extensions.hideKeyboard
import com.orbits.queuesystem.helper.configs.JsonConfig.createJsonData
import com.orbits.queuesystem.helper.configs.JsonConfig.createReconnectionJsonDataWithTransaction
import com.orbits.queuesystem.helper.configs.JsonConfig.createServiceJsonDataWithModel
import com.orbits.queuesystem.helper.configs.JsonConfig.createServiceJsonDataWithTransaction
import com.orbits.queuesystem.helper.configs.JsonConfig.createUserJsonData
import com.orbits.queuesystem.helper.interfaces.MessageListener
import com.orbits.queuesystem.helper.server.ServerService
import com.orbits.queuesystem.helper.configs.ServiceConfig.parseInServiceDbModel
import com.orbits.queuesystem.helper.configs.ServiceConfig.parseInServiceModelArraylist
import com.orbits.queuesystem.helper.server.TCPServer
import com.orbits.queuesystem.helper.configs.TransactionConfig.parseInTransactionDbModel
import com.orbits.queuesystem.helper.database.LocalDB.addServiceInDB
import com.orbits.queuesystem.helper.database.LocalDB.addServiceTokenToDB
import com.orbits.queuesystem.helper.database.LocalDB.addTransactionInDB
import com.orbits.queuesystem.helper.database.LocalDB.deleteServiceInDb
import com.orbits.queuesystem.helper.database.LocalDB.getAllResetData
import com.orbits.queuesystem.helper.database.LocalDB.getAllServiceFromDB
import com.orbits.queuesystem.helper.database.LocalDB.getAllTransactionFromDB
import com.orbits.queuesystem.helper.database.LocalDB.getTransactionFromDbWithIssuedStatus
import com.orbits.queuesystem.helper.database.LocalDB.getCounterIdForService
import com.orbits.queuesystem.helper.database.LocalDB.getCurrentServiceToken
import com.orbits.queuesystem.helper.database.LocalDB.getLastTransactionFromDbWithStatusOne
import com.orbits.queuesystem.helper.database.LocalDB.getLastTransactionFromDbWithStatusTwo
import com.orbits.queuesystem.helper.database.LocalDB.getResetData
import com.orbits.queuesystem.helper.database.LocalDB.getServiceById
import com.orbits.queuesystem.helper.database.LocalDB.getTransactionByToken
import com.orbits.queuesystem.helper.database.LocalDB.getTransactionFromDbWithCalledStatus
import com.orbits.queuesystem.helper.database.LocalDB.getTransactionFromDbWithDisplayStatus
import com.orbits.queuesystem.helper.database.LocalDB.isCounterAssigned
import com.orbits.queuesystem.helper.database.LocalDB.isResetDoneInDb
import com.orbits.queuesystem.helper.database.LocalDB.resetAllTransactionInDb
import com.orbits.queuesystem.helper.database.LocalDB.updateCurrentDateTimeInDb
import com.orbits.queuesystem.helper.database.LocalDB.updateLastDateTimeInDb
import com.orbits.queuesystem.helper.database.LocalDB.updateResetDateTime
import com.orbits.queuesystem.mvvm.counters.view.CounterListActivity
import com.orbits.queuesystem.mvvm.main.adapter.ServiceListAdapter
import com.orbits.queuesystem.mvvm.main.model.DisplayListDataModel
import com.orbits.queuesystem.mvvm.main.model.ServiceListDataModel
import com.orbits.queuesystem.mvvm.main.model.TransactionListDataModel
import com.orbits.queuesystem.mvvm.reset.view.ResetActivity
import com.orbits.queuesystem.mvvm.users.view.UserListActivity
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
    private var outStream: OutputStream? = null
    private val arrListClients = CopyOnWriteArrayList<String>()
    private var arrListDisplays = ArrayList<DisplayListDataModel?>()
    private lateinit var networkMonitor: NetworkMonitor
    private lateinit var headerLayout: NavHeaderLayoutBinding
    val gson = Gson()
    var serviceId = ""
    var serviceType = ""


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        networkMonitor = NetworkMonitor(this) {
            startServerService()
            runOnUiThread {
                initializeSocket()
            }
        }
        networkMonitor.registerNetworkCallback()

        updateCurrentDateTimeInDb(getCurrentDateTime())
        println("here is reset data in main ${getAllResetData()}")

        initResetData()
        initLeftNavMenuDrawer()
        initializeToolbar()
        initializeFields()
        onClickListeners()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initResetData(){
        if(getAllResetData()?.isNotEmpty() == true){
            println("here is data reset everyday ${getAllResetData()}")
            if (isResetDoneInDb()){
                println("here is data resetted")
                resetAllTransactionInDb()
                Toast.makeText(this@MainActivity,
                    getString(R.string.queue_reset_successfully), Toast.LENGTH_SHORT).show()
                updateLastDateTimeInDb(getCurrentDateTime())
                updateResetDateTime(getNextDate(getResetData()?.resetDateTime ?: ""))
            }
        }
    }

    private fun initLeftNavMenuDrawer() {
        headerLayout = binding.headerLayout

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                binding.drawerLayout.hideKeyboard()
            }
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerStateChanged(newState: Int) {}
        })

        onLeftNavMenuDrawerClickListener()
    }

    private fun onLeftNavMenuDrawerClickListener() {
        headerLayout.conHome.setOnClickListener {
            binding.drawerLayout.closeDrawers()
        }

        headerLayout.conUsers.setOnClickListener {
            val intent = Intent(this@MainActivity, UserListActivity::class.java)
            startActivity(intent)
        }

        headerLayout.conReset.setOnClickListener {
            val intent = Intent(this@MainActivity, ResetActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initializeToolbar() {
        setUpToolbar(
            binding.layoutToolbar,
            title = getString(R.string.app_name),
            isBackArrow = false,
            iconMenu = R.drawable.ic_menu,
            toolbarClickListener = object : CommonInterfaceClickEvent {
                override fun onToolBarListener(type: String) {
                    when (type) {
                        Constants.TOOLBAR_ICON_ONE -> {
                            val intent = Intent(this@MainActivity, CounterListActivity::class.java)
                            startActivity(intent)
                        }
                        Constants.TOOLBAR_ICON_MENU -> {
                            binding.drawerLayout.open()
                        }
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
                }
                else if(json.has(Constants.CONNECTION)){
                    sendMessageToWebSocketClient(arrListClients.lastOrNull()?.toString() ?: "", createJsonData())
                }
                else if(json.has(Constants.USERNAME)){
                    arrListClients.forEach {
                        sendMessageToWebSocketClient(it ?: "", createUserJsonData(json.get("userName").asString))
                    }
                }
                else if (json.has(Constants.DISPLAY_ID)){
                    manageCounterDisplayData(json)
                }
                else {
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
                    issueTime = model?.get("issueTime")?.asString ?: "",
                    startKeypadTime = null,
                    endKeypadTime = null,
                    status = model?.get("status")?.asString ?: ""

                )
                println("here is transactions for display ${getAllTransactionFromDB()}")
                val dbModel =
                    parseInTransactionDbModel(updateModel, updateModel.id ?: "")
                println("here is dbModel 111 ${dbModel}")
                addTransactionInDB(dbModel)

                println("here is transactions 111 ${getAllTransactionFromDB()}")
                println("here is transactions with status 4 ::  ${ getTransactionFromDbWithDisplayStatus(serviceId)}")

                sendMessageToWebSocketClient(
                    model?.get("displayId")?.asString ?: "",
                    createServiceJsonDataWithTransaction(
                        getTransactionFromDbWithCalledStatus(serviceId)
                    )
                )
            }

        }
        else if (json.has("Reconnection")) {
            sendMessageToWebSocketClientWith(
                json.get("displayId")?.asString ?: "",
                createReconnectionJsonDataWithTransaction(
                    getLastTransactionFromDbWithStatusTwo(json.get("serviceId")?.asString ?: "")
                ),
                onSuccess = {
                    val model = DisplayListDataModel(
                        id = json.get("displayId")?.asString ?: "",
                        counterId = json.get("counterId")?.asString ?: "",
                        serviceId = json.get("serviceId")?.asString ?: ""

                    )

                    arrListDisplays.add(model)
                },
                onFailure = { e ->
                    println("Error: ${e.message}")
                    // Handle failure, such as logging or notifying the user
                }
            )

            println("here is changed transactions issue model 2222 ${getTransactionFromDbWithCalledStatus(json.get("serviceId")?.asString ?: "")}")

        }
        else {
            println("here is transaction with service id ${json.get("serviceId")?.asString ?: ""}")
            println("here is transaction with with all status  ${getAllTransactionFromDB()}")
            println("here is transaction with status 1 in dislpay  ${getLastTransactionFromDbWithStatusOne(json.get("serviceId")?.asString ?: "")}")

            sendMessageToWebSocketClientWith(
                json.get("displayId")?.asString ?: "",
                createServiceJsonDataWithTransaction(getLastTransactionFromDbWithStatusOne(json.get("serviceId")?.asString ?: "")),
                onSuccess = {
                    val model = DisplayListDataModel(
                        id = json.get("displayId")?.asString ?: "",
                        counterId = json.get("counterId")?.asString ?: "",
                        serviceId = json.get("serviceId")?.asString ?: ""

                    )

                    arrListDisplays.add(model)
                },
                onFailure = { e ->
                    println("Error: ${e.message}")
                    // Handle failure, such as logging or notifying the user
                }
            )





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
                    status = "2"

                )
                val dbModel = parseInTransactionDbModel(updateModel, updateModel.id ?: "")
                addTransactionInDB(dbModel)

                println("here is transactions 0000 ${getAllTransactionFromDB()}")
                println("here is transactions with status ${ getTransactionFromDbWithIssuedStatus(serviceId)}")

                if ((getTransactionFromDbWithIssuedStatus(serviceId) != null)){
                    println("here is status of transaction ${model.get("status")?.asString}")
                    sendMessageToWebSocketClientWith(
                        json.get("counterId")?.asString ?: "",
                        createServiceJsonDataWithTransaction(
                            getTransactionFromDbWithIssuedStatus(serviceId)
                        ),
                        onSuccess = {
                            println("here is arrlist Display $arrListDisplays")
                            if (arrListDisplays.isNotEmpty()) {
                                println("here is display list  $arrListDisplays")

                                val targetCounterId = json.get("counterId")?.asString ?: ""

                                val matchFound = arrListDisplays.any { it?.counterId == targetCounterId }

                                if (matchFound) {
                                    // Retrieve the matching item
                                    val matchingItem = arrListDisplays.find { it?.counterId == targetCounterId }

                                    if (matchingItem != null) {
                                        println("here is display ids  ${matchingItem.id}")
                                        println("here is counter ids for display  ${matchingItem.counterId}")
                                        val sentModel = getTransactionFromDbWithIssuedStatus(matchingItem.serviceId)
                                        println("here is sentModel 111  $sentModel")

                                        /*sendMessageToWebSocketClient(
                                            matchingItem.id ?: "",
                                            createServiceJsonDataWithTransaction(
                                                sentModel
                                            )
                                        )*/

                                        sendMessageToWebSocketClientWith(
                                            matchingItem.id ?: "",
                                            createServiceJsonDataWithTransaction(sentModel),
                                            onSuccess = {
                                                val displayModel = getTransactionFromDbWithIssuedStatus(matchingItem.serviceId)
                                                val changedDisplayModel = TransactionListDataModel(
                                                    id = displayModel?.id.asString(),
                                                    counterId = displayModel?.counterId,
                                                    serviceId = displayModel?.serviceId,
                                                    entityID = displayModel?.entityID,
                                                    serviceAssign = displayModel?.serviceAssign,
                                                    token = displayModel?.token,
                                                    ticketToken = displayModel?.ticketToken,
                                                    keypadToken = displayModel?.keypadToken,
                                                    issueTime = displayModel?.issueTime,
                                                    startKeypadTime = displayModel?.startKeypadTime,
                                                    endKeypadTime = displayModel?.endKeypadTime,
                                                    status = "1"
                                                )
                                                val changedDisplayDbModel = parseInTransactionDbModel(changedDisplayModel, changedDisplayModel.id ?: "")
                                                println("here is changed transactions model 2222 $changedDisplayDbModel")
                                                addTransactionInDB(changedDisplayDbModel)
                                            },
                                            onFailure = { e ->
                                                println("Error: ${e.message}")
                                                // Handle failure, such as logging or notifying the user
                                            }
                                        )
                                    }
                                }
                            }
                        },
                        onFailure = {}

                    )

                    /*val issueModel = getTransactionFromDbWithIssuedStatus(serviceId)
                    val changedModel = TransactionListDataModel(
                        id = issueModel?.id.asString(),
                        counterId = issueModel?.counterId,
                        serviceId = issueModel?.serviceId,
                        entityID = issueModel?.entityID,
                        serviceAssign = issueModel?.serviceAssign,
                        token = issueModel?.token,
                        ticketToken = issueModel?.ticketToken,
                        keypadToken = issueModel?.keypadToken,
                        issueTime = issueModel?.issueTime,
                        startKeypadTime = getStartTimeForKeypad(),
                        endKeypadTime = issueModel?.endKeypadTime,
                        status = "1"

                    )
                    val changedDbModel = parseInTransactionDbModel(changedModel, changedModel.id ?: "")
                    addTransactionInDB(changedDbModel)*/

                    println("here is changed transactions 1111 ${getAllTransactionFromDB()}")

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

                    val issueModel = getTransactionFromDbWithCalledStatus(json.get("serviceId")?.asString ?: "")
                    val changedModel = TransactionListDataModel(
                        id = issueModel?.id.asString(),
                        counterId = issueModel?.counterId,
                        serviceId = issueModel?.serviceId,
                        entityID = issueModel?.entityID,
                        serviceAssign = issueModel?.serviceAssign,
                        token = issueModel?.token,
                        ticketToken = issueModel?.ticketToken,
                        keypadToken = issueModel?.keypadToken,
                        issueTime = issueModel?.issueTime,
                        startKeypadTime = getStartTimeForKeypad(),
                        endKeypadTime = issueModel?.endKeypadTime,
                        status = "2"

                    )
                    val changedDbModel = parseInTransactionDbModel(changedModel, changedModel.id ?: "")
                    addTransactionInDB(changedDbModel)

                    Extensions.handler(500){
                        println("here is arrlist Display $arrListDisplays")
                        if (arrListDisplays.isNotEmpty()) {
                            println("here is display list  $arrListDisplays")

                            val targetCounterId = json.get("counterId")?.asString ?: ""

                            val matchFound = arrListDisplays.any { it?.counterId == targetCounterId }

                            if (matchFound) {
                                // Retrieve the matching item
                                val matchingItem = arrListDisplays.find { it?.counterId == targetCounterId }

                                if (matchingItem != null) {
                                    println("here is display ids  ${matchingItem.id}")
                                    println("here is counter ids for display  ${matchingItem.counterId}")
                                    val sentModel = getTransactionByToken(json.get("tokenNo")?.asString ?: "")
                                    println("here is sentModel 5555  $sentModel")

                                    sendMessageToWebSocketClientWith(
                                        matchingItem.id ?: "",
                                        createServiceJsonDataWithTransaction(sentModel),
                                        onSuccess = {
                                            val displayModel = getTransactionByToken(json.get("tokenNo")?.asString ?: "")
                                            val changedDisplayModel = TransactionListDataModel(
                                                id = displayModel?.id.asString(),
                                                counterId = displayModel?.counterId,
                                                serviceId = displayModel?.serviceId,
                                                entityID = displayModel?.entityID,
                                                serviceAssign = displayModel?.serviceAssign,
                                                token = displayModel?.token,
                                                ticketToken = displayModel?.ticketToken,
                                                keypadToken = displayModel?.keypadToken,
                                                issueTime = displayModel?.issueTime,
                                                startKeypadTime = displayModel?.startKeypadTime,
                                                endKeypadTime = displayModel?.endKeypadTime,
                                                status = "1"

                                            )
                                            val changedDisplayDbModel = parseInTransactionDbModel(changedDisplayModel, changedDisplayModel.id ?: "")
                                            println("here is changed transactions model 2222 $changedDisplayDbModel")
                                            addTransactionInDB(changedDisplayDbModel)
                                        },
                                        onFailure = { e ->
                                            println("Error: ${e.message}")
                                            // Handle failure, such as logging or notifying the user
                                        }
                                    )
                                }
                            }
                        }

                    }
                }

            }
            else if (json.has("Reconnection")) {
                println("here is service id in reconnection ${json.get("serviceId")?.asString ?: ""}")
                println("here is trasanction in reconnection ${getLastTransactionFromDbWithStatusTwo(json.get("serviceId")?.asString ?: "")}")
                sendMessageToWebSocketClient(
                    json.get("counterId")?.asString ?: "",
                    createReconnectionJsonDataWithTransaction(
                        getLastTransactionFromDbWithStatusTwo(json.get("serviceId")?.asString ?: "")
                    )
                )

                println("here is changed transactions issue model 2222 ${getTransactionFromDbWithCalledStatus(json.get("serviceId")?.asString ?: "")}")

            }
            else {
                println("here is to check for counter 2")
                sendMessageToWebSocketClient(
                    json.get("counterId")?.asString ?: "",
                    createServiceJsonDataWithTransaction(
                        getTransactionFromDbWithIssuedStatus(json.get("serviceId")?.asString ?: "")
                    )
                )

                println("here is changed transactions issue model 2222 ${getTransactionFromDbWithIssuedStatus(json.get("serviceId")?.asString ?: "")}")


                val issueModel = getTransactionFromDbWithIssuedStatus(json.get("serviceId")?.asString ?: "")
                val changedModel = TransactionListDataModel(
                    id = issueModel?.id.asString(),
                    counterId = issueModel?.counterId,
                    serviceId = issueModel?.serviceId,
                    entityID = issueModel?.entityID,
                    serviceAssign = issueModel?.serviceAssign,
                    token = issueModel?.token,
                    ticketToken = issueModel?.ticketToken,
                    keypadToken = issueModel?.keypadToken,
                    issueTime = issueModel?.issueTime,
                    startKeypadTime = getStartTimeForKeypad(),
                    endKeypadTime = issueModel?.endKeypadTime,
                    status = "1"

                )
                val changedDbModel = parseInTransactionDbModel(changedModel, changedModel.id ?: "")
                println("here is changed transactions model 2222 $changedDbModel")
                addTransactionInDB(changedDbModel)

                println("here is changed transactions 2222 ${getAllTransactionFromDB()}")
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
                val dbModel = parseInTransactionDbModel(model, model.id ?: "")
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

    private fun sendMessageToWebSocketClientWith(
        clientId: String,
        jsonObject: JsonObject,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        try {
            val clientHandler = TCPServer.WebSocketManager.getClientHandler(clientId)
            if (clientHandler != null && clientHandler.isWebSocket) {
                Thread {
                    try {
                        val jsonMessage = gson.toJson(jsonObject)
                        println("Sending message to client: $clientId")
                        clientHandler.sendMessageToClient(clientId, jsonMessage)
                        onSuccess()
                    } catch (e: Exception) {
                        // Call the failure callback in case of an exception
                        onFailure(e)
                    }
                }.start()
            } else {
                // Handle case where clientHandler is not found or not a WebSocket client
                onFailure(Exception("Client handler is null or not a WebSocket client"))
            }
        } catch (e: Exception) {
            // Call the failure callback if there's an error outside the thread
            onFailure(e)
        }
    }


    fun getCurrentTimeFormatted(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())

        val currentTime = Date()

        return dateFormat.format(currentTime)
    }

    fun getStartTimeForKeypad(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())

        val currentTime = Date()

        return dateFormat.format(currentTime)
    }
}