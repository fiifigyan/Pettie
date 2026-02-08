package com.pettie.ui.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pettie.data.model.PetListing
import com.pettie.data.repository.PetListingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeListingsViewModel @Inject constructor(
    private val repository: PetListingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeListingsUiState>(HomeListingsUiState.Loading)
    val uiState: StateFlow<HomeListingsUiState> = _uiState.asStateFlow()

    init {
        observeListings()
    }

    private fun observeListings() {
        viewModelScope.launch {
            repository.getRecentListings().collect { result ->
                result
                    .onSuccess { listings ->
                        _uiState.value = if (listings.isEmpty()) {
                            HomeListingsUiState.Empty
                        } else {
                            HomeListingsUiState.Success(listings)
                        }
                    }
                    .onFailure { error ->
                        _uiState.value = HomeListingsUiState.Error(error.message ?: "Failed to load listings")
                    }
            }
        }
    }
}

sealed class HomeListingsUiState {
    object Loading : HomeListingsUiState()
    object Empty : HomeListingsUiState()
    data class Success(val listings: List<PetListing>) : HomeListingsUiState()
    data class Error(val message: String) : HomeListingsUiState()
}

