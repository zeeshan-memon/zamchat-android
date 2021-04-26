package com.example.zamchat.di.component

import android.app.Application
import com.example.zamchat.ZamChatApplication
import com.example.zamchat.di.builder.*
import com.example.zamchat.di.module.SocketModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ViewModelFactoryModule::class,
        ActivityModule::class,
        ViewModelModule::class,
        SocketModule::class,
        ServiceModule::class,
        FragmentModule::class
    ]
)
interface AppComponent : AndroidInjector<ZamChatApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    override fun inject(instance: ZamChatApplication?)
}