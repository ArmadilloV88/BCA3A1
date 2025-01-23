package bot.lobby.bot_lobby.models

import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import java.util.UUID


//@Entity(
//    tableName = "users"
//)
//data class User(
//    @PrimaryKey(autoGenerate = false)
//    val id: Int? = null,           // Assuming the ID is auto-generated
//    val role: Int = 1,               // The type_id references user type (User, Admin, etc.)
//    val bio: String? = null,
//    val username: String,               // Username of the user
//    val password: String? = null,       // Password of the user (may want to consider encryption later)
//    val biometrics: String? = null,     // Nullable in case biometrics are not provided
//    var teamIds: List<UUID>? = null,
//    var isPublic: Boolean = true,
//    var isLFT: Boolean = true,
//    var email: String? = null
//)
  
data class User(
    @PrimaryKey(autoGenerate = false)
    val id: Int? = null,                // Assuming the ID is auto-generated
    val role: Int = 1,                  // The type_id references user type (User, Admin, etc.)
    val bio: String? = null,
    val username: String,               // Username of the user
    val password: String? = null,       // Password of the user (may want to consider encryption later)
    var biometrics: String? = null,     // Nullable in case biometrics are not provided
    var teamIds: List<UUID>? = null,
    var isPublic: Boolean = true,
    var isLFT: Boolean = true,
    var email: String? = null,
    var isBiometricEnabled: Boolean = false  // New property for biometric registration
)

class TeamIdsConverters {
    // the following conversion was adapted from stackoverflow.com
    // Author: Enowneb (https://stackoverflow.com/users/21017714/enowneb)
    // Link: https://stackoverflow.com/questions/75166889/turn-string-of-object-back-into-object-kotlin
    @TypeConverter
    fun teamIdsToString(list: List<UUID>?): String? {
        return Gson().toJson(list)
    }

    // the following conversion was adapted from stackoverflow.com
    // Author: Charlie Niekirk (https://stackoverflow.com/users/4745989/charlie-niekirk)
    // Link: https://stackoverflow.com/questions/47823746/kotlin-convert-json-string-to-list-of-object-using-gson
    @TypeConverter
    fun stringToTeamIds(string: String?): List<UUID>? {
        return Gson().fromJson(string, Array<UUID>::class.java)?.asList()
    }
}
