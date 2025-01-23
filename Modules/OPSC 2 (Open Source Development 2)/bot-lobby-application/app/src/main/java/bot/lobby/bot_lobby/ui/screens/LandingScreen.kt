package bot.lobby.bot_lobby.ui.screens

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.tab.TabNavigator
import bot.lobby.bot_lobby.ui.composables.ScreenLayout
import bot.lobby.bot_lobby.ui.pages.HomeTab

class LandingScreen() : Screen {
    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
//        val context = LocalContext.current
//        val sessionViewModel = viewModel { SessionViewModel(context) }
//        val session by sessionViewModel.session.collectAsStateWithLifecycle()
//        val navigator = LocalNavigator.currentOrThrow
//
//        if(session != null){
//            navigator.popUntilRoot()
//        }

        TabNavigator(HomeTab) { tabNavigator ->
            ScreenLayout(tabNavigator)
        }
    }
}