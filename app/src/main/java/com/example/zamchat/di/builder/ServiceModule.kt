package com.example.zamchat.di.builder

import com.example.zamchat.view.service.ChatJobService
import com.example.zamchat.view.service.ChatService
import com.example.zamchat.view.service.FreebaseMessagingService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceModule
{
    @ContributesAndroidInjector
    abstract fun contributeAuthenticatorService(): ChatService

    @ContributesAndroidInjector
    abstract fun contributeChatJobService(): ChatJobService

    @ContributesAndroidInjector
    abstract fun contributeFirebaseMessagingService(): FreebaseMessagingService
}