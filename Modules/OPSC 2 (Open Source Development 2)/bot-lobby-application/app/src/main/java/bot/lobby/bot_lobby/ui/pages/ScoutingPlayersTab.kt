package bot.lobby.bot_lobby.ui.pages

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import bot.lobby.bot_lobby.ui.screens.ScoutingPlayersScreen

object ScoutingPlayersTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = "Players"
            val icon = rememberVectorPainter(Icons.Default.PersonOutline)

            return remember {
                TabOptions(
                    index = 1u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        ScoutingPlayersScreen()
    }
}