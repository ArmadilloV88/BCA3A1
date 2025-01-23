package bot.lobby.bot_lobby.models

data class AuthResponse(
    val access_token: String?,   // Token for successful authentication
    val refresh_token: String?,  // If refresh tokens are provided
    val token_type: String?,     // Type of token (usually "Bearer")
    val expires_in: Int?,        // Token expiration time
    val error: String?           // In case of errors
)