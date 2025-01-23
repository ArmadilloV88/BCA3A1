package bot.lobby.bot_lobby

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class BotLobbyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase here
        FirebaseApp.initializeApp(this)

        // Verify initialization (Optional)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("Firebase initialization failed")
            } else {
                val token = task.result
                println("Firebase token: $token")
            }
        }

    }
}
