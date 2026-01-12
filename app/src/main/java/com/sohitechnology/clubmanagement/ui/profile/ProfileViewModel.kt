package com.sohitechnology.clubmanagement.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sohitechnology.clubmanagement.core.session.AppDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val dataStore: AppDataStore
) : ViewModel() {

    fun logout(){
        viewModelScope.launch {
            dataStore.clear()
        }
    }
}