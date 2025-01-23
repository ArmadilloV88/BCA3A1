package bot.lobby.bot_lobby.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import bot.lobby.bot_lobby.MainActivity
import bot.lobby.bot_lobby.MainActivity.Companion.connectivityObserver
import bot.lobby.bot_lobby.forms.LoginForm
import bot.lobby.bot_lobby.observers.ConnectivityObserver
import bot.lobby.bot_lobby.services.GoogleSignInButton
import bot.lobby.bot_lobby.ui.theme.BlueStandard
import bot.lobby.bot_lobby.utils.BiometricAuthHelper
import bot.lobby.bot_lobby.utils.onFormValueChange
import bot.lobby.bot_lobby.view_models.SessionViewModel
import bot.lobby.bot_lobby.view_models.UserViewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import bot.lobby.bot_lobby.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


//class LoginScreen : Screen, AppCompatActivity() {


class LoginScreen : Screen, AppCompatActivity() {
    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val sessionViewModel = viewModel { SessionViewModel(context) }
        val session by sessionViewModel.session.collectAsStateWithLifecycle()
        val navigator = LocalNavigator.currentOrThrow

        val connectivity by connectivityObserver.observe()
            .collectAsState(ConnectivityObserver.Status.Unavailable)
        val isOffline = connectivity != ConnectivityObserver.Status.Available


        var isDeterminingLogin by remember { mutableStateOf(true) }

        LaunchedEffect(true) {
            delay(300L)
            isDeterminingLogin = false
        }

        if (session != null && !isDeterminingLogin) {
            navigator.push(LandingScreen())
        }

//        val userViewModel = UserViewModel()

        val form = LoginForm() // Form that manages the login state

        var useBiometrics by remember { mutableStateOf(false) }
        val isBiometricAvailable = BiometricAuthHelper.isBiometricSupported(context)

        // Biometric prompt Setup
        val biometricPromptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(R.string.biometric_login.toString())
            .setSubtitle(R.string.biometric_cred.toString())
            .setNegativeButtonText(R.string.cancel.toString())
            .build()

        // Create an instance of BiometricPrompt
        val biometricPrompt = BiometricPrompt(
            context as FragmentActivity,
            ContextCompat.getMainExecutor(context),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    // Handle successful biometric login here
                    if (!form.username.state.value.isNullOrBlank()) {
                        val username = form.username.state.value ?: ""
                        Log.d("LoginScreen", "Attempting biometric login for user: $username")

                        UserViewModel.loginWithBiometrics(
                            username = username,
                            activity = context as AppCompatActivity, // Pass the current activity context
                            context = context
                        ) { user ->
                            // Handle navigation and user session on successful biometric login
                            if (user != null) {
                                Log.d(
                                    "LoginScreen",
                                    "Biometric login successful for user: $username"
                                )
                                navigator.push(LandingScreen()) // Ensure LandingScreen is defined appropriately
                                Toast.makeText(
                                    context,
                                    R.string.login_success,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Log.e("LoginScreen", "Biometric login failed for user: $username")
                                Toast.makeText(
                                    context,
                                    R.string.biometric_login_failed,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Log.w("LoginScreen", "Username input is blank or null.")
                    }
                }

                override fun onAuthenticationFailed() {
                    Toast.makeText(context, R.string.biometric_authentication_failed, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        )
        // Initialize areCredentialsValid state
        val areCredentialsValid by remember { mutableStateOf(true) }

        // Check if biometric login is enabled and available
        if (useBiometrics && isBiometricAvailable) {
            // Trigger the biometric prompt
            biometricPrompt.authenticate(biometricPromptInfo)
        }

        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 25.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top part with the logo and app name
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.sign_in_title),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Image(
                        painter = painterResource(id = R.drawable.ic_bot_lobby_logo),
                        contentDescription = stringResource(R.string.app_name),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                    )

                    Text(
                        text = "Bot Lobby",
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Google Sign-In Button
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight() // Ensure it takes enough height
                ) {
                    GoogleSignInButton(
                        registerService = MainActivity.registerService,
                        loginService = MainActivity.loginService,

                        isReg = false, // Registration,
                        navigator = navigator,
                        enabled = !isOffline
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Email and Password fields
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Username Field
                    OutlinedTextField(
                        value = form.username.state.value ?: "",
                        onValueChange = { value ->
                            onFormValueChange(value, form, form.username)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.username_placeholder_login)) },
                        isError = form.username.hasError(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = stringResource(R.string.username_icon)
                            )
                        },
                        singleLine = true,
                        visualTransformation = VisualTransformation.None,
                        trailingIcon = {
                            if (form.username.hasError()) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = stringResource(R.string.error_icon),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )

                    // Password Field
                    var passwordVisible by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        value = form.password.state.value ?: "",
                        onValueChange = { value ->
                            onFormValueChange(value, form, form.password)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.password_placeholder_login)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = stringResource(R.string.password_icon)
                            )
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image =
                                if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = image,
                                    contentDescription = if (passwordVisible) stringResource(R.string.hide_password) else stringResource(R.string.show_password)
                                )
                            }
                        },
                        singleLine = true,
                        isError = form.password.hasError()
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Toggle switch for biometric login
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    stringResource(R.string.login_with_biometric)
                    if (!form.username.state.value.isNullOrBlank()) {
                        Switch(
                            checked = useBiometrics,
                            onCheckedChange = { checked ->
                                if (isBiometricAvailable) {
                                    useBiometrics = checked
                                } else {
                                    Toast.makeText(
                                        context,
                                        R.string.login_with_biometric_unavailable,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )
                    } else {
                        Text(stringResource(R.string.username_required), color = Color.Gray)
                    }
                }

                // Login Button
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = BlueStandard),
                    onClick = {
                        if (useBiometrics) {
                            biometricPrompt.authenticate(biometricPromptInfo)
                        } else {
                            form.validate(true)
                            if (form.isValid) {

                                // Launch the coroutine in the correct scope
                                CoroutineScope(Dispatchers.Main).launch {
                                    UserViewModel.loginUser(
                                        username = form.username.state.value ?: "",
                                        password = form.password.state.value ?: "",
                                        context
                                    ) { user ->
                                        if (user != null) {
                                            navigator.push(LandingScreen())
                                            Toast.makeText(
                                                context,
                                                R.string.login_success,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                R.string.error_invalid_login,
                                                Toast.LENGTH_SHORT
                                            ).show()

//                                AuthViewModel.loginUser(
//                                    email = form.email.state.value!!,
//                                    password = form.password.state.value!!
//                                )

                                            runBlocking {
                                                launch {
                                                    UserViewModel.loginUser(
                                                        username = form.username.state.value!!,
                                                        password = form.password.state.value!!,
                                                        context
                                                    ) { user -> // Provide the callback here
                                                        Log.i("USER", user.toString())

                                                        // Handle successful user login
                                                        if (user != null) {
                                                            // Navigate to LandingScreen if login is successful
                                                            navigator.push(LandingScreen())

                                                            Toast.makeText(
                                                                context,
                                                                R.string.login_success,
                                                                Toast.LENGTH_SHORT
                                                            )
                                                                .show()  // Show a confirmation toast
                                                        } else {
                                                            Log.i(
                                                                "USER",
                                                                "Login failed or user is null"
                                                            )

                                                            Toast.makeText(
                                                                context,
                                                                R.string.error_invalid_login,
                                                                Toast.LENGTH_SHORT
                                                            )
                                                                .show()  // Show a confirmation toast
                                                        }

                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                ) {
                    Text(stringResource(R.string.login_title), color = Color.White)
                }

                // Forgot Password Button
                TextButton(
                    onClick = { navigator.push(AccountScreen(mode = Mode.ForgotPassword)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false /*!isOffline*/
                    // as the forgot password functionality is not yet added, there's not really a point in having users access the page
                ) {
                    Text(
                        stringResource(R.string.forgot_password),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Register Button for new users
                TextButton(
                    onClick = { navigator.push(AccountScreen(mode = Mode.SignUp)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isOffline
                ) {
                    Text(
                        stringResource(R.string.register),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Forgot Password Button
//                TextButton(
//                    onClick = { navigator.push(AccountScreen(mode = Mode.ForgotPassword)) },
//                    modifier = Modifier.fillMaxWidth(),
//                ) {
//                    Text("Forgot Password?", style = MaterialTheme.typography.bodyMedium)
//                }
//
//                // Register Button
//                TextButton(
//                    onClick = { navigator.push(AccountScreen(mode = Mode.SignUp)) },
//                    modifier = Modifier.fillMaxWidth(),
//                ) {
//                    Text("Register", style = MaterialTheme.typography.bodyMedium)
//
//                        },
//                        modifier = Modifier.fillMaxWidth(),
//                        colors = ButtonDefaults.buttonColors(containerColor = BlueStandard),// Set button background to BlueStandard
//                        enabled = !isOffline
//                    ) {
//                        Text("Login", color = Color.White) // White text for contrast
//                    }
//
//
//
//                }
            }
        }
    }
}
