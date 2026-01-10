package com.sohitechnology.clubmanagement.core

sealed class NavigationEvent {
    object ToHome : NavigationEvent()      // login success
    object ToLogin : NavigationEvent()     // logout / 401
}