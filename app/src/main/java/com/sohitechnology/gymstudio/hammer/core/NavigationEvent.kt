package com.sohitechnology.gymstudio.hammer.core

sealed class NavigationEvent {
    object ToHome : NavigationEvent()      // login success
    object ToLogin : NavigationEvent()     // logout / 401
}