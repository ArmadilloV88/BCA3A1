package bot.lobby.bot_lobby.api

import bot.lobby.bot_lobby.models.Announcement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface AnnouncementApi {
    @GET("announcements")
    suspend fun getAnnouncements(
        @Header("apikey") key: String,
        @Query("select") joinQuery: String? = null,
        @Query("forTeamId") teamQuery: String? = null
    ): Response<List<Announcement>>


    @POST("announcements")
    suspend fun saveAnnouncement(
        @Header("apikey") key: String,
        @Body body: Announcement
    )
}