package bot.lobby.bot_lobby.api

import bot.lobby.bot_lobby.models.Team
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface TeamApi {
    @GET("teams")
    suspend fun getTeams(
        @Header("apiKey") key: String,
        @Query("id") id: String? = null
    ): Response<List<Team>>

    @GET("teams")
    suspend fun getTeamsByName(
        @Header("apiKey") key: String,
        @Query("name") name: String? = null,
        @Query("isPublic") isPublic: String? = null,
    ): Response<List<Team>>

    @GET("teams")
    suspend fun getUsersTeams(@Header("apiKey") key: String): Response<List<Team>>

    @POST("teams")
    suspend fun createTeam(@Header("apiKey") key: String, @Body team: Team): Response<Team>

    @PATCH("teams")
    suspend fun updateTeam(
        @Header("apiKey") key: String,
        @Query("id") id: String,
        @Body team: Team,
    ): Response<Team>

    @DELETE("teams")
    suspend fun deleteTeam(@Header("apiKey") key: String, @Query("id") id: String): Response<Team>
}