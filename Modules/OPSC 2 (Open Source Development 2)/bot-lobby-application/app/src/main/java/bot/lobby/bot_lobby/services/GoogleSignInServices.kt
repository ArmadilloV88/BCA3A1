package bot.lobby.bot_lobby.services

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Base64
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import bot.lobby.bot_lobby.models.User
import bot.lobby.bot_lobby.view_models.SupabaseAuthViewModel
import bot.lobby.bot_lobby.view_models.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.navigator.Navigator
import bot.lobby.bot_lobby.api.RetrofitInstance
import bot.lobby.bot_lobby.ui.screens.LandingScreen
import bot.lobby.bot_lobby.R
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.GlobalScope
import org.json.JSONObject


@Composable
fun GoogleSignInButton(
    registerService: RegisterService,
    loginService: LoginService,
    viewModel: SupabaseAuthViewModel = viewModel(),
    userModel: UserViewModel = viewModel(),
    isReg: Boolean, // Determines if it's a registration or login,
    navigator: Navigator,
    enabled: Boolean = true
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = context as? Activity ?: run {
        Log.e("GoogleSignInButton", "Context is not an activity")
        return
    }

    // Configure Google Sign-In
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("812262640049-f75l5h36lguq8d2009g3ca2u3hkhmps7.apps.googleusercontent.com") // Replace with your actual client ID
        .requestEmail()
        .build()

    val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)

    // Register the activity launcher for Google sign-in intent
    val signInLauncher: ActivityResultLauncher<Intent> = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data ->
                handleSignInResult(
                    data,
                    registerService,
                    loginService,
                    userModel,
                    isReg,
                    coroutineScope,
                    context,
                    navigator,
                    activity
                )
            } ?: Log.e("GoogleSignInButton", "Sign-in canceled or failed: No data returned.")
        } else {
            Log.e(
                "GoogleSignInButton",
                "Sign-in intent failed with result code ${result.resultCode}"
            )
        }
    }

    Column {
        Button(
            onClick = {
                Log.d("GoogleSignInButton", "Button clicked for Google Sign-In")
                try {
                    val signInIntent = googleSignInClient.signInIntent
                    Log.d("GoogleSignInButton", "Launching sign-in intent: $signInIntent")
                    signInLauncher.launch(signInIntent)
                    Log.d("GoogleSignInButton", "Sign-in launcher launched")
                } catch (e: Exception) {
                    Log.e("GoogleSignInButton", "Error launching sign-in intent: ${e.message}")
                }
            }, enabled = enabled
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_google_logo),
                contentDescription = "Google Icon",
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))

            Text(if (isReg) "Register with Google" else "Sign in with Google")
        }

        // Define the button's click logic
//        val onClick: () -> Unit = {
//            // Launch Google Sign-In Intent
//            val signInIntent = googleSignInClient.signInIntent
//            Log.d("GoogleSignInButton", "Launching Google Sign-In Intent")
//
//            // Provide feedback to the user
//            Toast.makeText(context, "Starting Google Sign-In...", Toast.LENGTH_SHORT)
//                .show() // Toast message to indicate the process
//
//            signInLauncher.launch(signInIntent) // Launch the sign-in intent using the launcher
//        }
//
//
//        // UI for the button
//        Column {
//            Button(onClick = onClick, enabled = enabled) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_google_logo),
//                    contentDescription = "Google Icon",
//                    modifier = Modifier.size(16.dp)
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//
//                Text(if (isReg) "Register with Google" else "Sign in with Google")
//            }
//        }
    }
}

private fun handleSignInResult(
    data: Intent,
    registerService: RegisterService,
    loginService: LoginService,
    userviewmodel: UserViewModel,
    isReg: Boolean,
    coroutineScope: CoroutineScope,
    context: Context,
    navigator: Navigator,
    activity: Activity
) {
    val accountTask: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
    try {
        // Retrieve the signed-in account
        val account = accountTask.getResult(ApiException::class.java)
        Log.d("GoogleSignInButton", "Account retrieved successfully.")

        // Extract the email
        val userEmail = account.email ?: throw Exception("Email not found")
        Log.d("GoogleSignInButton", "User Email: $userEmail")

        // Create the new User object
        val newUser = User(
            id = null,
            role = 1,
            bio = null,
            username = userEmail,
            password = "password", // Consider using a better method to manage user passwords
            biometrics = null,
            teamIds = listOf(),
            isPublic = true,
            isLFT = true,
            email = userEmail,
            isBiometricEnabled = false
        )
        Log.d("GoogleSignInButton", "New User Object Created: $newUser")


        GlobalScope.launch {
            try {
                val queryString = "eq.$userEmail"

                // Wrap `getUsers` in a coroutine to handle suspension
                val res = RetrofitInstance.UserApi.getUsers(
                    RetrofitInstance.apiKey,
                    email = queryString
                )

                if (res.isSuccessful) {
                    if (res.body().isNullOrEmpty()) {
                        Log.d(
                            "GoogleSignInButton",
                            "User does not exist. Proceeding to register."
                        )
                        newUser.isBiometricEnabled = true

                        // Register the user within the same coroutine scope
                        userviewmodel.createUser(newUser) { registeredUser ->
                            if (registeredUser != null) {
                                // Proceed to login after successful registration
                                GlobalScope.launch {
                                    val loginResult = loginService.login(registeredUser.username, registeredUser.password ?: "")
                                    if (loginResult.isSuccessful) {
                                        Log.d(
                                            "GoogleSignInButton",
                                            "Login successful for newly registered user."
                                        )
                                        // Navigate to the next screen
                                        navigator.push(LandingScreen())
                                    } else {
                                        val errorMessage =
                                            loginResult.errorBody()?.string() ?: "Unknown error"
                                        Log.e(
                                            "GoogleSignInButton",
                                            "Login failed for newly registered user: $errorMessage"
                                        )
                                    }
                                }
                            } else {
                                Log.e(
                                    "GoogleSignInButton",
                                    "Registration failed or returned null user."
                                )
                            }
                        }
                    } else {
                        Log.d("GoogleSignInButton", "User exists. Proceeding to log in.")

                        // Proceed with login within coroutine
                        val loginResult = loginService.login(userEmail, "")
                        if (loginResult.isSuccessful) {
                            Log.d("GoogleSignInButton", "Login successful.")
                            // Navigate to the next screen
                            navigator.push(LandingScreen())
                        } else {
                            val errorMessage =
                                loginResult.errorBody()?.string() ?: "Unknown error"
                            Log.e("GoogleSignInButton", "Login failed: $errorMessage")
                        }

//         // Check if the user is registering or logging in
//         coroutineScope.launch {
//             // check if user exists. if they dont, register them, otherwise log them in
//             val queryString = "eq.$userEmail"

//             val res =
//                 RetrofitInstance.UserApi.getUsers(RetrofitInstance.apiKey, email = queryString)

//             if (res.isSuccessful) {
//                 // Register if nothing is found
//                 if (res.body().isNullOrEmpty()) {
//                     val registrationResult = registerService.register(newUser)
//                     Log.d("GoogleSignInButton", "Registration Result: $registrationResult")
//                 }

//                 val loginResult = loginService.login(newUser.username, "")
//                 Log.d("GoogleSignInButton", "Login Result: $loginResult")

//                 if (loginResult.isSuccessful) {
// //                    Log.d("LoginService", "Login successful!")
// ////
// //                    Log.d("LoginService 2", loginResult.body().toString())
// //////
// //////
// //////                    // save user to state
// //                    AuthViewModel.updateUsersDetails(loginResult.body()!![0] )
// //
// //                    // get and save users teams
// //                    val teamViewModel = TeamViewModel()
// //                    var usersTeams = emptyList<Team>()
// //                    val res = teamViewModel.getUsersTeams(loginResult.body()!![0] )
// //
// //                    if (res.errors.isNullOrEmpty()) {
// //                        usersTeams = res.data!!
// //                    }
// //
// //                    AuthViewModel.setUsersTeams(usersTeams)

//                     UserViewModel.loginUser(newUser.username, "password", context) { user ->
//                         Log.d("LoginService 2", user.toString())

                    }
                } else {
                    val errorMessage = res.errorBody()?.string() ?: "Unknown error"
                    Log.e("GoogleSignInButton", "Failed to check if user exists: $errorMessage")
                    Log.e(
                        "GoogleSignInButton",
                        "Check user existence failed with status code: ${res.code()}"
                    )
                }
            } catch (exception: Exception) {
                Log.e(
                    "GoogleSignInButton",
                    "Exception occurred: ${exception.message}",
                    exception
                )
            }
        }
    } catch (e: ApiException) {
        Log.e("GoogleSignInButton", "Sign-in failed: ${e.statusCode}, Message: ${e.message}")
    } catch (e: Exception) {
        Log.e("GoogleSignInButton", "Error: ${e.message}")
    }
}

// Function to decode the Google ID Token (if needed)
fun parseIdToken(idToken: String): JSONObject {
    val parts = idToken.split(".")
    if (parts.size != 3) throw IllegalArgumentException("Invalid ID token format.")
    val payload = parts[1] // The payload is the second part of the token
    val decodedBytes = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_WRAP)
    val json = String(decodedBytes)
    return JSONObject(json)
}
