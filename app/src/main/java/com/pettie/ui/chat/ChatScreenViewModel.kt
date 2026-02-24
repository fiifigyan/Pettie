package com.pettie.ui.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pettie.data.model.Message
import com.pettie.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatScreenViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val chatId: String = savedStateHandle.get<String>("chatId")!!

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    init {
        viewModelScope.launch {
            chatRepository.getMessages(chatId).collect { result ->
                result.onSuccess {
                    _messages.value = it
                }
            }
        }
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            chatRepository.sendMessage(chatId, text)
        }
    }
}