package com.sohitechnology.clubmanagement.core


import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object NavigationManager {

    private val _events = MutableSharedFlow<NavigationEvent>() // one-time events
    val events = _events.asSharedFlow()

    suspend fun navigate(event: NavigationEvent) {
        _events.emit(event) // emit navigation
    }
}