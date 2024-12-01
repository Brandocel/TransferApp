package com.example.transferapp.ui.auth

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.transferapp.ui.Screen
import com.example.transferapp.viewmodel.AuthViewModel
@Composable
fun RegisterScreen(navController: NavController, authViewModel: AuthViewModel) {
    // Declaración de variables para capturar los valores de entrada
    var name by remember { mutableStateOf("") } // Este es para el nombre
    var email by remember { mutableStateOf("") } // Este es para el correo electrónico
    var password by remember { mutableStateOf("") } // Este es para la contraseña
    var confirmPassword by remember { mutableStateOf("") } // Este es para confirmar la contraseña

    // Valor predeterminado para roleId
    val roleId = "55AEBAC4-A30C-4E1E-86CF-48D02455A22B"

    // Estado del registro
    val registerState by authViewModel.loginState.collectAsState()

    // Contexto para mostrar mensajes al usuario
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Campo para el nombre
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Campo para el correo electrónico
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Campo para la contraseña
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Campo para confirmar la contraseña
        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Botón para registrar
        Button(
            onClick = {
                when {
                    name.isBlank() -> {
                        Toast.makeText(context, "Por favor, ingrese su nombre", Toast.LENGTH_SHORT).show()
                    }
                    email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                        Toast.makeText(context, "Por favor, ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show()
                    }
                    password.isBlank() -> {
                        Toast.makeText(context, "Por favor, ingrese una contraseña", Toast.LENGTH_SHORT).show()
                    }
                    password != confirmPassword -> {
                        Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        // Llamada al ViewModel para registrar
                        authViewModel.register(name, email, password, roleId)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }

    // Manejo del estado del registro
    LaunchedEffect(registerState) {
        registerState?.let { state ->
            if (state == "User registered successfully.") {
                Toast.makeText(context, state, Toast.LENGTH_SHORT).show()
                navController.navigate(Screen.Login.route)
            } else if (state.startsWith("Error")) {
                Toast.makeText(context, state, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
