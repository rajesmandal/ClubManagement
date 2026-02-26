package com.sohitechnology.clubmanagement.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sohitechnology.clubmanagement.core.NavigationEvent
import com.sohitechnology.clubmanagement.core.NavigationManager
import com.sohitechnology.clubmanagement.core.session.AppDataStore
import com.sohitechnology.clubmanagement.core.session.SessionKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val dataStore: AppDataStore
) : ViewModel() {

    init {
        decideNavigation()
    }

    private fun decideNavigation() {
        viewModelScope.launch {
            delay(1000) //1 second ka wait
            val isLoggedIn = dataStore.readOnce(
                SessionKeys.IS_LOGGED_IN,
                false
            ) ?: false // one-time read

            if (isLoggedIn) {
                NavigationManager.navigate(
                    NavigationEvent.ToHome
                )
            } else {
                NavigationManager.navigate(
                    NavigationEvent.ToLogin
                )
            }
        }
    }
}
