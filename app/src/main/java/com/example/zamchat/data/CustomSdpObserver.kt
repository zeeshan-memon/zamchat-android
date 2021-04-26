package com.example.zamchat.data

import android.util.Log
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription


internal open class CustomSdpObserver() : SdpObserver {

    override fun onCreateSuccess(sessionDescription: SessionDescription) {
        Log.d(
            "CustomSdpObserver",
            "onCreateSuccess() called with: sessionDescription = [$sessionDescription]"
        )
    }

    override fun onSetSuccess() {
        Log.d("CustomSdpObserver", "onSetSuccess() called")
    }

    override fun onCreateFailure(s: String) {
        Log.d("CustomSdpObserver", "onCreateFailure() called with: s = [$s]")
    }

    override fun onSetFailure(s: String) {
        Log.d("CustomSdpObserver", "onSetFailure() called with: s = [$s]")
    }


}