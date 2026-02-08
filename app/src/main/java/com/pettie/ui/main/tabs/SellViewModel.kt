package com.pettie.ui.main.tabs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pettie.data.repository.PetListingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellViewModel @Inject constructor(
    private val repository: PetListingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SellUiState>(SellUiState.Idle)
    val uiState: StateFlow<SellUiState> = _uiState.asStateFlow()

    fun createListing(
        title: String,
        species: String,
        price: String,
        location: String,
        description: String
    ) {
        val numericPrice = price.toDoubleOrNull()

        when {
            title.isBlank() || species.isBlank() || price.isBlank() || location.isBlank() -> {
                _uiState.value = SellUiState.Error("Please fill in all required fields")
                return
            }

            numericPrice == null || numericPrice <= 0.0 -> {
                _uiState.value = SellUiState.Error("Please enter a valid price")
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = SellUiState.Loading
            repository
                .createListing(
                    title = title,
                    description = description,
                    species = species,
                    price = numericPrice,
                    location = location
                )
                .onSuccess {
                    _uiState.value = SellUiState.Success
                }
                .onFailure { error ->
                    _uiState.value = SellUiState.Error(error.message ?: "Failed to create listing")
                }
        }
    }

    fun clearState() {
        _uiState.value = SellUiState.Idle
    }
}

sealed class SellUiState {
    object Idle : SellUiState()
    object Loading : SellUiState()
    object Success : SellUiState()
    data class Error(val message: String) : SellUiState()
}

