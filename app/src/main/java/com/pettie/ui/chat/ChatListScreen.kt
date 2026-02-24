package com.pettie.ui.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    onNavigateToChat: (String) -> Unit
) {
    val chats by viewModel.chats.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Chats") })
        }
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(it)) {
            items(chats) { chat ->
                ListItem(
                    headlineContent = { Text(chat.id) },
                    supportingContent = { Text(chat.lastMessage?.text ?: "") },
                    modifier = Modifier.clickable { onNavigateToChat(chat.id) }
                )
            }
        }
    }
}
