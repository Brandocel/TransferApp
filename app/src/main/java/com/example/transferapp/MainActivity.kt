package com.example.transferapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.transferapp.data.api.ApiService
import com.example.transferapp.data.local.SessionManager
import com.example.transferapp.repository.AuthRepository
import com.example.transferapp.repository.HomeRepository
import com.example.transferapp.repository.SeatSelectionRepository
import com.example.transferapp.ui.Screen
import com.example.transferapp.ui.selection.SeatSelectionScreen
import com.example.transferapp.ui.auth.LoginScreen
import com.example.transferapp.ui.auth.RegisterScreen
import com.example.transferapp.ui.home.HomeScreen
import com.example.transferapp.ui.theme.TransferAppTheme
import com.example.transferapp.viewmodel.AuthViewModel
import com.example.transferapp.viewmodel.AuthViewModelFactory
import com.example.transferapp.viewmodel.HomeViewModel
import com.example.transferapp.viewmodel.HomeViewModelFactory
import com.example.transferapp.viewmodel.SeatSelectionViewModel
import com.example.transferapp.viewmodel.SeatSelectionViewModelFactory
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = this
            val navController = rememberNavController()

            // Instancias necesarias
            val apiService = ApiService.create()
            val authRepository = AuthRepository(apiService)
            val homeRepository = HomeRepository(apiService)
            val sessionManager = SessionManager(context)
            val seatSelectionRepository = SeatSelectionRepository(apiService)

            val authViewModel: AuthViewModel = viewModel(
                factory = AuthViewModelFactory(authRepository, sessionManager)
            )
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(homeRepository)
            )

            val seatSelectionViewModel: SeatSelectionViewModel = viewModel(
                factory = SeatSelectionViewModelFactory(seatSelectionRepository)
            )

            // Determina la ruta inicial basado en el estado del token
            val startDestination = runBlocking {
                val token = sessionManager.authToken.first()
                if (!token.isNullOrEmpty()) Screen.Home.route else Screen.Login.route
            }

            // Configuración de la UI
            TransferAppTheme {
                NavHost(navController = navController, startDestination = startDestination) {
                    composable(Screen.Login.route) {
                        LoginScreen(navController, authViewModel)
                    }
                    composable(Screen.Register.route) {
                        RegisterScreen(navController, authViewModel)
                    }
                    composable(Screen.Home.route) {
                        HomeScreen(navController, homeViewModel)
                    }
                    composable(
                        route = Screen.SeatSelection.route,
                        arguments = listOf(
                            navArgument("unitId") { type = NavType.StringType },
                            navArgument("pickupTime") { type = NavType.StringType },
                            navArgument("reservationDate") { type = NavType.StringType },
                            navArgument("hotelId") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val unitId = backStackEntry.arguments?.getString("unitId")!!
                        val pickupTime = backStackEntry.arguments?.getString("pickupTime")!!
                        val reservationDate = backStackEntry.arguments?.getString("reservationDate")!!
                        val hotelId = backStackEntry.arguments?.getString("hotelId")!!

                        seatSelectionViewModel.fetchSeatStatus(unitId, pickupTime, reservationDate, hotelId)

                        SeatSelectionScreen(navController, seatSelectionViewModel)
                    }
                }
            }
        }
    }
}