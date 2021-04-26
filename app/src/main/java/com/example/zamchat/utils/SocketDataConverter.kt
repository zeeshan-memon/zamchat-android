package com.example.zamchat.utils

import android.util.Log
import androidx.lifecycle.MutableLiveData
import org.json.JSONException
import org.json.JSONObject


object SocketDataConverter {

    fun socketDataConverter(socketData: Array<Any>, mutableLiveData: MutableLiveData<JSONObject>) {
        Log.i("socketDataConverter", socketData[0].toString())
        try {
            mutableLiveData.postValue(JSONObject(socketData[0].toString()))
        } catch (e: JSONException) {
            Log.i("SocketDataConverter", e.toString())
        }
    }
}