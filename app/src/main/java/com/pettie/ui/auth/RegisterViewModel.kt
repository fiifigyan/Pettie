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
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Initial)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(email: String, password: String, confirmPassword: String, displayName: String) {
        when {
            email.isBlank() || password.isBlank() || displayName.isBlank() -> {
                _uiState.value = RegisterUiState.Error("Please fill in all fields")
                return
            }
            password != confirmPassword -> {
                _uiState.value = RegisterUiState.Error("Passwords do not match")
                return
            }
            password.length < 6 -> {
                _uiState.value = RegisterUiState.Error("Password must be at least 6 characters")
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading
            authRepository.register(email, password, displayName)
                .onSuccess {
                    _uiState.value = RegisterUiState.Success
                }
                .onFailure { e ->
                    _uiState.value = RegisterUiState.Error(e.message ?: "Registration failed")
                }
        }
    }

    fun clearError() {
        _uiState.value = RegisterUiState.Initial
    }
}

sealed class RegisterUiState {
    object Initial : RegisterUiState()
    object Loading : RegisterUiState()
    object Success : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}
