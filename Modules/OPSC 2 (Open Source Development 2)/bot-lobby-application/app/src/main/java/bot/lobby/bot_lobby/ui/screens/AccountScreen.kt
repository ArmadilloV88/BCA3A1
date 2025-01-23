    package bot.lobby.bot_lobby.ui.screens

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bot.lobby.bot_lobby.MainActivity.Companion.loginService
import bot.lobby.bot_lobby.forms.SignUpForm
import bot.lobby.bot_lobby.models.User
import bot.lobby.bot_lobby.ui.theme.BlueStandard
import bot.lobby.bot_lobby.utils.onFormValueChange
import bot.lobby.bot_lobby.view_models.UserViewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import bot.lobby.bot_lobby.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

    enum class Mode {
    SignUp, ForgotPassword
}

data class AccountScreen(
    val mode: Mode
) : Screen {
    override val key = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val context = LocalContext.current as AppCompatActivity
        val navigator = LocalNavigator.currentOrThrow
        val form = SignUpForm()
//        val userViewModel = UserViewModel()
//        val auth = remember { FirebaseAuth.getInstance() }

        var isBiometricEnabled by remember { mutableStateOf(false) }
        var actionWasSuccessful by remember { mutableStateOf(true) }
        val coroutineScope = rememberCoroutineScope()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = if (mode == Mode.SignUp) R.string.register else R.string.reset_password),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.arrow_back)
                            )
                        }
                    }
                )
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 25.dp)
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_bot_lobby_logo),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                    )

                    Text(
                        text = "Bot Lobby",
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )

                    OutlinedTextField(
                        value = form.username.state.value ?: "",
                        onValueChange = { value -> onFormValueChange(value, form, form.username) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.username_placeholder)) },
                        isError = form.username.hasError(),
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = stringResource(R.string.username_icon)
                            )
                        },
                        singleLine = true
                    )

                    var passwordVisible by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        value = form.password.state.value ?: "",
                        onValueChange = { value -> onFormValueChange(value, form, form.password) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(if (mode == Mode.SignUp) R.string.password_placeholder_signup else R.string.password_placeholder_reset)) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = stringResource(R.string.password_icon)
                            )
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image =
                                if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    image,
                                    contentDescription = if (passwordVisible) stringResource(R.string.hide_password) else stringResource(R.string.show_password)
                                )
                            }
                        },
                        singleLine = true,
                        isError = form.password.hasError()
                    )

                    OutlinedTextField(
                        value = form.passwordConfirmation.state.value ?: "",
                        onValueChange = { value ->
                            onFormValueChange(
                                value,
                                form,
                                form.passwordConfirmation
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.confirm_password_placeholder)) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = stringResource(R.string.confirm_password_icon)
                            )
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image =
                                if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    image,
                                    contentDescription = if (passwordVisible) stringResource(R.string.hide_password) else stringResource(R.string.show_password)
                                )
                            }
                        },
                        singleLine = true,
                        isError = form.passwordConfirmation.hasError()
                    )

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(stringResource(R.string.register_with_biometrics))
                        Switch(
                            checked = isBiometricEnabled,
                            onCheckedChange = { isBiometricEnabled = it }
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (!actionWasSuccessful) {
                            Text(
                                stringResource(R.string.error_something_wrong),
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                        Button(
                            onClick = {
                                form.validate(true)

                                if (form.isValid) {
                                    val username = form.username.state.value!!
                                    val password = form.password.state.value!!


                                    if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
                                        Toast.makeText(
                                            context,
                                            R.string.error_invalid_login2,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    val newUser = User(
                                        username = username,
                                        password = password,
                                        isBiometricEnabled = isBiometricEnabled
                                    )

                                    UserViewModel.createUser(
                                        User(
                                            username = form.username.state.value!!,
                                            password = form.password.state.value!!
                                        )
                                    ) {
                                        UserViewModel.loginUser(
                                            username = form.username.state.value!!,
                                            password = form.password.state.value!!,
                                            context
                                        ) { user ->
                                            if (user == null) {
                                                Toast.makeText(
                                                    context,
                                                    R.string.error_invalid_login,
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show() // Toast message to indicate the process
                                            } else {
                                                navigator.push(LandingScreen())
                                            }
                                        }
                                    }

                                    // User registration with callback
                                    UserViewModel.createUser(newUser) { createdUser ->
                                        if (createdUser != null) {
                                            // Successfully created user, now log in
                                            coroutineScope.launch {
                                                try {
                                                    // Log the user in after registration
                                                    val loginResult = withContext(Dispatchers.IO) {
                                                        val usernameNonNull = createdUser.username
                                                        val passwordNonNull = createdUser.password
                                                            ?: throw IllegalArgumentException(R.string.password_null.toString())

                                                        loginService.login(
                                                            usernameNonNull,
                                                            passwordNonNull
                                                        )
                                                    }

                                                    if (loginResult.isSuccessful) {
                                                        withContext(Dispatchers.Main) {
                                                            Toast.makeText(
                                                                context,
                                                                R.string.login_success,
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                            navigator.push(LandingScreen())
                                                        }
                                                    } else {
                                                        withContext(Dispatchers.Main) {
                                                            Toast.makeText(
                                                                context,
                                                                R.string.login_failed,
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }
                                                } catch (loginException: Exception) {
                                                    withContext(Dispatchers.Main) {
                                                        Toast.makeText(
                                                            context,
                                                            R.string.login_failed,
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                            }
                                        } else {
                                            // Handle registration failure
                                            Toast.makeText(
                                                context,
                                                R.string.registration_failed,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                    }
                                } else {
                                    // Handle forgot password
                                    Toast.makeText(
                                        context,
                                        R.string.password_reset_unimplemented,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = BlueStandard)
                        ) {
                            Text(text = stringResource(if (mode == Mode.SignUp) R.string.register else R.string.reset_password))
                        }
                        if (mode == Mode.SignUp) {
                            TextButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { navigator.pop() }
                            ) {
                                Text(
                                    buildAnnotatedString {
                                        append(stringResource(R.string.already_have_account))
                                        withStyle(style = SpanStyle(color = BlueStandard)) {
                                            append(stringResource(R.string.sign_in_here))
                                        }
                                    },
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}