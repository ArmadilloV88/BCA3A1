package bot.lobby.bot_lobby.models

import java.util.Date
import java.util.UUID

//data class Announcement(
//    val message: Message
//)

data class Announcement(
    val forTeamId: UUID,
    val title: String,
    val body: String,
    val createdAt: Date = Date(), // Set current date and time
    val createdByUserId: Int, // Set to the ID of the current user
    
    // The following items are in plural to match the un-aliased joins returned from the database api
    val teams: Team? = null,
    val users: User? = null

)

// will be either of these ^
data class /* Message */ AnnouncementNotification(
    val message: Message
)

data class Message(
    val topic: String,
    val notification: NotificationBody
)

data class NotificationBody(
    val title: String,
    val body: String
)