package bot.lobby.bot_lobby.models

data class FetchResponse<T>(
    val data: T? = null,
    val errors: String? = null
)