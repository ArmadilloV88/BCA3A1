package bot.lobby.bot_lobby.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import bot.lobby.bot_lobby.MainActivity
import bot.lobby.bot_lobby.R
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.text.SimpleDateFormat
import java.util.Locale

class PushNotificationService : FirebaseMessagingService() {

    companion object {
        private const val TOPIC_NEW_EVENTS = "new_events"
        private const val CHANNEL_ID = "event_notification_channel"

        // Subscribe to notifications
        fun subscribeToEventNotifications() {
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_NEW_EVENTS)
        }

        // Display a local notification with additional fields
        @SuppressLint("MissingPermission")
        fun showLocalNotification(
            context: Context,
            title: String,
            message: String,
            dateCreated: String,
            createdByUserId: String
        ) {
            // Create notification channel if necessary (Android 8.0+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID, "Announcement Notifications", NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Notifications for new announcements"
                }
                val manager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.createNotificationChannel(channel)
            }

            // Build and display the notification
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
            )

            // Left-align the elements in the notification content
            val notificationContent = """
                $message

                Date Created: $dateCreated
                Created By: $createdByUserId
            """.trimIndent()

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_bot_lobby_logo)
                .setContentTitle(title)
                .setContentText(notificationContent)
                .setStyle(
                    NotificationCompat.BigTextStyle().bigText(notificationContent)
                ) // Expands for full content
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                notify(System.currentTimeMillis().toInt(), builder.build())
            }
        }
    }


    //    override fun onMessageReceived(message: RemoteMessage) {
//        super.onMessageReceived(message)
//
//        // Respond to received messages
//    }

//    override fun onNewToken(token: String) {
//        super.onNewToken(token)
//
//        // Push token to server
//
//        // Then save token to db for the corresponding user
//    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.data.let { data ->
            val title = data["title"] ?: "New Announcement!"
            val message =
                data["message"] ?: "A new announcement has been posted. Come check it out!"
            val dateCreated = data["dateCreated"] ?: SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
            ).format(System.currentTimeMillis())
            val createdByUserId = data["createdByUserId"] ?: "Unknown User"

            sendNotification(title, message, dateCreated, createdByUserId)
        }
    }

    @SuppressLint("MissingPermission")
    private fun sendNotification(
        title: String,
        message: String,
        dateCreated: String,
        createdByUserId: String
    ) {
        showLocalNotification(
            context = this,
            title = title,
            message = message,
            dateCreated = dateCreated,
            createdByUserId = createdByUserId
        )
    }
}
