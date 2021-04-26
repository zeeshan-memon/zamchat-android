package com.example.zamchat.view.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.zamchat.data.SocketConnectivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.android.AndroidInjection
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket
import javax.inject.Inject

//@Inject
//lateinit var socketConnectivity: SocketConnectivity
class FreebaseMessagingService : FirebaseMessagingService() {



    override fun onCreate() {
//        AndroidInjection.inject(this)
//        socketConnectivity.socket.connect()
        initSocket()
        super.onCreate()
    }


    private fun initSocket() {

        Log.i("Socket CALLEDs", "CALLEDZEEEE!!!")

        val options = IO.Options()
        options.forceNew = true
        options.reconnection = true
        options.secure = true
        options.transports = arrayOf(WebSocket.NAME)
        options.reconnectionAttempts = 99999
        options.reconnectionDelay = 1000.toLong()
        options.reconnectionDelayMax = 300.toLong()
        options.query = "id= zeeshan"

        val socket = IO.socket("http://192.168.1.165:3000/", options)

        socket.connect()

        socket.on(Socket.EVENT_CONNECT) {

            Log.i("MYSocket Connected", "")
        }

        socket.on(Socket.EVENT_DISCONNECT) {
            Log.i("MYSocket Disconnected", it[0].toString())
        }

        socket.on(Socket.EVENT_CONNECT_ERROR) {
            Log.i("MYSocket ERROR", it[0].toString())
        }


        // return SocketConnectivity(socket)
    }
}
