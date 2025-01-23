package bot.lobby.bot_lobby.ui.pages

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import bot.lobby.bot_lobby.ui.screens.AnnouncementsScreen
import bot.lobby.bot_lobby.ui.screens.NewAnnouncementScreen
import bot.lobby.bot_lobby.view_models.AnnouncementViewModel
import bot.lobby.bot_lobby.view_models.AnnouncementViewModelFactory
import bot.lobby.bot_lobby.view_models.SessionViewModel

object AnnouncementsTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = "Announcements"
            val icon = rememberVectorPainter(Icons.Default.ElectricBolt)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        var showNewAnnouncementScreen by remember { mutableStateOf(false) }

        val context = LocalContext.current
        val announcementViewModel: AnnouncementViewModel =
            viewModel(factory = AnnouncementViewModelFactory(context))
        val sessionViewModel = viewModel { SessionViewModel(context) }
        val session by sessionViewModel.session.collectAsState()

        // Retrieve the current user's ID (replace with actual retrieval method)
        val currentUserId = "user_id_example" // Replace with actual logic to get the user ID

        if (showNewAnnouncementScreen) {
            session?.let {
                NewAnnouncementScreen(
                    session = it,
                    viewModel = announcementViewModel,
                    onCancel = {
                        showNewAnnouncementScreen = false
                    },
                    onPostAnnouncement = {
                        showNewAnnouncementScreen = false
                    },
                    currentUserId = currentUserId // Passing the required user ID here
                )
            }
        } else {
            session?.userLoggedIn?.let {
                AnnouncementsScreen(
                    //                announcementViewModel = announcementViewModel,
                    //                announcements = announcements,
                    user = it,
                    onAddAnnouncement = {
                        showNewAnnouncementScreen = true
                    },
                    onAnnouncementClick = { announcement ->
                        // Optional click action
                    }
                )
            }
        }
    }
}
