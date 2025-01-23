package bot.lobby.bot_lobby.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.gson.gson

// Object to configure and provide a singleton HttpClient instance
object ApiClient {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            gson() // Use Gson for JSON serialization/deserialization
        }
    }
}
