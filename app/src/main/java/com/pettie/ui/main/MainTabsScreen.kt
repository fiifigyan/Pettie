package com.pettie.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import com.pettie.ui.main.tabs.FavoritesTab
import com.pettie.ui.main.tabs.HomeTab
import com.pettie.ui.main.tabs.ProfileTab
import com.pettie.ui.main.tabs.SearchTab
import com.pettie.ui.main.tabs.SellTab

@Composable
fun MainTabsScreen(
    onSignOut: () -> Unit = {},
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val tabs = listOf(
        TabItem("Home", Icons.Default.Home, "home"),
        TabItem("Search", Icons.Default.Search, "search"),
        TabItem("Sell", Icons.Outlined.Add, "sell"),
        TabItem("Favorites", Icons.Default.Favorite, "favorites"),
        TabItem("Profile", Icons.Default.Person, "profile")
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
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") { HomeTab() }
            composable("search") { SearchTab() }
            composable("sell") { SellTab() }
            composable("favorites") { FavoritesTab() }
            composable("profile") { ProfileTab(onSignOut = onSignOut) }
        }
    }
}

private data class TabItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)
