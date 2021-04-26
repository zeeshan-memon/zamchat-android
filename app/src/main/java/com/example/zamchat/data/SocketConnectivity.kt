package com.example.zamchat.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.zamchat.utils.SocketDataConverter.socketDataConverter
import io.socket.client.Ack
import io.socket.client.Socket
import org.json.JSONObject
import javax.inject.Inject


class SocketConnectivity @Inject constructor(private val mSocket: Socket) {

    var socket: Socket = mSocket
    val message = MutableLiveData<JSONObject>()
    val offer = MutableLiveData<JSONObject>()
    val candidate = MutableLiveData<JSONObject>()
    val answer = MutableLiveData<JSONObject>()
    val encCall = MutableLiveData<JSONObject>()

    init {

        try {
            // mSocket.connect()
            socket.on(Socket.EVENT_CONNECT) {

                Log.i("Socket Connected", "Socket Connected")
                val obj = JSONObject()
                obj.put("message", "Zeeshan is connected")

                socket.emit("message", obj)
            }

            socket.on(Socket.EVENT_DISCONNECT) {
                if (it.isNotEmpty())
                    Log.i("Socket Disconnected", it[0].toString())
            }

            socket.on(Socket.EVENT_CONNECT_ERROR) {
                Log.i("MYSocket ERROR", it[0].toString())
            }

            initializeSocketListeners()
        } catch (e: Exception) {
            Log.i("Socket Exception", e.toString())
        }

    }


    fun initializeSocketListeners() {

        socket.on("message") {

            Log.i("MESSSAAAGGEEE", it.toString())
            socketDataConverter(it, message)
        }

        socket.on("offer") {

            Log.i("OFFERRR", it.toString())
            socketDataConverter(it, offer)
        }

        socket.on("answer") {

            Log.i("ANSWERRRR", it.toString())
            socketDataConverter(it, answer)
        }

        socket.on("candidate") {

            Log.i("candidate", it.toString())
            socketDataConverter(it, candidate)
        }

        socket.on("endcall") {

            Log.i("endcall", it.toString())
            socketDataConverter(it, encCall)
        }
    }
}