package com.pettie.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pettie.ui.auth.ForgotPasswordScreen
import com.pettie.ui.auth.LoginScreen
import com.pettie.ui.auth.RegisterScreen
import com.pettie.ui.main.MainTabsScreen
import com.pettie.ui.main.detail.PetDetailScreen

@Composable
fun PettieNavHost(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState(initial = null)

    LaunchedEffect(authState) {
        if (authState != null) {
            navController.navigate(NavRoutes.MAIN_TABS) {
                popUpTo(NavRoutes.LOGIN) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = NavRoutes.LOGIN
    ) {
        composable(NavRoutes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(NavRoutes.REGISTER) },
                onNavigateToForgotPassword = { navController.navigate(NavRoutes.FORGOT_PASSWORD) },
                onLoginSuccess = {
                    navController.navigate(NavRoutes.MAIN_TABS) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.REGISTER) {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(NavRoutes.MAIN_TABS) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() },
                onResetSent = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.MAIN_TABS) {
            MainTabsScreen(
                onSignOut = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.MAIN_TABS) { inclusive = true }
                    }
                },
                onNavigateToPetDetail = { petId ->
                    navController.navigate("pet_detail/$petId")
                }
            )
        }

        composable(
            route = NavRoutes.PET_DETAIL,
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) {
            PetDetailScreen()
        }
    }
}
