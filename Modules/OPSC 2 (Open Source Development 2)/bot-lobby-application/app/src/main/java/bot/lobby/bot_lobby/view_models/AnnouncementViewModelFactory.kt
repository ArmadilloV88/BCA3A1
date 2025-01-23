package bot.lobby.bot_lobby.view_models

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AnnouncementViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnnouncementViewModel::class.java)) {
            return AnnouncementViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
