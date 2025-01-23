package bot.lobby.bot_lobby.view_models

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bot.lobby.bot_lobby.api.SupabaseClient.client
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.gotrue
import kotlinx.coroutines.launch
import bot.lobby.bot_lobby.models.UserState
import bot.lobby.bot_lobby.utils.SharedPreferenceHelper

class SupabaseAuthViewModel : ViewModel() {
    private val _userState = mutableStateOf<UserState>(UserState.Loading)
    val userState: State<UserState> = _userState

    // Save the token to SharedPreferences
    private fun saveToken(context: Context) {
        viewModelScope.launch {
            val accessToken = client.gotrue.currentAccessTokenOrNull()
            val sharedPref = SharedPreferenceHelper(context)
            sharedPref.saveStringData("accessToken", accessToken)
        }
    }

    // Retrieve token from SharedPreferences
    private fun getToken(context: Context): String? {
        val sharedPref = SharedPreferenceHelper(context)
        return sharedPref.getStringData("accessToken")
    }

    // Logout the user and clear token
    fun logout(context: Context) {
        val sharedPref = SharedPreferenceHelper(context)
        viewModelScope.launch {
            try {
                _userState.value = UserState.Loading
                client.gotrue.logout()
                sharedPref.clearPreferences()
                _userState.value = UserState.Success("Logged out successfully!")
            } catch (e: Exception) {
                _userState.value = UserState.Error(e.message ?: "")
            }
        }
    }

    // Check the Google login status and handle sign-in or sign-up
    fun checkGoogleLoginStatus(context: Context, result: NativeSignInResult) {
        _userState.value = UserState.Loading
        when (result) {
            is NativeSignInResult.Success -> {
                saveToken(context)
                _userState.value = UserState.Success("Logged in via Google")
            }

            is NativeSignInResult.ClosedByUser -> {}
            is NativeSignInResult.Error -> {
                _userState.value = UserState.Error(result.message)
            }

            is NativeSignInResult.NetworkError -> {
                _userState.value = UserState.Error(result.message)
            }
        }
    }

    // Sign in or sign up with Supabase using email
    /*private fun signInOrSignUpWithSupabase(context: Context, email: String, password: String) {
        viewModelScope.launch {
            try {
                // Attempt to sign in
                val response = client.gotrue.login(email = email, password = password)
                if (response != null) {
                    saveToken(context)
                    _userState.value = UserState.Success("Successfully signed in!")
                }
            } catch (e: RestException) {
                // If sign-in fails, attempt sign-up
                try {
                    val signUpResponse = client.gotrue.signUp(email = email, password = password)
                    if (signUpResponse != null) {
                        saveToken(context)
                        _userState.value = UserState.Success("Successfully signed up!")
                    }
                } catch (signUpError: RestException) {
                    _userState.value = UserState.Error("Failed to sign up: ${signUpError.error}")
                }
            } catch (e: Exception) {
                _userState.value = UserState.Error("Unknown Error: ${e.message}")
            }
        }
    }*/


    // Check if the user is logged in based on token availability
    fun isUserLoggedIn(context: Context) {
        viewModelScope.launch {
            try {
                _userState.value = UserState.Loading
                val token = getToken(context)
                if (token.isNullOrEmpty()) {
                    _userState.value = UserState.Success("User is not logged in!")
                } else {
                    client.gotrue.retrieveUser(token)
                    client.gotrue.refreshCurrentSession()
                    saveToken(context)
                    _userState.value = UserState.Success("User is already logged in!")
                }
            } catch (e: RestException) {
                _userState.value = UserState.Error(e.error)
            }
        }
    }
}