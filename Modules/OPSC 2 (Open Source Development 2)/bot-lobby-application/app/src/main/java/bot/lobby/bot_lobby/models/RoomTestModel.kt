package bot.lobby.bot_lobby.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RoomTestModel (

    val firstName: String,
    val lastName: String,
    val phoneNumber: String,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
