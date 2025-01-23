package bot.lobby.bot_lobby.api

import bot.lobby.bot_lobby.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApi {
    @GET("users")
    suspend fun getUser(
        @Header("apikey") key: String,
        @Query("id") id: String? = null,
    ): Response<List<User>>

    @GET("users")
    suspend fun getUsers(
        @Header("apikey") key: String,
        @Query("id") id: String? = null,
        @Query("email") email: String? = null
    ): Response<List<User>>

    @GET("users")
    suspend fun login(
        @Header("apikey") apiKey: String,
        @Query("username") username: String,
        @Query("password") password: String
    ): Response<List<User>>

    @POST("users")
    suspend fun register(
        @Header("apikey") apiKey: String,
        @Body user: User
    ): Response<User>

    @GET("users")
    suspend fun getUsersByUsername(
        @Header("apikey") apiKey: String,
        @Query("username") username: String
    ): Response<List<User>>

    @GET("users")
    suspend fun getUsersByName(
        @Header("apiKey") key: String,
        @Query("username") username: String? = null,
        @Query("isPublic") isPublic: String? = null,
    ): Response<List<User>>

    @POST("users")
    suspend fun createUser(@Header("apikey") key: String, @Body user: User): Response<Unit>

    @PATCH("users")
    suspend fun updateUser(
        @Header("apikey") key: String,
        @Query("id") id: String,
        @Body user: User
    ): Response<User>

    @DELETE("users")
    suspend fun deleteUser(@Header("apikey") key: String, @Query("id") id: String): Response<User>
}