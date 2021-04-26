package com.example.zamchat

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import com.example.zamchat.di.component.AppComponent
import com.example.zamchat.di.component.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.HasAndroidInjector


@SuppressLint("Registered")
/**
 * Kotlin    Application.
 * It's necessary for dagger injection
 */
class ZamChatApplication : DaggerApplication() {
    override fun onCreate() {
        super.onCreate()

    }
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {

        //Build app component
        val appComponent: AppComponent = DaggerAppComponent.builder()
            .application(this)
            .build()
        //inject application instance
        appComponent.inject(this)
        return appComponent
    }
}