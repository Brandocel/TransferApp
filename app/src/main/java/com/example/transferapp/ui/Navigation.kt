package com.example.transferapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.transferapp.ui.auth.LoginScreen
import com.example.transferapp.ui.auth.RegisterScreen
import com.example.transferapp.ui.home.HomeScreen
import com.example.transferapp.viewmodel.AuthViewModel

// Rutas de la app
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
}

@Composable
fun AppNavigation(navController: NavHostController, authViewModel: AuthViewModel) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(navController, authViewModel)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController, authViewModel)
        }
        composable(Screen.Home.route) {
            HomeScreen()
        }
    }
}
