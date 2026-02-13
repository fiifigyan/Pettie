package com.pettie.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.pettie.data.model.PetListing
import com.pettie.data.repository.PetListingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: PetListingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val userId = auth.currentUser?.uid

    init {
        if (userId == null) {
            _uiState.value = ProfileUiState.Error("Not signed in")
        } else {
            viewModelScope.launch {
                repository.getListingsByUser(userId).collect { result ->
                    result.onSuccess {
                        _uiState.value = ProfileUiState.Success(auth.currentUser, it)
                    }.onFailure {
                        _uiState.value = ProfileUiState.Error(it.message ?: "Failed to load listings")
                    }
                }
            }
        }
    }
}

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val user: com.google.firebase.auth.FirebaseUser?, val listings: List<PetListing>) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}
