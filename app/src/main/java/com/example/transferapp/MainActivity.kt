package com.example.transferapp

import android.content.Context
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
            val sessionManager = SessionManager(context) // Inicialización de SessionManager
            val authRepository = AuthRepository(apiService)
            val homeRepository = HomeRepository(apiService, sessionManager) // Pasa SessionManager
            val seatSelectionRepository = SeatSelectionRepository(apiService)

            // ViewModel para autenticación
            val authViewModelFactory = AuthViewModelFactory(authRepository, sessionManager)
            val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)

            // ViewModel para Home
            val homeViewModelFactory = HomeViewModelFactory(homeRepository, sessionManager)
            val homeViewModel: HomeViewModel = viewModel(factory = homeViewModelFactory)

            // ViewModel para selección de asientos
            val seatSelectionViewModelFactory = SeatSelectionViewModelFactory(
                repository = seatSelectionRepository,
                apiService = apiService
            )
            val seatSelectionViewModel: SeatSelectionViewModel = viewModel(factory = seatSelectionViewModelFactory)

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
                            navArgument("storeId") { type = NavType.StringType },
                            navArgument("folio") { type = NavType.StringType }
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
                        val folio = backStackEntry.arguments?.getString("folio")!!

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
                            userId = userId, // Pasa aquí el userId del token
                            folio = folio
                        )
                    }
                }
            }
        }
    }
}

