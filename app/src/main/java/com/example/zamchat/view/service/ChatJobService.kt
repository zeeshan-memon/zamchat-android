package com.example.zamchat.view.service

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.JobIntentService
import com.example.zamchat.data.SocketConnectivity
import dagger.android.AndroidInjection
import javax.inject.Inject


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class ChatJobService : JobIntentService() {

    @Inject
    lateinit var socketConnectivity: SocketConnectivity
    companion object{

        /**
         * Unique job ID for this service.
         */
        val JOB_ID = 1000
        /**
         * Convenience method for enqueuing work in to this service.
         */
        fun enqueueWork(context: Context, work: Intent) {
            Log.i("enqueueWork", "Executing work")
            enqueueWork(context, ChatJobService::class.java, JOB_ID, work)
        }
    }

    override fun onHandleWork(intent: Intent) { // We have received work to do.  The system or framework is already
// holding a wake lock for us at this point, so we can just go.
        Log.i("SimpleJobIntentService", "Executing work: $intent")
        AndroidInjection.inject(this)
        if(!socketConnectivity.socket.connected())
            socketConnectivity.socket.connect()
//        var label = intent.getStringExtra("label")
//        if (label == null) {
//            label = intent.toString()
//        }
//        toast("Executing: $label")
//        for (i in 0..4) {
//            Log.i(
//                "SimpleJobIntentService", "Running service " + (i + 1)
//                        + "/5 @ " + SystemClock.elapsedRealtime()
//            )
//            try {
//                Thread.sleep(1000)
//            } catch (e: InterruptedException) {
//            }
//        }
//        Log.i(
//            "SimpleJobIntentService",
//            "Completed service @ " + SystemClock.elapsedRealtime()
//        )
    }

    override fun onDestroy() {
        super.onDestroy()
        toast("All work complete")
    }

    val mHandler: Handler = Handler()

    // Helper for showing tests
    fun toast(text: CharSequence?) {
        mHandler.post(Runnable {
            Toast.makeText(
                this,
                text,
                Toast.LENGTH_SHORT
            ).show()
        })
    }


}
