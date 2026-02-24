package com.pettie.ui.main.tabs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pettie.data.model.PetListing
import com.pettie.data.repository.PetListingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val petListingRepository: PetListingRepository
) : ViewModel() {

    private val _favoriteListings = MutableStateFlow<List<PetListing>>(emptyList())
    val favoriteListings: StateFlow<List<PetListing>> = _favoriteListings.asStateFlow()

    init {
        viewModelScope.launch {
            petListingRepository.getFavoriteListings().collect { listings ->
                _favoriteListings.value = listings
            }
        }
    }
}