package com.example.zamchat.di.module

import android.util.Log
import com.example.zamchat.data.SocketConnectivity
import dagger.Module
import dagger.Provides
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket
import org.jetbrains.annotations.NotNull
import java.lang.Exception
import javax.inject.Singleton

@Module
class SocketModule {

 var socket: Socket? = null
    @Provides
    @NotNull
    @Singleton
    fun initSocket (): SocketConnectivity{

        Log.i("Socket CALLED", "CALLED!!!")
        try {
            if(socket == null) {
                val options = IO.Options()
                options.forceNew = true
                options.reconnection = true
                options.secure = true
                options.transports = arrayOf(WebSocket.NAME)
                options.reconnectionAttempts = 99999
                options.reconnectionDelay = 1000.toLong()
                options.reconnectionDelayMax = 300.toLong()
                options.query = "id=zeeshan"
               options.query = "id=zam"

                socket = IO.socket("http://192.168.1.165:8000/", options)
            }
        }catch (e: Exception){

            Log.i("Socket Exception", e.toString())
        }


        return SocketConnectivity(socket!!)
    }
}