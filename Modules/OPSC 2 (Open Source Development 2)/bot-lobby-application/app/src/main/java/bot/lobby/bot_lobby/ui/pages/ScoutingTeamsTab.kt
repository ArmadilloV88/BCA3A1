package bot.lobby.bot_lobby.ui.pages

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PeopleOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

import bot.lobby.bot_lobby.ui.screens.ScoutingTeamsScreen


object ScoutingTeamsTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = "Teams"
            val icon = rememberVectorPainter(Icons.Default.PeopleOutline)

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
        ScoutingTeamsScreen()
    }
}