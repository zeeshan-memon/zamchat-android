package com.example.zamchat.di.builder

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindContext(application: Application): Context

   // @Binds
    //abstract fun bindViewModelFactory(factory: ViewModelFactory) : ViewModelProvider.Factory
}