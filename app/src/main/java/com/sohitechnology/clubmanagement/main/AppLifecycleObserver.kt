package com.sohitechnology.clubmanagement.main

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLifecycleObserver @Inject constructor() : DefaultLifecycleObserver {

    private var onForeground: () -> Unit = {}

    fun setOnForegroundListener(listener: () -> Unit) {
        onForeground = listener
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        onForeground()
    }
}
