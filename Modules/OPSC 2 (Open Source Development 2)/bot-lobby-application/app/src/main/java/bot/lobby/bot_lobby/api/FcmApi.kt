package bot.lobby.bot_lobby.api

import bot.lobby.bot_lobby.models.AnnouncementNotification
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface FcmApi {
    @POST("projects/botlobby-43cb4/messages:send/")
    suspend fun postAnnouncement(
        @Header("Authorization") authorization: String, // NEEDS TO HAVE A "Bearer" PREFIX!
        @Body body: AnnouncementNotification
    )
}