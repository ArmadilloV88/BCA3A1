package bot.lobby.bot_lobby.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import java.util.UUID

// Team data class
@Entity(
    tableName = "teams"
)
data class Team(
    @PrimaryKey(autoGenerate = false)
    var id: UUID = UUID.randomUUID(),

    val tag: String,
    val name: String,
    val userIdsAndRoles: List<IdAndRole>?,// Map<Int, String>, //List<Pair<Int, String>>
    val isPublic: Boolean = true,
    val isOpen: Boolean = true,
    val isLFM: Boolean = true,
    val maxNumberOfUsers: Int = 10,
    val bio: String? = null
)

data class IdAndRole(
    val id: Int,
    val isOwner: Boolean = true
)


class IdAndRoleConverters {

    // the following conversion was adapted from stackoverflow.com
    // Author: Enowneb (https://stackoverflow.com/users/21017714/enowneb)
    // Link: https://stackoverflow.com/questions/75166889/turn-string-of-object-back-into-object-kotlin
    @TypeConverter
    fun idAndRoleToString(list: List<IdAndRole>): String? {
        return Gson().toJson(list)
    }

    // the following conversion was adapted from stackoverflow.com
    // Author: Charlie Niekirk (https://stackoverflow.com/users/4745989/charlie-niekirk)
    // Link: https://stackoverflow.com/questions/47823746/kotlin-convert-json-string-to-list-of-object-using-gson
    @TypeConverter
    fun stringToIdAndRole(string: String?): List<IdAndRole> {
        return Gson().fromJson(string, Array<IdAndRole>::class.java).asList()
    }
}


