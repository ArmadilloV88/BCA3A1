package bot.lobby.bot_lobby.view_models

import android.util.Log
import androidx.appcompat.app.AppCompatActivity

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bot.lobby.bot_lobby.models.FetchResponse
import bot.lobby.bot_lobby.models.Team
import bot.lobby.bot_lobby.models.User
import bot.lobby.bot_lobby.services.LoginService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import bot.lobby.bot_lobby.api.RetrofitInstance.UserApi
import bot.lobby.bot_lobby.api.RetrofitInstance.apiKey
import bot.lobby.bot_lobby.utils.BiometricAuthHelper

import bot.lobby.bot_lobby.models.Session
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn


object UserViewModel : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _searchError: MutableStateFlow<String?> = MutableStateFlow(null)
    val searchError: StateFlow<String?> = _searchError.asStateFlow()

    //    private val _searchedUsers: MutableStateFlow<List<User>?> = MutableStateFlow(null)
//    val searchedUsers: StateFlow<List<User>?> = _searchedUsers.asStateFlow()
    private val _refreshSearch: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val searchedUsers = combine(_searchQuery, _refreshSearch) { searchQuery, refreshSearch ->
        if (refreshSearch) {
            _refreshSearch.value = false
        }

        _isSearching.value = true

        // The following query was adapted from stackoverflow.com
        // Author: bgs (https://stackoverflow.com/users/2298058/bgs)
        // Link: https://stackoverflow.com/questions/17322228/check-if-a-column-contains-text-using-sql
        // Create a query string that will be used to search for all teams based on their ids
        val usernameQuery = "like.*${searchQuery}*"
        val isPublicQuery = "eq.true"


        // Fetch data
        val response: Any

        if (searchQuery.isNotEmpty()) {
            try {
                response = UserApi.getUsersByName(
                    apiKey,
                    username = usernameQuery,
                    isPublic = isPublicQuery
                )

                _isSearching.value = false
                response.body()?.toList()

            } catch (exception: Exception) {
                _searchError.value = exception.message.toString()
                Log.i("ERROR!", exception.message.toString())

                _isSearching.value = false
                emptyList()
            }
        } else {
            _isSearching.value = false
            emptyList()
        }

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun refreshSearch() {
        _refreshSearch.value = true
    }

    private val _userData: MutableStateFlow<List<User>> = MutableStateFlow(listOf())
    val userData: StateFlow<List<User>> = _userData


    suspend fun getTeamsUsers(team: Team): FetchResponse<List<User>> {
        var users: List<User> = listOf()
        var errorMessage: String? = null

//        viewModelScope.launch {
        try {
            // Create a query string that will be used to search for all users based on their ids
            var queryString = "in.("

            team.userIdsAndRoles?.forEach { item ->
                // The first item in the pair is the user id
                queryString += "${item.id}"

                queryString += if (team.userIdsAndRoles.indexOf(item) == (team.userIdsAndRoles.size - 1)) ")" else ","
            }


            val response =
                UserApi.getUsers(apiKey, queryString)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    users = body

                    Log.i("DATA!", body.toString())
                } else {
                    Log.e("ERROR!", "Response body is null")
                }
            } else {
                Log.e("ERROR", "Failed to fetch users: ${response.errorBody()?.string()}")
            }
        } catch (exception: Exception) {
            errorMessage = exception.message.toString()
            Log.e("ERROR!", exception.message.toString())
        }
//        }

        return FetchResponse(users, errorMessage)
    }

    //Register User Biometrics
    fun registerBiometricsForUser(
        user: User,
        isBiometricEnabled: Boolean,
        activity: AppCompatActivity,
        callback: (User?) -> Unit
    ) {
        if (isBiometricEnabled) {
            viewModelScope.launch {
                val updatedUser = BiometricAuthHelper.registerBiometricData(user, activity)
                if (updatedUser.biometrics != null) {
                    updateUser(updatedUser) // Update the user in your database
                    callback(updatedUser)  // Pass updated user with biometrics registered back via callback
                } else {
                    Log.e("UserViewModel", "Biometric registration failed.")
                    callback(null)
                }
            }
        } else {
            Log.i("UserViewModel", "Biometric registration is disabled for user: ${user.username}")
            callback(user)
        }
    }

//    fun getUsersByName(apiKey: String, username: String): Response<List<User>> {
//        return UserApi.getUsers("username=eq.$username", apiKey)
//    }

    // Function to handle biometric login and fetch the user
    fun loginWithBiometrics(
        username: String,
        activity: AppCompatActivity,
        context: Context,
        callback: (User?) -> Unit
    ) {
        viewModelScope.launch {
            Log.d("UserViewModel", "Attempting to login with biometrics for user: $username")

            try {
                // Log API Key for debugging purposes
                Log.d("UserViewModel", "API Key: $apiKey")

                // Use the Retrofit instance to fetch user data with API key in headers
                val response = UserApi.getUsersByUsername(apiKey, "eq.$username")

                Log.d("UserViewModel", "API Response Status: ${response.code()}")

                if (response.isSuccessful && response.body() != null && response.body()!!
                        .isNotEmpty()
                ) {
                    val user =
                        response.body()!![0] // Assuming the username is unique and you get a single user

                    // Check if biometrics are enabled
                    if (user.isBiometricEnabled) {
                        // Authenticate using biometrics
                        val isAuthenticated = BiometricAuthHelper.authenticate(activity).await()

                        if (isAuthenticated) {
                            val sessionViewModel = SessionViewModel(context)

                            val announcementViewModel = AnnouncementViewModel(context)


                            // save user to state
//                AuthViewModel.updateUsersDetails(user)

                            // get and save users teams
                            var usersTeams = emptyList<Team>()
                            val response = TeamViewModel.getUsersTeams(user)

                            if (response.errors.isNullOrEmpty()) {
                                usersTeams = response.data!!
                            }

                            val newSession = Session(
                                userLoggedIn = user,
                                usersTeams = usersTeams
                            )

                            sessionViewModel.upsertSession(newSession)

                            user.teamIds?.forEach {
                                announcementViewModel.subscribeToTeamAnnouncements(it)
                            }

                            callback(user) // Successful login
                        } else {
                            Log.e("UserViewModel", "Biometric authentication failed")
                            callback(null) // Authentication failed
                        }
                    } else {
                        Log.e("UserViewModel", "Biometrics not enabled for user")
                        callback(null) // Biometrics not enabled
                    }
                } else {
                    Log.e(
                        "UserViewModel",
                        "User not found or API request failed. Response: ${
                            response.errorBody()?.string()
                        }"
                    )
                    callback(null) // User retrieval failed
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Exception occurred during login: ${e.message}")
                callback(null) // Handle exceptions gracefully
            }
        }
    }

    suspend fun getOnlineProfile(userId: Int): FetchResponse<User?> {
        var user: User? = null
        var errorMessage: String? = null

//        viewModelScope.launch {
        try {
            val response = UserApi.getUser(
                apiKey,
                "eq.${userId}",
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    user = body.first()

                    Log.i("DATA!", body.toString())
                } else {
                    Log.e("ERROR!", "Response body is null")
                }
            } else {
                Log.e(
                    "ONLINE PROFILE ERROR",
                    "Failed to fetch users: ${response.errorBody()?.string()}"
                )
            }
        } catch (exception: Exception) {
            errorMessage = exception.message.toString()
            Log.e("ERROR!", exception.message.toString())
        }
//        }

        return FetchResponse(user, errorMessage)
    }


    // Function to update an existing user
    fun updateUser(updatedUser: User) {

        viewModelScope.launch {
            try {
                val res = getOnlineProfile(updatedUser.id!!)

                if(res.errors.isNullOrEmpty() && res.data != null){
                    updatedUser.isBiometricEnabled = res.data.isBiometricEnabled

                    val response = UserApi.updateUser(
                        apiKey,
                        "eq.${updatedUser.id}",
                        updatedUser
                    )
                    if (response.isSuccessful) {
                        Log.i("SUCCESS", "User updated: ${response.body()}")
//                    AuthViewModel.updateUsersDetails(updatedUser)
                    } else {
                        Log.e(
                            "UPDATE USER ERROR",
                            "User update failed: ${response.errorBody()?.string()}"
                        )
                    }
                }
            } catch (exception: Exception) {
                Log.e("ERROR!", exception.message.toString())
            }
        }

        refreshSearch()
    }

    // Function to delete a user
    fun deleteUser(userId: Int, user: User? = null, callback: (User?) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = UserApi.deleteUser(
                    apiKey,
                    "eq.${userId}"
                )
                if (response.isSuccessful) {
                    Log.i("SUCCESS", "User deleted")
                    callback(user)
                } else {
                    Log.e("ERROR", "User deletion failed: ${response.errorBody()?.string()}")
                }
            } catch (exception: Exception) {
                Log.e("ERROR!", exception.message.toString())
            }
        }

        refreshSearch()
    }

    fun clearSearchQuery() {
        _searchQuery.value = ""
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Create a new user
    fun createUser(newUser: User, callback: (User?) -> Unit) {
        Log.i("USER TO CREATE", newUser.toString())
        viewModelScope.launch {
            try {
                val response = UserApi.createUser(apiKey, newUser)

                if (response.isSuccessful && response.body() != null) {
                    Log.d("UserViewModel", "User registered successfully: $newUser")
                    callback(newUser)
                } else {
                    Log.e("ERROR", "User creation failed: ${response.errorBody()?.string()}")
                    callback(null)
                }
            } catch (exception: Exception) {
                Log.e("ERROR!", exception.message.toString())
                callback(null)
            }
        }
    }

    // Login user
    fun loginUser(username: String, password: String, context: Context, callback: (User?) -> Unit) {
        viewModelScope.launch {
            val response = LoginService(UserApi).login(username, password)

            if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                val user = response.body()!![0] // Get the first user from the list

                val sessionViewModel = SessionViewModel(context)
                val announcementViewModel = AnnouncementViewModel(context)

                // save user to state
//                AuthViewModel.updateUsersDetails(user)

                // get and save users teams
                var usersTeams = emptyList<Team>()
                val response = TeamViewModel.getUsersTeams(user)

                if (response.errors.isNullOrEmpty()) {
                    usersTeams = response.data!!
                }

                val newSession = Session(
                    userLoggedIn = user,
                    usersTeams = usersTeams
                )

                sessionViewModel.upsertSession(newSession)

                user.teamIds?.forEach {
                    announcementViewModel.subscribeToTeamAnnouncements(it)
                }

                Log.d("USER LOGGED IN AS", sessionViewModel.session.value?.userLoggedIn.toString())

                callback(user) // Return the user via the callback
            } else {
                Log.e("UserViewModel", "Login failed: ${response.errorBody()?.string()}")
                callback(null) // Return null in case of failure
            }
        }
    }

    // Fetch all users
    fun getUserData() {
        viewModelScope.launch {
            try {
                val response = UserApi.getUsers(apiKey)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _userData.value = it
                        Log.d("UserViewModel", "Fetched users: $it")
                    } ?: Log.e("ERROR!", "Response body is null")
                } else {
                    Log.e("ERROR", "Failed to fetch users: ${response.errorBody()?.string()}")
                }
            } catch (exception: Exception) {
                Log.e("ERROR!", exception.message.toString())
            }
        }
    }

    fun clearData() {
        _searchQuery.value = ""

        _isSearching.value = false

        _searchError.value = null

//        _searchedUsers.value = null

        _userData.value = emptyList()
    }
}