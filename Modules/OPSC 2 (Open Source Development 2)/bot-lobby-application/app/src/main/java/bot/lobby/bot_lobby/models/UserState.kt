package bot.lobby.bot_lobby.models

sealed class UserState {
    object Loading: UserState()
    data class Success(val message: String): UserState()
    data class Error(val message: String): UserState()
}