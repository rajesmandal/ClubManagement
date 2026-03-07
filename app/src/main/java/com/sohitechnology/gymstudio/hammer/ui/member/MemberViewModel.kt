package com.sohitechnology.gymstudio.hammer.ui.member

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sohitechnology.gymstudio.hammer.core.common.ApiResult
import com.sohitechnology.gymstudio.hammer.core.session.AppDataStore
import com.sohitechnology.gymstudio.hammer.core.session.SessionKeys
import com.sohitechnology.gymstudio.hammer.core.util.ImageUtil
import com.sohitechnology.gymstudio.hammer.data.model.AddMemberRequest
import com.sohitechnology.gymstudio.hammer.data.model.ImageUploadRequest
import com.sohitechnology.gymstudio.hammer.data.model.MemberRequest
import com.sohitechnology.gymstudio.hammer.data.model.UpdateMemberRequest
import com.sohitechnology.gymstudio.hammer.data.repository.AuthRepository
import com.sohitechnology.gymstudio.hammer.data.repository.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemberViewModel @Inject constructor(
    private val repository: MemberRepository,
    private val authRepository: AuthRepository,
    private val dataStore: AppDataStore,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(MemberState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<MemberUiEvent>()
    val event = _event.asSharedFlow()

    private var lastClubId: String = "0"
    private var lastStatus: Int = 0

    fun loadMembers(clubId: String, status: Int, forceRefresh: Boolean) {
        lastClubId = clubId
        lastStatus = status
        
        if (!forceRefresh && _state.value.members.isNotEmpty()) return

        viewModelScope.launch {
            val companyId = dataStore.readOnce(
                SessionKeys.COMPANY_ID,
                "0"
            )
            repository.getMembers(
                MemberRequest(
                    cId = companyId.toInt(),
                    clubId = clubId,
                    status = status
                ),
                forceRefresh
            ).collect { result ->
                when (result) {
                    is ApiResult.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    is ApiResult.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                members = result.data.map { dto -> dto.toUi() }
                            )
                        }
                    }
                    is ApiResult.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun refreshMembers() {
        loadMembers(lastClubId, lastStatus, true)
    }

    fun selectMember(member: MemberUiModel) {
        _state.update { it.copy(selectedMember = member) }
    }

    fun uploadMemberImage(bitmap: Bitmap, memberId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            val base64String = ImageUtil.compressAndEncodeToBase64(context, bitmap)
            
            if (base64String == null) {
                _state.update { it.copy(isLoading = false, error = "Failed to process image") }
                return@launch
            }

            val companyId = dataStore.readOnce(SessionKeys.COMPANY_ID, "0").toIntOrNull() ?: 0

            val request = ImageUploadRequest(
                cId = companyId,
                folderName = "Images",
                base64Strings = base64String,
                fileName = "$memberId-$companyId",
                fileType = "image/jpeg",
                imgKey = "liikolmnbyKhJ7E/BHocqyfX4POWmedEOgsTKJDh/aE="
            )

            authRepository.uploadImage(request).collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        if (result.data.success == true) {
                            val newImageUrl = "https://img.gymstudio.in/${dataStore.readOnce(SessionKeys.COMPANY_ID, "0").toIntOrNull() ?: 0}/Images/${result.data.data.imageName}"
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    selectedMember = it.selectedMember?.copy(image = newImageUrl)
                                )
                            }
                            _event.emit(MemberUiEvent.ImageUploadSuccess(result.data.message ?: "Image uploaded successfully"))
                        } else {
                            _state.update { it.copy(isLoading = false, error = result.data.message ?: "Image upload failed") }
                        }
                    }
                    is ApiResult.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.message) }
                    }
                    is ApiResult.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun updateMember(request: UpdateMemberRequest) {
        viewModelScope.launch {
            repository.updateMember(request).collect { result ->
                when (result) {
                    is ApiResult.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    is ApiResult.Success -> {
                        if (result.data.success == true){
                            _state.update { it.copy(isLoading = false) }
                            _event.emit(MemberUiEvent.UpdateSuccess(result.data.message ?: "Update successful"))
                        } else{
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.data.message ?: "Update failed"
                                )
                            }
                        }
                    }
                    is ApiResult.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun addMember(request: AddMemberRequest) {
        viewModelScope.launch {
            repository.addMember(request).collect { result ->
                when (result) {
                    is ApiResult.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    is ApiResult.Success -> {
                        if (result.data.success == true) {
                            _state.update { it.copy(isLoading = false) }
                            _event.emit(MemberUiEvent.AddSuccess(result.data.message ?: "Member added successfully"))
                        } else {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.data.message ?: "Add member failed"
                                )
                            }
                        }
                    }
                    is ApiResult.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}

sealed class MemberUiEvent {
    data class UpdateSuccess(val message: String) : MemberUiEvent()
    data class AddSuccess(val message: String) : MemberUiEvent()
    data class ImageUploadSuccess(val message: String) : MemberUiEvent()
}
