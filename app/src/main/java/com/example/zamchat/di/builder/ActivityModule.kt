package com.example.zamchat.di.builder

import com.example.zamchat.view.activity.MainActivity
import com.example.zamchat.view.fragment.home.HomeFragment
import dagger.Module
import dagger.android.AndroidInjectionModule
import dagger.android.ContributesAndroidInjector

@Module(includes = [AndroidInjectionModule::class])
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity


}