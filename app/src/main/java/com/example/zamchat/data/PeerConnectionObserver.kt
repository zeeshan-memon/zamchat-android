package com.example.zamchat.data

import android.util.Log
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.RtpReceiver


open class PeerConnectionObserver : PeerConnection.Observer {
    override fun onIceCandidate(p0: IceCandidate?) {
        Log.i("ZEEEEEESSSHHNNN", "onIceCandidate")
    }

    override fun onDataChannel(p0: DataChannel?) {
        Log.i("ZEEEEEESSSHHNNN", "onDataChannel")
    }

    override fun onIceConnectionReceivingChange(p0: Boolean) {
        Log.i("ZEEEEEESSSHHNNN", "onIceConnectionReceivingChange")
    }

    override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
        Log.i("ZEEEEEESSSHHNNN", "onIceConnectionChange")
    }

    override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
        Log.i("ZEEEEEESSSHHNNN", "onIceGatheringChange")
    }

    override fun onAddStream(p0: MediaStream?) {
        Log.i("ZEEEEEESSSHHNNN", "onAddStream")
    }

    override fun onSignalingChange(p0: PeerConnection.SignalingState?) {
        Log.i("ZEEEEEESSSHHNNN", "onSignalingChange")
    }

    override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
        Log.i("ZEEEEEESSSHHNNN", "onIceCandidatesRemoved")
    }

    override fun onRemoveStream(p0: MediaStream?) {
        Log.i("ZEEEEEESSSHHNNN", "onRemoveStream")
    }

    override fun onRenegotiationNeeded() {
        Log.i("ZEEEEEESSSHHNNN", "onRenegotiationNeeded")
    }

    override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {
        Log.i("ZEEEEEESSSHHNNN", "onAddTrack")
    }
}