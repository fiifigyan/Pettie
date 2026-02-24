package com.pettie.ui.main.tabs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pettie.data.model.PetListing
import com.pettie.data.repository.PetListingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val petListingRepository: PetListingRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<PetListing>>(emptyList())
    val searchResults: StateFlow<List<PetListing>> = _searchResults.asStateFlow()

    init {
        searchQuery
            .debounce(500)
            .onEach { query ->
                if (query.isNotBlank()) {
                    petListingRepository.searchListings(query).collect {
                        _searchResults.value = it
                    }
                } else {
                    _searchResults.value = emptyList()
                }
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}