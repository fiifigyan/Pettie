package com.pettie.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pettie.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ForgotPasswordUiState>(ForgotPasswordUiState.Initial)
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _uiState.value = ForgotPasswordUiState.Error("Please enter your email")
            return
        }

        viewModelScope.launch {
            _uiState.value = ForgotPasswordUiState.Loading
            authRepository.resetPassword(email)
                .onSuccess {
                    _uiState.value = ForgotPasswordUiState.Success
                }
                .onFailure { e ->
                    _uiState.value = ForgotPasswordUiState.Error(e.message ?: "Failed to send reset email")
                }
        }
    }

    fun clearError() {
        _uiState.value = ForgotPasswordUiState.Initial
    }
}

sealed class ForgotPasswordUiState {
    object Initial : ForgotPasswordUiState()
    object Loading : ForgotPasswordUiState()
    object Success : ForgotPasswordUiState()
    data class Error(val message: String) : ForgotPasswordUiState()
}
