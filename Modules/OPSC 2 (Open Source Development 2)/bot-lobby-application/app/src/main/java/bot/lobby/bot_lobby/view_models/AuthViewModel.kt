package bot.lobby.bot_lobby.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bot.lobby.bot_lobby.api.RetrofitInstance
import bot.lobby.bot_lobby.api.RetrofitInstance.UserApi
import bot.lobby.bot_lobby.models.AuthRequest
import bot.lobby.bot_lobby.models.AuthResponse
import bot.lobby.bot_lobby.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

object AuthViewModel : ViewModel() {
//    private val _usersTeamsUsers: MutableStateFlow<MutableMap<UUID, List<User>>> =
//        MutableStateFlow(mutableMapOf())
//    val usersTeamsUsers: StateFlow<Map<UUID, List<User>>> = _usersTeamsUsers.asStateFlow()

    // StateFlow to observe the login/registration response
    private val _authState = MutableStateFlow<AuthResponse?>(null)
    val authState: StateFlow<AuthResponse?> = _authState.asStateFlow()

    // For login error state
    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()

    // Function to login
    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                val request = AuthRequest(email, password)
                val response = RetrofitInstance.AuthApi.login(
                    RetrofitInstance.apiKey,
                    request
                ) // Pass request directly to body

                if (response.isSuccessful) {
                    val authResponse = response.body()
                    _authState.value = authResponse  // Update auth state with the response
                    Log.i("SUCCESS", "Login successful: ${authResponse?.access_token}")
                } else {
                    _errorState.value = response.errorBody()?.string()  // Update error state
                    Log.e("ERROR", "Login failed: ${response.errorBody()?.string()}")
                }
            } catch (e: HttpException) {
                _errorState.value = "Network error"
                Log.e("ERROR", "HttpException during login: ${e.message}")
            } catch (e: IOException) {
                _errorState.value = "IO error"
                Log.e("ERROR", "IOException during login: ${e.message}")
            }
        }
    }

    // Function to register
    fun registerUser(
        user: User
    ) {
        viewModelScope.launch {
            try {
                val request = AuthRequest(user.username, user.password.toString())
                val response = RetrofitInstance.AuthApi.register(RetrofitInstance.apiKey, request)

                if (response.isSuccessful) {
                    val authResponse = response.body()
                    _authState.value = authResponse  // Update auth state with the response
                    Log.i("SUCCESS", "Registration successful: ${authResponse?.access_token}")

                    // Step to save user data in the users table
                    // No need to fetch userId, as it is auto-incremented
                    saveUserDataInTable(user)
                } else {
                    _errorState.value = response.errorBody()?.string()  // Update error state
                    Log.e("ERROR", "Registration failed: ${response.errorBody()?.string()}")
                }
            } catch (e: HttpException) {
                _errorState.value = "Network error"
                Log.e("ERROR", "HttpException during registration: ${e.message}")
            } catch (e: IOException) {
                _errorState.value = "IO error"
                Log.e("ERROR", "IOException during registration: ${e.message}")
            }
        }
    }


    private fun saveUserDataInTable(
        user: User
    ) {
        viewModelScope.launch {
            try {
                val response =
                    UserApi.createUser(RetrofitInstance.apiKey, user)
                if (response.isSuccessful) {
                    Log.i("SUCCESS", "User data saved successfully: ${response.body()}")
                } else {
                    Log.e("ERROR", "Failed to save user data: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("ERROR", "Exception during saving user data: ${e.message}")
            }
        }
    }

//    // Login user
//    fun loginUser(username: String, password: String) {
//        viewModelScope.launch {
//            val response = LoginService(UserApi).login(username, password)
//            if (response.isSuccessful && response.body() != null) {
//                Log.d("UserViewModel", "Login successful: ${response.body()}")
//            } else {
//                Log.e("UserViewModel", "Login failed: ${response.errorBody()?.string()}")
//            }
//        }
//    }
//
//    // Register user
//    fun registerUser(newUser: User) {
//        viewModelScope.launch {
//            val response = RegisterService(UserApi).register(newUser)
//            if (response.isSuccessful && response.body() != null) {
//                Log.d("UserViewModel", "Registration successful: ${response.body()}")
//            } else {
//                Log.e("UserViewModel", "Registration failed: ${response.errorBody()?.string()}")
//            }
//        }
//    }


    // To clear the auth state after successful login/registration
    fun clearAuthState() {
        _authState.value = null
        _errorState.value = null
    }



//    fun setUser(user: User){
//        _userLoggedIn.value = user
//    }



//    fun updateTeamsUsers(teamId: UUID, teamsUsers: List<User>) {
//        _usersTeamsUsers.value[teamId] = teamsUsers
//    }


}