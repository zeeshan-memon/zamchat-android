package com.example.zamchat.view.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.zamchat.R
import com.example.zamchat.data.SocketConnectivity
import com.example.zamchat.view.service.ChatService
import com.example.zamchat.view.service.FreebaseMessagingService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjection
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket
import java.util.Observer
import javax.inject.Inject


class MainActivity : AppCompatActivity()  {

    private lateinit var appBarConfiguration: AppBarConfiguration
    @Inject
    lateinit var socketConnectivity: SocketConnectivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        AndroidInjection.inject(this)
//        val mIntent = Intent(this, ChatJobService::class.java)
//        ChatJobService.enqueueWork(this, mIntent)

        //initSocket ()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, ChatService::class.java))
        } else {
            startService(Intent(this, ChatService::class.java))
        }


//        val fab: FloatingActionButton = findViewById(R.id.fab)
//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_gallery,
                R.id.nav_slideshow,
                R.id.nav_tools,
                R.id.nav_share,
                R.id.nav_send
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    //lateinit var socket: Socket
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
