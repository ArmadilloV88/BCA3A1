package bot.lobby.bot_lobby.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.viewmodel.compose.viewModel
import bot.lobby.bot_lobby.models.Announcement
import bot.lobby.bot_lobby.models.User
import bot.lobby.bot_lobby.ui.composables.AnnouncementItem
import bot.lobby.bot_lobby.ui.theme.BlueStandard
import bot.lobby.bot_lobby.view_models.AnnouncementViewModel
import bot.lobby.bot_lobby.R
import java.time.LocalDate

@SuppressLint("NewApi")
@Composable
fun AnnouncementsScreen(
//    announcementViewModel: AnnouncementViewModel,
//    announcements: List<Announcement>,
    user: User,
    onAddAnnouncement: () -> Unit,
    onAnnouncementClick: (Announcement) -> Unit
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }


    val context = LocalContext.current
    val announcementViewModel = viewModel { AnnouncementViewModel(context) }

    announcementViewModel.refreshAnnouncements()

    var announcements by remember { mutableStateOf<List<Announcement>?>(null) }
    var error: String? by remember { mutableStateOf(null) }
    var isLoading by remember { mutableStateOf(true) }


//    var teamsUsers by remember { mutableStateOf<FetchResponse<List<User>>?>(null) }

    LaunchedEffect(true) {
        isLoading = true

        val response = announcementViewModel.getAnnouncements(user)

        if (response.errors.isNullOrEmpty()) {
            announcements = response.data
        } else {
            error = response.errors
        }

        isLoading = false
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.announcments),
                style = MaterialTheme.typography.headlineMedium.copy( // Changed h5 to headlineSmall
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center
            )


            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Text(stringResource(id = R.string.loading))
            } else if (!error.isNullOrEmpty()) {
                error?.let { Text(it) }
            } else if (announcements.isNullOrEmpty()) {
                Text(stringResource(id = R.string.no_announcements_found))
            } else {
                Box(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(22.dp)
                    ) {
                        items(announcements!!) { announcement ->
                            AnnouncementItem(
                                announcement = announcement,
                                onClick = { onAnnouncementClick(announcement) }
                            )
//                    Spacer(modifier = Modifier.height(8.dp)) // Spacer between items
                        }
                    }
                }
            }


//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                contentAlignment = Alignment.BottomEnd // Changed to BottomEnd for full compatibility
//            ) {

//            }
        }

        FloatingActionButton(
            onClick = onAddAnnouncement,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            // Align to bottom-right corner
//            shape = MaterialTheme.shapes.medium,
            containerColor = BlueStandard,
            contentColor = Color.White
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = stringResource(id = R.string.add_announcments),
                tint = Color.White
            )
        }

    }


}
