package bot.lobby.bot_lobby.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson

@Entity(
    tableName = "session"
)
data class Session (
    @PrimaryKey(autoGenerate = true)
    var id: Int? = 0,
    var userLoggedIn: User,
    var usersTeams: List<Team>
)


class SessionConverters {

    // the following conversion was adapted from stackoverflow.com
    // Author: Enowneb (https://stackoverflow.com/users/21017714/enowneb)
    // Link: https://stackoverflow.com/questions/75166889/turn-string-of-object-back-into-object-kotlin
    @TypeConverter
    fun userToString(userLoggedIn: User?): String? {
        return Gson().toJson(userLoggedIn)
    }

    // the following conversion was adapted from stackoverflow.com
    // Author: Charlie Niekirk (https://stackoverflow.com/users/4745989/charlie-niekirk)
    // Link: https://stackoverflow.com/questions/47823746/kotlin-convert-json-string-to-list-of-object-using-gson
    @TypeConverter
    fun stringToUser(string: String?): User? {
        return Gson().fromJson(string, User::class.java)
    }



    // the following conversion was adapted from stackoverflow.com
    // Author: Enowneb (https://stackoverflow.com/users/21017714/enowneb)
    // Link: https://stackoverflow.com/questions/75166889/turn-string-of-object-back-into-object-kotlin
    @TypeConverter
    fun teamsToString(usersTeams: List<Team>): String? {
        return Gson().toJson(usersTeams)
    }

    // the following conversion was adapted from stackoverflow.com
    // Author: Charlie Niekirk (https://stackoverflow.com/users/4745989/charlie-niekirk)
    // Link: https://stackoverflow.com/questions/47823746/kotlin-convert-json-string-to-list-of-object-using-gson
    @TypeConverter
    fun stringToTeams(string: String?): List<Team> {
        return Gson().fromJson(string, Array<Team>::class.java).asList()
    }
}