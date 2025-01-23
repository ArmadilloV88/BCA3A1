package bot.lobby.bot_lobby.models

// Data class representing an event
data class Event(
    val title: String,          // Title of the event
    val description: String,    // Description of the event
    val startDate: String,      // Start date of the event (could be a Date type if preferred)
    val endDate: String,        // End date of the event
    val team: String,           // Team associated with the event
    val teamMembers: List<String> // List of team members participating in the event
)
