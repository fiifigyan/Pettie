package com.pettie.ui.main.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pettie.data.model.PetListing
import com.pettie.data.repository.ChatRepository
import com.pettie.data.repository.PetListingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PetDetailViewModel @Inject constructor(
    private val petListingRepository: PetListingRepository,
    private val chatRepository: ChatRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val petId: String = savedStateHandle.get<String>("petId")!!

    val petListing: StateFlow<Result<PetListing?>?> = petListingRepository.getListing(petId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    suspend fun getOrCreateChat(sellerId: String): String {
        return chatRepository.getOrCreateChat(sellerId).getOrThrow()
    }
}