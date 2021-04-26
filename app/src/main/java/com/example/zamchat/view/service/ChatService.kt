package com.example.zamchat.view.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import com.example.zamchat.data.SocketConnectivity
import com.example.zamchat.view.activity.MainActivity
import dagger.android.AndroidInjection
import io.socket.client.Socket
import javax.inject.Inject

class ChatService : IntentService("My Intent Service") {

    @Inject
    lateinit var socketConnectivity: SocketConnectivity
    private val CHANNEL_ID = "ZamChaForegroundService"
    lateinit var socket: Socket


    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
        socket = socketConnectivity.socket

        if (!socket.connected())
            socket.connect()

    }

    override fun onHandleIntent(p0: Intent?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @SuppressLint("MissingSuperCall", "ShowToast")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        //do heavy work on a background thread

        // createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service Kotlin Example")
            .setContentText("")
            //.setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)

        socketConnectivity.message.observeForever {
            Log.i("ZOZOZOZOZOservice", it.toString())
            Toast.makeText(this, it.toString(), Toast.LENGTH_LONG)

        }

        return Service.START_STICKY
    }



    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID, "ZamChat",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {

        stopForeground(true)
        super.onDestroy()
    }
}