package com.example.transferapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
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
import extractUserId
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

            // Crear la factory para AuthViewModel
            val authViewModelFactory = AuthViewModelFactory(authRepository, sessionManager)
            val authViewModel: AuthViewModel = viewModel(
                factory = authViewModelFactory
            )

            // Crear la factory para HomeViewModel pasando solo homeRepository
            val homeViewModelFactory = HomeViewModelFactory(homeRepository)
            val homeViewModel: HomeViewModel = viewModel(
                factory = homeViewModelFactory
            )

            // Crear la factory para SeatSelectionViewModel
            val seatSelectionViewModelFactory = SeatSelectionViewModelFactory(
                repository = seatSelectionRepository,
                apiService = apiService
            )
            val seatSelectionViewModel: SeatSelectionViewModel = viewModel(
                factory = seatSelectionViewModelFactory
            )

            // Obtener el userId del token de sesión
            val userId = runBlocking {
                val token = sessionManager.authToken.first()
                if (!token.isNullOrEmpty()) extractUserId(token) else ""
            }

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
                        HomeScreen(navController, homeViewModel, userId)
                    }
                    composable(
                        route = Screen.SeatSelection.route,
                        arguments = listOf(
                            navArgument("unitId") { type = NavType.StringType },
                            navArgument("pickupTime") { type = NavType.StringType },
                            navArgument("reservationDate") { type = NavType.StringType },
                            navArgument("hotelId") { type = NavType.StringType },
                            navArgument("agencyId") { type = NavType.StringType },
                            navArgument("client") { type = NavType.StringType },
                            navArgument("adult") { type = NavType.IntType },
                            navArgument("child") { type = NavType.IntType },
                            navArgument("zoneId") { type = NavType.StringType },
                            navArgument("storeId") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val unitId = backStackEntry.arguments?.getString("unitId")!!
                        val pickupTime = backStackEntry.arguments?.getString("pickupTime")!!
                        val reservationDate = backStackEntry.arguments?.getString("reservationDate")!!
                        val hotelId = backStackEntry.arguments?.getString("hotelId")!!
                        val agencyId = backStackEntry.arguments?.getString("agencyId")!!
                        val client = backStackEntry.arguments?.getString("client")!!
                        val adult = backStackEntry.arguments?.getInt("adult")!!
                        val child = backStackEntry.arguments?.getInt("child")!!
                        val zoneId = backStackEntry.arguments?.getString("zoneId")!!
                        val storeId = backStackEntry.arguments?.getString("storeId")!!

                        SeatSelectionScreen(
                            navController = navController,
                            viewModel = seatSelectionViewModel,
                            agencyId = agencyId,
                            client = client,
                            adult = adult,
                            child = child,
                            zoneId = zoneId,
                            storeId = storeId,
                            unitId = unitId,
                            pickupTime = pickupTime,
                            reservationDate = reservationDate,
                            hotelId = hotelId,
                            userId = userId // Pasa aquí el userId del token
                        )
                    }
                }
            }
        }
    }
}
