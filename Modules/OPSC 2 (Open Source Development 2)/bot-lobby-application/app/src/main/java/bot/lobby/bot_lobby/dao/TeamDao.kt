package bot.lobby.bot_lobby.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import bot.lobby.bot_lobby.models.Team
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {

    @Upsert
    suspend fun upsertTeam(team: Team)

    @Delete
    suspend fun deleteTeam(team: Team)

    @Query("DELETE FROM teams")
    suspend fun deleteAll()

    @Query("SELECT * FROM teams")
    fun getTeams(): Flow<List<Team>>
}