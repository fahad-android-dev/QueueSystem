package com.orbits.queuesystem.helper

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build


class AppController : Application() {
    companion object {
        lateinit var instance: AppController
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        showServiceNotification()

    }

    private fun showServiceNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "2",
                "Server Running",
                NotificationManager.IMPORTANCE_MIN // Set the importance to HIGH for high-priority notifications
            ).apply {
                description = "Channel Description"
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}