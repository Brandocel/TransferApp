package com.example.transferapp.data.api

import com.example.transferapp.data.model.AvailabilityResponse
import com.example.transferapp.data.model.HomeResponse
import com.example.transferapp.data.model.MultipleReservationsRequest
import com.example.transferapp.data.model.ReservationResponse
import com.example.transferapp.data.model.SeatStatusResponse
import com.example.transferapp.ui.home.components.Reservation
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("api/Auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse


    @POST("api/Auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @GET("api/Home/get-all-info")
    suspend fun getAllInfo(): HomeResponse

    @GET("api/home/unit-availability")
    suspend fun getUnitAvailability(
        @Query("unitId") unitId: String,
        @Query("pickupTime") pickupTime: String,
        @Query("reservationDate") reservationDate: String,
        @Query("hotelId") hotelId: String
    ): AvailabilityResponse

    @GET("api/Reservation/seat-status")
    suspend fun getSeatStatus(
        @Query("unitId") unitId: String,
        @Query("pickupTime") pickupTime: String,
        @Query("reservationDate") reservationDate: String,
        @Query("hotelId") hotelId: String
    ): SeatStatusResponse

    @POST("api/Reservation/add-multiple-reservations")
    suspend fun addMultipleReservations(
        @Body request: MultipleReservationsRequest
    ): ReservationResponse

    @GET("api/Home/user-reservations/{userId}")
    suspend fun getUserReservations(@Path("userId") userId: String): ApiResponse<List<Reservation>>



    companion object {
        private const val BASE_URL = "https://f269-189-174-200-94.ngrok-free.app/"

        //Local
       // private const val BASE_URL = "https://localhost:7130/"

        fun create(): ApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }

}

data class ApiResponse<T>(
    val success: Boolean, // Indica si la operación fue exitosa
    val message: String,  // Mensaje descriptivo de la operación
    val data: T?          // Los datos reales (pueden ser de cualquier tipo)
)

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String, val user: User)
data class RegisterRequest(val email: String, val password: String, val name: String, val roleId: String)
data class RegisterResponse(val success: Boolean, val message: String)
data class User(val id: String, val name: String, val email: String)
