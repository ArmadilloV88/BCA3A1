package com.example.bot_lobby.screens

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import bot.lobby.bot_lobby.services.LoginService
import bot.lobby.bot_lobby.services.RegisterService
import bot.lobby.bot_lobby.R
import bot.lobby.bot_lobby.models.User
import bot.lobby.bot_lobby.utils.BiometricAuthHelper
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    loginService: LoginService,
    registerService: RegisterService,
    activity: AppCompatActivity
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var enableBiometrics by remember { mutableStateOf(false) } // Toggle state
    var result by remember { mutableStateOf("") }
    var isLoginMode by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        if (!isLoginMode) { // Only show for Registration
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Enable Biometric Registration")
                Switch(
                    checked = enableBiometrics,
                    onCheckedChange = { enableBiometrics = it }
                )
            }
        }

        Button(onClick = {
            coroutineScope.launch {
                if (!isLoginMode) {
                    val newUser = User(username = username, password = password)
                    if (enableBiometrics) {
                        registerBiometricData(newUser, activity) // Pass activity here
                    }
                    val response = registerService.register(newUser)
                    result = if (response.isSuccessful) R.string.registration_successful.toString() else R.string.registration_failed.toString()
                }
            }
        }) {
            Text(text = if (isLoginMode) stringResource(id = R.string.login_title) else stringResource(
                id = R.string.register)
            )
        }
    }
}

fun registerBiometricData(user: User, activity: AppCompatActivity): User {
    val updatedUser = BiometricAuthHelper.registerBiometricData(user, activity)
    Log.d("BiometricAuthHelper", "Biometric data registered for user: ${updatedUser.username}")
    return updatedUser
}
