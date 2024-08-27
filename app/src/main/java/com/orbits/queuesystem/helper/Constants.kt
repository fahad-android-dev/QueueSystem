package com.orbits.queuesystem.helper

import android.os.Build
import com.orbits.queuesystem.BuildConfig


object Constants {

    const val APP_PASSWORD = "1234"

    var DEVICE_TOKEN = ""
    val DEVICE_MODEL: String = Build.MODEL
    const val DEVICE_TYPE = "A" //passed in banners
    val OS_VERSION = Build.VERSION.RELEASE
    const val APP_VERSION = BuildConfig.VERSION_NAME




    const val DATE_FORMAT = "yyyy-MM-dd hh:mm:ss"


    var DEVICE_DENSITY = 0.0

    val fontBold = "bold"
    val fontRegular = "regular"
    val fontMedium = "medium"
    val fontRegularRev = "regular_reverse"


    const val TOOLBAR_ICON_ONE = "iconOne"
    const val TOOLBAR_ICON_TWO = "iconTwo"
    const val TOOLBAR_ICON_MENU = "iconMenu"

    const val TRANSACTION = "transaction"

    const val DISPENSER_CLIENT_ID = "007"



    const val KEYPAD_COUNTER_TYPE = "keypadCounterType"
    const val COUNTER_DISPLAY_TYPE = "counterDisplayType"
    const val KEYPAD_CONNECTION_TYPE = "keypadConnectionType"
    const val TICKET_TYPE = "ticketType"
    const val SERVICE_TYPE = "serviceType"
    const val CONNECTION = "connection"
    const val USERNAME = "userName"
    const val DISPLAY_ID = "displayId"

}
