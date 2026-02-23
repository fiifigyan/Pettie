package com.pettie.ui.main.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pettie.ui.main.home.components.PetListingCard

@Composable
fun SearchTab(
    viewModel: SearchViewModel = hiltViewModel(),
    onNavigateToPetDetail: (String) -> Unit
) {
    val searchResults by viewModel.searchResults.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp)
    ) {
        TextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            placeholder = { Text("Search by breed, species, or location") },
            modifier = Modifier.fillMaxWidth()
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(top = 16.dp)
        ) {
            items(searchResults) { listing ->
                PetListingCard(
                    listing = listing,
                    onClick = { onNavigateToPetDetail(listing.id) }
                )
            }
        }
    }
}
