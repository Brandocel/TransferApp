package com.example.transferapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.transferapp.data.api.ApiService
import com.example.transferapp.repository.SeatSelectionRepository
import com.example.transferapp.ui.auth.LoginScreen
import com.example.transferapp.ui.auth.RegisterScreen
import com.example.transferapp.ui.home.HomeScreen
import com.example.transferapp.ui.selection.SeatSelectionScreen
import com.example.transferapp.viewmodel.AuthViewModel
import com.example.transferapp.viewmodel.HomeViewModel
import com.example.transferapp.viewmodel.SeatSelectionViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// Rutas de la app
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object SeatSelection : Screen("seat_selection/{unitId}/{pickupTime}/{reservationDate}/{hotelId}/{agencyId}/{client}/{adult}/{child}") {
        fun createRoute(
            unitId: String,
            pickupTime: String,
            reservationDate: String,
            hotelId: String,
            agencyId: String,
            client: String,
            adult: Int,
            child: Int
        ): String {
            val encodedClient = URLEncoder.encode(client, StandardCharsets.UTF_8.toString())
            return "seat_selection/$unitId/$pickupTime/$reservationDate/$hotelId/$agencyId/$encodedClient/$adult/$child"
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
                navArgument("adult") { type = NavType.IntType },  // Cambiar a IntType
                navArgument("child") { type = NavType.IntType }   // Cambiar a IntType
            )
        ) { backStackEntry ->
            val unitId = backStackEntry.arguments?.getString("unitId")!!
            val pickupTime = backStackEntry.arguments?.getString("pickupTime")!!
            val reservationDate = backStackEntry.arguments?.getString("reservationDate")!!
            val hotelId = backStackEntry.arguments?.getString("hotelId")!!
            val agencyId = backStackEntry.arguments?.getString("agencyId")!!
            val client = backStackEntry.arguments?.getString("client")!!
            val adult = backStackEntry.arguments?.getInt("adult")!!  // Usar getInt
            val child = backStackEntry.arguments?.getInt("child")!!  // Usar getInt

            SeatSelectionScreen(
                navController = navController,
                viewModel = seatSelectionViewModel,
                agencyId = agencyId,
                client = client,
                adult = adult,
                child = child
            )
        }

    }

}

