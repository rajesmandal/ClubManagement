package com.sohitechnology.clubmanagement.ui.member

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sohitechnology.clubmanagement.core.common.ApiResult
import com.sohitechnology.clubmanagement.core.session.AppDataStore
import com.sohitechnology.clubmanagement.core.session.SessionKeys
import com.sohitechnology.clubmanagement.data.repository.ClubRepository
import com.sohitechnology.clubmanagement.ui.common.DropdownItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemberFilterViewModel @Inject constructor(
    private val repository: ClubRepository,
    private val dataStore: AppDataStore
) : ViewModel() {

    private val _clubs = MutableStateFlow<List<DropdownItem>>(emptyList())
    val clubs = _clubs.asStateFlow()

    init {
        loadClubs()
    }

    private fun loadClubs() {
        viewModelScope.launch {
            val companyId = dataStore.readOnce(
                SessionKeys.COMPANY_ID,
                "0"
            )
            repository.getClubs(companyId.toInt()).collect { result ->
                if (result is ApiResult.Success) {
                        _clubs.value = result.data
                }
            }
        }
    }
}
