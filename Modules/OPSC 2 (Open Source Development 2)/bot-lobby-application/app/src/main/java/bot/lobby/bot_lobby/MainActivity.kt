package bot.lobby.bot_lobby

import android.annotation.SuppressLint
import android.os.Bundle

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.StrictMode
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.material.icons.filled.SignalWifiStatusbarConnectedNoInternet4
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon


import androidx.appcompat.app.AppCompatActivity

import androidx.compose.material3.Text
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cafe.adriel.voyager.transitions.SlideTransition
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.navigator.Navigator
import androidx.compose.material.icons.Icons
import androidx.compose.ui.text.style.TextAlign
import bot.lobby.bot_lobby.api.RetrofitInstance
import bot.lobby.bot_lobby.api.UserApi
import bot.lobby.bot_lobby.observers.ConnectivityObserver
import bot.lobby.bot_lobby.observers.NetworkConnectivityObserver
import bot.lobby.bot_lobby.services.LoginService
import bot.lobby.bot_lobby.services.RegisterService
import bot.lobby.bot_lobby.ui.screens.LoginScreen
import bot.lobby.bot_lobby.ui.theme.BotLobbyTheme
import bot.lobby.bot_lobby.view_models.SessionViewModel
import bot.lobby.bot_lobby.R


// class MainActivity : ComponentActivity(), AppCompatActivity() {
//     companion object {
//         val userApi: UserApi = RetrofitInstance.UserApi
//         // Declare loginService without instantiation here
//
class MainActivity :  /*ComponentActivity(),*/ AppCompatActivity() {
    companion object {
        val userApi: UserApi = RetrofitInstance.UserApi


        lateinit var loginService: LoginService
        lateinit var registerService: RegisterService


        lateinit var connectivityObserver: ConnectivityObserver

        const val REQUEST_NOTIFICATION_PERMISSION_CODE = 1001 // Declare constant here
    }

    @SuppressLint("StateFlowValueCalledInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        connectivityObserver = NetworkConnectivityObserver(applicationContext)
        enableEdgeToEdge()

        loginService = LoginService(userApi)
        registerService = RegisterService(userApi, this)

        // Check and request notification permission
        if (!isNotificationPermissionGranted()) {
            requestNotificationPermission()
        }

        
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        
        setContent {
            var showStudentNotice by remember { mutableStateOf(true) }

            val context = LocalContext.current
            val sessionViewModel = viewModel { SessionViewModel(context) }
          
            val connectivity by connectivityObserver.observe()
                .collectAsState(ConnectivityObserver.Status.Unavailable)
            var dismissedOfflineDialog by remember { mutableStateOf(false) }

//            val showOfflineDialog by remember {mutableStateOf(connectivity != ConnectivityObserver.Status.Available && !dismissedOfllineDialog)}

            val showOfflineDialog =
                connectivity != ConnectivityObserver.Status.Available && !dismissedOfflineDialog
          
            if (connectivity == ConnectivityObserver.Status.Available) {
                dismissedOfflineDialog = false
            }

//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);


            BotLobbyTheme {
                Navigator(
//                    if (auth.currentUser != null)
//                        LandingScreen()
//                    else
                    LoginScreen()
                ) {
                    SlideTransition(it)
                }
            }

            if (showOfflineDialog) {
                AlertDialog(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.SignalWifiStatusbarConnectedNoInternet4,
                            contentDescription = "Offline Mode"
                        )
                    },
                    title = {
                        Text(
                            text = stringResource(R.string.offline_notice_title),
                            textAlign = TextAlign.Center
                        )
                    },
                    text = {
                        Text(stringResource(R.string.offline_notice_body))
                    },
                    onDismissRequest = {
                        dismissedOfflineDialog = true
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                dismissedOfflineDialog = true
                            }
                        ) {
                            Text(stringResource(R.string.confirm_action))
                        }
                    },
                )
            }

            if (showStudentNotice) {
                AlertDialog(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Student Alert"
                        )
                    },
                    title = {
                        Text(
                            text = stringResource(R.string.student_notice_title),
                            textAlign = TextAlign.Center
                        )
                    },
                    text = {
                        Text(
                            stringResource(R.string.student_notice_body),
                            textAlign = TextAlign.Justify
                        )
                    },
                    onDismissRequest = {
                        showStudentNotice = false
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showStudentNotice = false
                            }
                        ) {
                            Text(stringResource(R.string.confirm_action))
                        }
                    },
                )
            }

            // FIREBASE MESSAGING TEST
//            Surface(
//                color = MaterialTheme.colorScheme.background,
//                modifier = Modifier.fillMaxSize()
//            ) {
//                val state = viewModel.state
//                if (state.isEnteringToken) {
//                    EnterTokenDialog(
//                        token = state.remoteToken,
//                        onTokenChange = viewModel::onRemoteTokenChange,
//                        onSubmit = viewModel::onSubmitRemoteToken
//                    )
//                } else {
//                    ChatScreen(
//                        messageText = state.messageText,
//                        onMessageSend = {
//                            viewModel.sendMessage(isBroadcast = false)
//                        },
//                        onMessageBroadcast = {
//                            viewModel.sendMessage(isBroadcast = true)
//                        },
//                        onMessageChange = viewModel::onMessageChange
//                    )
//                }
//            }
        }
    }

            
//            val session by sessionViewModel.session.collectAsState()
//            var isLoading by remember { mutableStateOf(false) }
//
//
//
//            LaunchedEffect(true) {
//                Log.i("SESSION BOI", session.toString())
//                isLoading = true
//                delay(3000L)
//
//                Log.i("SESSION BOI", session.toString())
//
//                isLoading = false
//            }

//            val coroutineScope = rememberCoroutineScope()
//
//            val vm = TestViewModel(applicationContext)
//
////            LaunchedEffect(true) {
////                models =
////            }
//
//           val state by vm.session.collectAsStateWithLifecycle()

//            val sessionViewModel = SessionViewModel(LocalContext.current)


//            val test by sessionViewModel.test.collectAsState()
//            val session by sessionViewModel.session.collectAsStateWithLifecycle()

//            val shouldLogIn = session != null


//            Log.i("Session?", time.toString() )

//            if(session != null)
//                Log.i("SESSION IS NOT NULL",session.toString() )

//            val screenToDisplay = if(session != null)
//                LandingScreen() else LoginScreen()

//            val status by connectivityObserver.observe().collectAsState(
//                initial = ConnectivityObserver.Status.Unavailable
//            )
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(text = "Network status: $status")
//            }

    private fun isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_NOTIFICATION_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, // Changed type here
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


//@Composable
//private fun RowScope.TabNavigationItem(tab: Tab) {
//    val tabNavigator = LocalTabNavigator.current
//    val isSelected = tabNavigator.current == tab
//
//    NavigationBarItem(
//        selected = isSelected,
//        onClick = { tabNavigator.current = tab },
//        icon = {
//            Icon(
//                tab.options.icon!!,
//                contentDescription = tab.options.title,
//                modifier = Modifier.alpha(if (!isSelected) 0.5f else 1f)
//            )
//        },
//        label = {
//            Text(
//                text = tab.options.title,
//                textAlign = TextAlign.Center,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis,
//                modifier = Modifier.alpha(if (!isSelected) 0.5f else 1f),
//                style = if (isSelected) MaterialTheme.typography.bodyMedium
//                else MaterialTheme.typography.bodySmall
//            )
//        },
//        colors = NavigationBarItemDefaults.colors(
//            indicatorColor = MaterialTheme.colorScheme.primary,
//            selectedTextColor = MaterialTheme.colorScheme.primary
//        )
//    )
//}
