package bot.lobby.bot_lobby.models

data class Player(
    val player: String,     // This stores the email address (e.g., "user1@demo.com")
    val playertag: String,        // This stores the "Player Tag #" (e.g., "Player Tag 1")
    val teams: List<String>,// Allows multiple teams or no team
    val description: String
)