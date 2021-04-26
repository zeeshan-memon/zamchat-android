package com.example.zamchat.view.fragment.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.zamchat.R
import com.example.zamchat.data.CustomSdpObserver
import com.example.zamchat.data.PeerConnectionObserver
import com.example.zamchat.data.SocketConnectivity
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.*
import org.webrtc.PeerConnection.IceServer
import org.webrtc.PeerConnection.RTCConfiguration
import org.webrtc.PeerConnectionFactory.InitializationOptions
import java.util.*
import javax.inject.Inject

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    @Inject
    lateinit var socketConnectivity: SocketConnectivity
    val ALL_PERMISSIONS_CODE = 1
    var rootEglBase: EglBase? = null
    var peerConnectionFactory: PeerConnectionFactory? = null
    private val TAG = "MainActivity"
    var audioConstraints: MediaConstraints? = null
    var videoConstraints: MediaConstraints? = null
    var sdpConstraints: MediaConstraints? = null
    var videoSource: VideoSource? = null
    var videoCapturerAndroid: VideoCapturer? = null
    var localVideoTrack: VideoTrack? = null
    var audioSource: AudioSource? = null
    var localAudioTrack: AudioTrack? = null
    var surfaceTextureHelper: SurfaceTextureHelper? = null
    var isBackCamera = false
    var peerIceServers: List<IceServer> = ArrayList()

    val stunServer = listOf(
        IceServer.builder("stun:stun.l.google.com:19302")
            .createIceServer(),
        IceServer.builder("stun:stun1.l.google.com:19302")
            .createIceServer(),
        IceServer.builder("stun:stun3.l.google.com:19302")
            .createIceServer(),
        IceServer.builder("stun:stun01.sipphone.com")
            .createIceServer(),
        IceServer.builder("stun:stun4.l.google.com:1930")
            .createIceServer(),
        IceServer.builder("turn:numb.viagenie.ca")
            .setUsername("webrtc@live.com")
            .setPassword("muazkh")
            .createIceServer(),
        IceServer.builder("turn:192.158.29.39:3478?transport=udp")
            .setUsername("28224511:1379330808")
            .setPassword("JZEOEt2V3Qb0y27GRntt2u2PAYA=")
            .createIceServer(),
        IceServer.builder("turn:192.158.29.39:3478?transport=tcp")
            .setUsername("JZEOEt2V3Qb0y27GRntt2u2PAYA=")
            .setPassword("28224511:1379330808")
            .createIceServer()

    )

/*
    val stunServer = listOf(
        IceServer.builder("stun:stun.l.google.com:19302")
            .createIceServer()
    )
*/


    var offer = JSONObject()
    var answer = JSONObject()
    var localPeer: PeerConnection? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
//        val textView: TextView = root.findViewById(R.id.text_home)
//        homeViewModel.text.observe(this, Observer {
//            textView.text = it
//        })

        AndroidSupportInjection.inject(this)
        return root
    }

    @SuppressLint("DefaultLocale")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                ),
                ALL_PERMISSIONS_CODE
            )
        } else {
            // all permissions already granted
            start()
        }

        switch_camera.setOnClickListener {

            addCameraSource()
        }
        make_call.setOnClickListener {

            createIceCandidate()
        }

        receive_call.setOnClickListener {
            doAnswer()
        }
        end_call.setOnClickListener {
            hangup()
            socketConnectivity.socket.emit("endcall", "")
        }
        socketConnectivity.offer.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            offer = it
            Log.i("offerObserver", it.toString())
            try {
                make_call.visibility = View.GONE
                receive_call.visibility = View.VISIBLE
                updateVideoViews(true)

                if(localPeer == null) {
                    createIceCandidate1()
                }
                localPeer!!.setRemoteDescription(
                    CustomSdpObserver(),
                    SessionDescription(SessionDescription.Type.OFFER, it.getString("sdp"))
                )


            } catch (e: JSONException) {

                Log.i("errrror", e.toString() )
            }

        })

        socketConnectivity.answer.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("answerObserver", it.toString())

            try {
                localPeer!!.setRemoteDescription(
                    CustomSdpObserver(),
                    SessionDescription(
                        SessionDescription.Type.fromCanonicalForm(it.getString("type").toLowerCase()),
                        it.getString("sdp")
                    )
                )
                updateVideoViews(true)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        })

        socketConnectivity.candidate.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            try {
                Log.i("candidateObserver", it.toString())
                localPeer!!.addIceCandidate(IceCandidate(it.getString("id"),it.getInt("label"),it.getString("candidate")))

            }catch (jsonException:JSONException){
                Log.i("candidate",jsonException.toString())
            }
        })
        socketConnectivity.encCall.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            try {
                Log.i("encCallObserver", it.toString())
                    hangup()
            }catch (jsonException:JSONException){
                Log.i("encCall",jsonException.toString())
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == ALL_PERMISSIONS_CODE && grantResults.size == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
        ) {
            // all permissions granted
            start()
        } else {
            //  finish()
        }
    }


    private fun start() {
        initializeViews()
        addCameraSource()
    }
    fun createIceCandidate1(){

        val rtcConfig = RTCConfiguration(stunServer)

        // TCP candidates are only useful when connecting to a server that supports
        // ICE-TCP.
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE
        rtcConfig.continualGatheringPolicy =
            PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
        // Use ECDSA encryption.
        rtcConfig.keyType = PeerConnection.KeyType.ECDSA
        localPeer = peerConnectionFactory!!.createPeerConnection(
            rtcConfig,
            object : PeerConnectionObserver() {
                override fun onIceCandidate(p0: IceCandidate?) {
                    super.onIceCandidate(p0)
                    /**
                     * Received local ice candidate. Send it to remote peer through signalling for negotiation
                     */
                    localPeer!!.addIceCandidate(p0)
                    Log.i("onIceCandidate", p0.toString())
                    val data = JSONObject()
                    data.put("type", "candidate")
                    data.put("label", p0!!.sdpMLineIndex)
                    data.put("id", p0.sdpMid)
                    data.put("candidate", p0.sdp)
                    data.put("sender", "zeeshan")
                    data.put("receiver", "zam")
                    socketConnectivity.socket.emit("candidate", data)

                }

                override fun onAddStream(p0: MediaStream?) {
                    super.onAddStream(p0)

                    gotRemoteStream(p0!!)
                }
            }
        )!!
        addStreamToLocalPeer()

        Log.i("connected",socketConnectivity.socket.connected().toString())


    }

    fun createIceCandidate(){

        val rtcConfig = RTCConfiguration(stunServer)

        // TCP candidates are only useful when connecting to a server that supports
        // ICE-TCP.
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE
        rtcConfig.continualGatheringPolicy =
            PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
        // Use ECDSA encryption.
        rtcConfig.keyType = PeerConnection.KeyType.ECDSA
        localPeer = peerConnectionFactory!!.createPeerConnection(
            rtcConfig,
            object : PeerConnectionObserver() {
                override fun onIceCandidate(p0: IceCandidate?) {
                    super.onIceCandidate(p0)
                    /**
                     * Received local ice candidate. Send it to remote peer through signalling for negotiation
                     */
                    localPeer!!.addIceCandidate(p0)
                    Log.i("onIceCandidate", p0.toString())
                    val data = JSONObject()
                    data.put("type", "candidate")
                    data.put("label", p0!!.sdpMLineIndex)
                    data.put("id", p0.sdpMid)
                    data.put("candidate", p0.sdp)
                    data.put("sender", "zeeshan")
                    data.put("receiver", "zam")
                    socketConnectivity.socket.emit("candidate", data)

                }

                override fun onAddStream(p0: MediaStream?) {
                    super.onAddStream(p0)

                    gotRemoteStream(p0!!)
                }
            }
        )!!
        addStreamToLocalPeer()

            Log.i("connected",socketConnectivity.socket.connected().toString())
            doCall()

    }
    /**
     * This method is called when the app is the initiator - We generate the offer and send it over through socket
     * to remote peer
     */
    private fun doCall() {
        Log.i("DPCALLL","DO CALLL")
        sdpConstraints = MediaConstraints()
        sdpConstraints!!.mandatory.add(
            MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true")
        )
        sdpConstraints!!.mandatory.add(
            MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true")
        )
        localPeer!!.createOffer(object : CustomSdpObserver() {

            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                super.onCreateSuccess(sessionDescription)

                localPeer!!.setLocalDescription(
                    CustomSdpObserver(),
                    sessionDescription
                )
                Log.d("onCreateSuccess", "SignallingClient emit ")
                try {
                    Log.d(
                        "SignallingClient",
                        "emitMessage() called with: message = [$sessionDescription]"
                    )
                    val obj = JSONObject()
                    obj.put("type", sessionDescription.type.canonicalForm())
                    obj.put("sdp", sessionDescription.description)
                    Log.d("emitMessage", obj.toString())
                    obj.put("sender", "zeeshan")
                    obj.put("receiver", "zam")
                    socketConnectivity.socket.emit("offer", obj)
                    Log.d("vivek1794", obj.toString())
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                // SignallingClient.getInstance().emitMessage(sessionDescription)
            }
        }, sdpConstraints)
    }

    private fun doAnswer() {

        Log.d(
            "doAnswer",
            "emitMessage() called with: message "
        )
        localPeer!!.createAnswer(object : CustomSdpObserver() {

            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                super.onCreateSuccess(sessionDescription)
                localPeer!!.setLocalDescription(
                    CustomSdpObserver(),
                    sessionDescription
                )
                val obj = JSONObject()
                obj.put("type", sessionDescription.type.canonicalForm())
                obj.put("sdp", sessionDescription.description)
                obj.put("sender", "zam")
                obj.put("receiver", "zeeshan")
                Log.d("emitMessage", obj.toString())
                socketConnectivity.socket.emit("answer", obj)
                Log.d("vivek1794", obj.toString())
            }

        }, MediaConstraints())
    }
    private fun updateVideoViews(remoteVisible: Boolean) {
            Log.i("remoteVisible",remoteVisible.toString())
//            var params: ViewGroup.LayoutParams = local_gl_surface_view.layoutParams
//            if (remoteVisible) {
//                params.height = dpToPx(100)
//                params.width = dpToPx(100)
//            } else {
//                params = FrameLayout.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT
//                )
//            }
//        local_gl_surface_view.layoutParams = params

    }
    /**
     * Adding the stream to the localpeer
     */
    private fun addStreamToLocalPeer() { //creating local mediastream
        Log.i("addStreamToLocalPeer", "addStreamToLocalPeer")
        val stream = peerConnectionFactory!!.createLocalMediaStream("102")
        stream.addTrack(localAudioTrack)
        stream.addTrack(localVideoTrack)
        localPeer!!.addStream(stream)
    }

    private fun initializeViews() {
        activity!!.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        rootEglBase = EglBase.create()
        local_gl_surface_view.init(rootEglBase!!.eglBaseContext, null)
        remote_gl_surface_view.init(rootEglBase!!.getEglBaseContext(), null)
        local_gl_surface_view.setZOrderMediaOverlay(true)
        remote_gl_surface_view.setZOrderMediaOverlay(true)


        //Initialize PeerConnectionFactory globals.
        val initializationOptions =
            InitializationOptions.builder(context)
                .createInitializationOptions()
        PeerConnectionFactory.initialize(initializationOptions)

        //Create a new PeerConnectionFactory instance - using Hardware encoder and decoder.
        val options = PeerConnectionFactory.Options()
        val defaultVideoEncoderFactory = DefaultVideoEncoderFactory(
            rootEglBase!!.eglBaseContext,  /* enableIntelVp8Encoder */
            true,  /* enableH264HighProfile */
            true
        )
        val defaultVideoDecoderFactory = DefaultVideoDecoderFactory(rootEglBase!!.eglBaseContext)
        peerConnectionFactory = PeerConnectionFactory.builder()
            .setOptions(options)
            .setVideoEncoderFactory(defaultVideoEncoderFactory)
            .setVideoDecoderFactory(defaultVideoDecoderFactory)
            .createPeerConnectionFactory()
    }

    /**
     * Received remote peer's media stream. we will get the first video track and render it
     */
    private fun gotRemoteStream(stream: MediaStream) { //we have remote video stream. add to the renderer.
        val videoTrack = stream.videoTracks[0]
        try {
            Log.i("gotRemoteStream","gotRemoteStream")
            remote_gl_surface_view.visibility = View.VISIBLE
            videoTrack.addSink(remote_gl_surface_view)
        } catch (e: Exception) {
           Log.i("SREAMERROR",e.toString())
        }
    }

    private fun addCameraSource() {

        if (videoCapturerAndroid != null && localVideoTrack != null) {
            videoCapturerAndroid!!.dispose()
            videoCapturerAndroid!!.stopCapture()
            videoSource!!.dispose()
            localVideoTrack!!.removeSink(local_gl_surface_view)
        }
        //Now create a VideoCapturer instance.
        videoCapturerAndroid = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            createCameraCapturer(Camera2Enumerator(context))!!
        } else {
            createCameraCapturer(Camera1Enumerator(false))!!
        }

        //Create MediaConstraints - Will be useful for specifying video and audio constraints.
        audioConstraints = MediaConstraints()
        videoConstraints = MediaConstraints()

        //Create a VideoSource instance
        surfaceTextureHelper =
            SurfaceTextureHelper.create("CaptureThread", rootEglBase!!.eglBaseContext)
        videoSource = peerConnectionFactory!!.createVideoSource(videoCapturerAndroid!!.isScreencast)
        videoCapturerAndroid!!.initialize(
            surfaceTextureHelper,
            context,
            videoSource!!.capturerObserver
        )


        localVideoTrack = peerConnectionFactory!!.createVideoTrack("100", videoSource)

        //create an AudioSource instance
        audioSource = peerConnectionFactory!!.createAudioSource(audioConstraints)
        localAudioTrack = peerConnectionFactory!!.createAudioTrack("101", audioSource)

        videoCapturerAndroid!!.startCapture(1024, 720, 30)

        local_gl_surface_view.visibility = View.VISIBLE

        // can add our renderer to the VideoTrack.
        localVideoTrack!!.addSink(local_gl_surface_view)

        local_gl_surface_view.setMirror(true)
        remote_gl_surface_view.setMirror(true)
    }

    private fun createCameraCapturer(enumerator: CameraEnumerator): VideoCapturer? {

        Log.i("ISBACKCAMERAAAAA", isBackCamera.toString())
        if (isBackCamera) {
            isBackCamera = false
            val deviceNames = enumerator.deviceNames
            // First, try to find front facing camera
            Logging.d(TAG, "Looking for  back cameras.")
            for (deviceName in deviceNames) {

                if (enumerator.isBackFacing(deviceName)) {
                    Logging.d(TAG, deviceName.toString())
                    val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                    if (videoCapturer != null) {
                        return videoCapturer
                    }
                }
            }
            // Front facing camera not found, try something else
            Logging.d(TAG, "Looking for other cameras.")
            for (deviceName in deviceNames) {
                if (!enumerator.isFrontFacing(deviceName)) {
                    Logging.d(TAG, "Creating other camera capturer.")
                    val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                    if (videoCapturer != null) {
                        return videoCapturer
                    }
                }
            }
        } else {
            isBackCamera = true
            val deviceNames = enumerator.deviceNames
            // First, try to find front facing camera
            Logging.d(TAG, "Looking for front facing cameras.")
            for (deviceName in deviceNames) {

                if (enumerator.isFrontFacing(deviceName)) {
                    Logging.d(TAG, deviceName.toString())
                    val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                    if (videoCapturer != null) {
                        return videoCapturer
                    }
                }
            }
            // Front facing camera not found, try something else
            Logging.d(TAG, "Looking for other cameras.")
            for (deviceName in deviceNames) {
                if (!enumerator.isFrontFacing(deviceName)) {
                    Logging.d(TAG, "Creating other camera capturer.")
                    val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                    if (videoCapturer != null) {
                        return videoCapturer
                    }
                }
            }
        }
        return null
    }

    /**
     * Util Methods
     */
    fun dpToPx(dp: Int): Int {
        val displayMetrics = resources.displayMetrics
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    private fun hangup() {
        try {
            if (localPeer != null) {
                localPeer!!.close()
            }
            localPeer!!.close()

            updateVideoViews(false)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}