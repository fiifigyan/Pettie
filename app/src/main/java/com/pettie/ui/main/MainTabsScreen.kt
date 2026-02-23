package com.pettie.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pettie.ui.chat.ChatListScreen
import com.pettie.ui.main.tabs.FavoritesTab
import com.pettie.ui.main.tabs.HomeTab
import com.pettie.ui.main.tabs.ProfileTab
import com.pettie.ui.main.tabs.SearchTab
import com.pettie.ui.main.tabs.SellTab
import com.pettie.ui.navigation.NavRoutes

@Composable
fun MainTabsScreen(
    onSignOut: () -> Unit = {},
    onNavigateToPetDetail: (String) -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToChat: (String) -> Unit,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val tabs = listOf(
        TabItem("Home", Icons.Default.Home, NavRoutes.HOME),
        TabItem("Search", Icons.Default.Search, NavRoutes.SEARCH),
        TabItem("Sell", Icons.Outlined.Add, NavRoutes.SELL),
        TabItem("Favorites", Icons.Default.Favorite, NavRoutes.FAVORITES),
        TabItem("Messages", Icons.Default.Email, NavRoutes.CHAT_LIST),
        TabItem("Profile", Icons.Default.Person, NavRoutes.PROFILE)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEach { tab ->
                    val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                        selected = selected,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(NavRoutes.HOME) { HomeTab(onNavigateToPetDetail = onNavigateToPetDetail) }
            composable(NavRoutes.SEARCH) { SearchTab(onNavigateToPetDetail = onNavigateToPetDetail) }
            composable(NavRoutes.SELL) { SellTab() }
            composable(NavRoutes.FAVORITES) { FavoritesTab(onNavigateToPetDetail = onNavigateToPetDetail) }
            composable(NavRoutes.CHAT_LIST) { ChatListScreen(onNavigateToChat = onNavigateToChat) }
            composable(NavRoutes.PROFILE) { ProfileTab(onSignOut = onSignOut, onNavigateToEditProfile = onNavigateToEditProfile) }
        }
    }
}

private data class TabItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)
