package bot.lobby.bot_lobby.api


import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "https://ynsntpgpunobawajnbow.supabase.co/rest/v1",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inluc250cGdwdW5vYmF3YWpuYm93Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjQ1MjAyNjAsImV4cCI6MjA0MDA5NjI2MH0.LpyqZLqJp8bgSOXd3Tuw9Nntq60WJ4-GtAGK9bbJko8"
    ) {
        install(GoTrue)
        install(ComposeAuth) {
            googleNativeLogin(serverClientId = "812262640049-8kkpdsig31r35bqlogg7vtrchmjnk8vl.apps.googleusercontent.com")
        }
    }
}