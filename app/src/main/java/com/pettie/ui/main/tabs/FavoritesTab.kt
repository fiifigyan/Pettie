package com.pettie.ui.main.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pettie.ui.main.home.components.PetListingCard

@Composable
fun FavoritesTab(
    viewModel: FavoritesViewModel = hiltViewModel(),
    onNavigateToPetDetail: (String) -> Unit
) {
    val favoriteListings by viewModel.favoriteListings.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp)
    ) {
        if (favoriteListings.isEmpty()) {
            Text("You have no favorite listings yet.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(favoriteListings) { listing ->
                    PetListingCard(
                        listing = listing,
                        onClick = { onNavigateToPetDetail(listing.id) }
                    )
                }
            }
        }
    }
}
