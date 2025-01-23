package bot.lobby.bot_lobby.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstance {
    const val apiKey =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inluc250cGdwdW5vYmF3YWpuYm93Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjQ1MjAyNjAsImV4cCI6MjA0MDA5NjI2MH0.LpyqZLqJp8bgSOXd3Tuw9Nntq60WJ4-GtAGK9bbJko8"

    private const val BASE_URL = "https://ynsntpgpunobawajnbow.supabase.co/rest/v1/"

    private const val AUTH_BASE_URL = "https://ynsntpgpunobawajnbow.supabase.co/auth/v1/"

    private const val FCM_BASE_URL =
        "https://fcm.googleapis.com/v1/"

    // AuthApi for login and registration
    val AuthApi by lazy {
        Retrofit.Builder()
            .baseUrl(AUTH_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    val UserApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApi::class.java)
    }

    val TeamApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TeamApi::class.java)
    }


    val FcmApi by lazy {
        Retrofit.Builder()
            .baseUrl(FCM_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(FcmApi::class.java)
    }

    val AnnouncementApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AnnouncementApi::class.java)
    }
}