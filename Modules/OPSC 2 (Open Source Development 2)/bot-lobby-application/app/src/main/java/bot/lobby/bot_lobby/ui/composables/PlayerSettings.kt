package bot.lobby.bot_lobby.ui.composables

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import bot.lobby.bot_lobby.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerSettings(
    onSignOut: () -> Unit = {},
    onDeleteAccount: () -> Unit = {},
    onSync: () -> Unit = {},
    isOffline: Boolean = false
) {
    // Variables for toggles and dropdown
    var isPushNotificationsEnabled by remember { mutableStateOf(false) }
    var isDarkModeEnabled by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("English") }


    // List of supported languages
    val languages = listOf("English", "Afrikaans")

    // State to control dropdown expansion
    var expanded by remember { mutableStateOf(false) }

    var isDeleteDialogOpen by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Scrollable Column for the settings content
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Centered Heading for Settings
        Text(
            text = stringResource(id = R.string.settings),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black, // Set text color to black
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 24.dp)
        )

        // Row 1: Offline Mode
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                stringResource(id = R.string.offline_mode),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold, // Bold text for section heading
                color = Color.Black // Black text color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(id = R.string.sync_to_online_database), fontSize = 16.sp)
                IconButton(
                    onClick = onSync,
//                    modifier = Modifier.size(24.dp) // Icon button with no border
                    enabled = !isOffline
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(id = R.string.sync),
//                        tint = Color.Black // Icon color
                    )
                }
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        // Row 2: Notifications
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                stringResource(id = R.string.notifications),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold, // Bold text for section heading
                color = Color.Black // Black text color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(id = R.string.push_notifications), fontSize = 16.sp)
                Switch(
                    checked = isPushNotificationsEnabled,
                    onCheckedChange = { isPushNotificationsEnabled = it }
                )
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        // Row 3: Themes
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                stringResource(id = R.string.themes),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold, // Bold text for section heading
                color = Color.Black // Black text color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(id = R.string.dark_mode), fontSize = 16.sp)
                Switch(
                    checked = isDarkModeEnabled,
                    onCheckedChange = { isDarkModeEnabled = it }
                )
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        // Row 4: Language Dropdown
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                stringResource(id = R.string.language),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold, // Bold text for section heading
                color = Color.Black // Black text color
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Language selection dropdown with square edges and a down arrow
            Box {
                OutlinedButton(
                    onClick = { expanded = true }, // Toggle dropdown expansion
                    shape = RoundedCornerShape(8.dp), // Slightly rounded edges
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp), // Adjust height for a consistent button size
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Selected language text
                        Text(selectedLanguage)

                        // Down arrow icon
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown Arrow",
                            tint = Color.Black // Icon color
                        )
                    }
                }

                // Dropdown menu for language options
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false } // Close dropdown when dismissed
                ) {
                    languages.forEach { language ->
                        DropdownMenuItem(
                            text = { Text(language) },
                            onClick = {
                                if(language == "English"){
                                    changeLocales(context,"en")

                                } else{
                                    changeLocales(context,"af")

                                }


                                selectedLanguage = language // Set selected language
                                expanded = false // Close dropdown after selection
                            }
                        )
                    }
                }
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        // Row 5: Account
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                stringResource(id = R.string.account),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold, // Bold text for section heading
                color = Color.Black // Black text color
            )
            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = onSignOut,
//                navigator.push(LoginScreen())  // Push the LoginScreen back onto the stack
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                border = BorderStroke(1.dp, Color.Gray),
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = stringResource(id = R.string.delete))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(id = R.string.sign_out), fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = { isDeleteDialogOpen = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isOffline
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = stringResource(id = R.string.delete))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(id = R.string.delete_account), fontSize = 16.sp)
            }
        }
    }

    if (isDeleteDialogOpen) {
        AlertDialog(
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = stringResource(id = R.string.offline_mode)
                )
            },
            title = {
                Text(
                    text = stringResource(R.string.delete_account_title),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(stringResource(R.string.delete_account_body))
            },
            onDismissRequest = {
                isDeleteDialogOpen = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteAccount()
                        isDeleteDialogOpen = false
                    }
                ) {
                    Text(stringResource(R.string.confirm_action))
                }
            },
        )
    }
}

fun changeLocales(context: Context, localeString: String){
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
        context.getSystemService(LocaleManager::class.java)
            .applicationLocales = LocaleList.forLanguageTags(localeString)
    } else{
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(localeString))
    }
}
