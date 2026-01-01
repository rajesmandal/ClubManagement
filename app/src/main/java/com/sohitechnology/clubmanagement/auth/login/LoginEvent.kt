package com.sohitechnology.clubmanagement.auth.login

sealed class LoginEvent {
    data class EmailChanged(val value: String) : LoginEvent()
    data class PasswordChanged(val value: String) : LoginEvent()
    object LoginClicked : LoginEvent()

}