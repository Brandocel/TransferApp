package com.example.transferapp.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.transferapp.data.api.ApiService
import com.example.transferapp.data.local.SessionManager
import com.example.transferapp.repository.AuthRepository
import com.example.transferapp.repository.HomeRepository
import com.example.transferapp.repository.SeatSelectionRepository
import com.example.transferapp.ui.auth.LoginScreen
import com.example.transferapp.ui.auth.RegisterScreen
import com.example.transferapp.ui.home.HomeScreen
import com.example.transferapp.ui.selection.SeatSelectionScreen
import com.example.transferapp.viewmodel.AuthViewModel
import com.example.transferapp.viewmodel.AuthViewModelFactory
import com.example.transferapp.viewmodel.HomeViewModel
import com.example.transferapp.viewmodel.HomeViewModelFactory
import com.example.transferapp.viewmodel.SeatSelectionViewModel
import com.example.transferapp.viewmodel.SeatSelectionViewModelFactory
import extractUserId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// Rutas de la app
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object SeatSelection : Screen("seat_selection/{unitId}/{pickupTime}/{reservationDate}/{hotelId}/{agencyId}/{client}/{adult}/{child}/{zoneId}/{storeId}") {
        fun createRoute(
            unitId: String,
            pickupTime: String,
            reservationDate: String,
            hotelId: String,
            agencyId: String,
            client: String,
            adult: Int,
            child: Int,
            zoneId: String,
            storeId: String
        ): String {
            val encodedClient = URLEncoder.encode(client, StandardCharsets.UTF_8.toString())
            return "seat_selection/$unitId/$pickupTime/$reservationDate/$hotelId/$agencyId/$encodedClient/$adult/$child/$zoneId/$storeId"
        }

    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    seatSelectionViewModel: SeatSelectionViewModel
) {
    // Obtenemos el token del `SessionManager` (suponiendo que se inyecta en algún contexto)
    val sessionManager = SessionManager(navController.context)
    val userId = runBlocking {
        val token = sessionManager.authToken.first()
        if (!token.isNullOrEmpty()) extractUserId(token) else ""
    }

    NavHost(navController = navController, startDestination = Screen.Login.route) {
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
                userId = userId // Pasamos el userId extraído del token
            )
        }
    }
}


