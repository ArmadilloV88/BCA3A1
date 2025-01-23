package bot.lobby.bot_lobby.view_models

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.util.Log
import androidx.lifecycle.ViewModel
import bot.lobby.bot_lobby.api.RetrofitInstance
import bot.lobby.bot_lobby.models.Announcement
import bot.lobby.bot_lobby.models.AnnouncementNotification
import bot.lobby.bot_lobby.models.FetchResponse
import bot.lobby.bot_lobby.models.Message
import bot.lobby.bot_lobby.models.NotificationBody
import bot.lobby.bot_lobby.models.User
import bot.lobby.bot_lobby.services.PushNotificationService
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayInputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class AnnouncementViewModel(context: Context) : ViewModel() {
    private val context by lazy { context }

    // List to store announcements
    private val _announcements = mutableListOf<Announcement>()

    private val _isFetching = MutableStateFlow(false)
    val isFetching: StateFlow<Boolean> = _isFetching

    private val _fetchError: MutableStateFlow<String?> = MutableStateFlow(null)
    val fetchError: StateFlow<String?> = _fetchError.asStateFlow()

    private val _refreshAnnouncements: MutableStateFlow<Boolean> = MutableStateFlow(false)


    val announcements =
        combine(_isFetching, _refreshAnnouncements) { isFetching, refresh ->
            if (refresh) {
                _refreshAnnouncements.value = false
            }

            _isFetching.value = true


            val joinQuery = "*,users(*),teams(*)"

            try {
                val response =
                    RetrofitInstance.AnnouncementApi.getAnnouncements(
                        RetrofitInstance.apiKey,
                        joinQuery
                    )

                Log.i("DATA FROM HERE", response.body().toString())
                _isFetching.value = false


                response.body()?.toList()
            } catch (exception: Exception) {
                _fetchError.value = exception.message.toString()
                Log.i("ERROR!", exception.message.toString())

                _isFetching.value = false
                emptyList()
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun refreshAnnouncements() {
        _refreshAnnouncements.value = true
    }

    suspend fun getAnnouncements(user: User): FetchResponse<List<Announcement>> {
        var announcements: List<Announcement> = listOf()
        var errorMessage: String? = null

        try {
            val joinQuery = "*,users(*),teams(*)"

            // Create a query string that will be used to get all the announcements for all the
            // users teams based on the teams ids
            var teamQuery = "in.("

            user.teamIds?.forEach { id ->
                teamQuery += "$id"

                teamQuery += if (user.teamIds!!.indexOf(id) == (user.teamIds!!.size - 1)) ")" else ","
            }

            // Fetch data
            val response =
                RetrofitInstance.AnnouncementApi.getAnnouncements(
                    key = RetrofitInstance.apiKey,
                    joinQuery = joinQuery,
                    teamQuery = teamQuery
                )
            val body = response.body()
            if (body != null) {
                announcements = body
            }
        } catch (exception: Exception) {
            errorMessage = exception.message.toString()
            Log.i("ERROR!", exception.message.toString())
        }
//        }

        return FetchResponse(announcements, errorMessage)
    }


    // State variable to track if an announcement was recently saved
    var announcementSaved = mutableStateOf(false)
        private set

    init {
        initializeFirebase()
    }


    fun postAnnouncement(announcement: Announcement) {
        viewModelScope.launch {
            try {
                subscribeToTeamAnnouncements(announcement.forTeamId)

                RetrofitInstance.AnnouncementApi.saveAnnouncement(
                    key = RetrofitInstance.apiKey,
                    body = announcement
                )

                val res = RetrofitInstance.FcmApi.postAnnouncement(
                    authorization = "Bearer ${getAccessToken()}",
                    body = AnnouncementNotification(
                        Message(
                            topic = "team-${announcement.forTeamId}-announcements",
                            notification = NotificationBody(
                                title = announcement.title,
                                body = announcement.body
                            )
                        )

                    )
                )

                Log.i("RES", res.toString())

                refreshAnnouncements()

            } catch (e: Exception) {
                e.printStackTrace()

                Log.i("Post Announcement Error", e.toString())
            }
        }
    }

    private fun initializeFirebase() {
        // Ensure Firebase is initialized before subscribing to topics
        if (FirebaseApp.getApps(context).isNotEmpty()) {
            FirebaseMessaging.getInstance().subscribeToTopic("new_announcements")
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        // Handle subscription failure if needed
                    }
                }
        } else {
            // Log an error or handle initialization failure
            println("Firebase is not initialized")
        }
    }

    // Save an announcement and trigger a push notification
    fun saveAnnouncement(title: String, body: String, forTeamId: UUID, createdByUserId: Int) {
        val newAnnouncement = Announcement(
            forTeamId = forTeamId,
            title = title,
            body = body,
            createdAt = Date(), // Set current date and time
            createdByUserId = createdByUserId // Set to the ID of the current user
        )

        println("saveAnnouncement called for: ${newAnnouncement.title}")  // Debug line
        _announcements.add(newAnnouncement)
        announcementSaved.value = true // Set to true to indicate a successful save

        // Trigger push notification for the new announcement
        sendPushNotification(newAnnouncement)
    }

    // Reset the save status (e.g., call this after showing a confirmation message)
    fun resetAnnouncementSaved() {
        announcementSaved.value = false
    }

    // Function to send push notification using Firebase Cloud Messaging
    private fun sendPushNotification(announcement: Announcement) {
        if (FirebaseApp.getApps(context).isNotEmpty()) {
            viewModelScope.launch {
                // Adding extra spaces to approximate centering
                val notificationContent = """
                ${announcement.body}

                """.trimIndent()

                PushNotificationService.showLocalNotification(
                    context = context,
                    title = "New Announcement: ${announcement.title}",
                    message = notificationContent,
                    dateCreated = SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault()
                    ).format(announcement.createdAt),  // Avoid duplication in the notification
                    createdByUserId = "${announcement.createdByUserId}"
                )
            }
        } else {
            // Handle Firebase unavailability
            println("Firebase is not initialized")
        }
    }


    fun subscribeToTeamAnnouncements(teamId: UUID) {
        viewModelScope.launch {
            Firebase.messaging.subscribeToTopic("team-${teamId}-announcements").await()
        }
    }

    fun unsubscribeFromTeamAnnouncements(teamId: UUID) {
        viewModelScope.launch {
            Firebase.messaging.unsubscribeFromTopic("team-${teamId}-announcements").await()
        }
    }

    private val firebaseMessagingScope = "https://www.googleapis.com/auth/firebase.messaging"
    private fun getAccessToken(): String? {
        var token: String? = null

        try {
            val jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"botlobby-43cb4\",\n" +
                    "  \"private_key_id\": \"6c0368b55db277db37239ace88deda60881fcd31\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCeAMuKdBl17zFF\\nK7Fse1lHMyUUfpCA+kpsyyqdeYuMe/GtYwn75BevwhSNroW5txasC62SzfyIb4Ak\\nXe8LnjYYNx3xC7KIsptVRgpGVIAu+tswvcyjjBdJ8zZ5/gbSXZ7Pd91vmjdt7v6H\\n4kEh+FR4apkQSo1bgqk8lX3gFB9kV7Ho7VCivjG/PXIXZD969VWcFM+lni4vzlFc\\nYAzWY5WwkF9YEEzCcDOg+Vz2kEFoydAV4HKTpifkDwpd4KKc/FSSikJtgGyWWje6\\nyS7OB4ZvwJwskXpCXvehNrOeEecCyaEiCsdhiKkXetrqnvxQlJAPInLW0Xn8MkgP\\ns1huQs0ZAgMBAAECggEAAwwPUpzJd5Sr4j7WsTmq3eHfn4bH/09w1gAQKDgx98Qq\\n0gJsnoimWhHu5RiY3Z0+o7BRE9lfkGYG+dc8lUeNtYPX7zPKb/oA7Y2EksPDFmv3\\nZ78fhxTOAGVtlWpBgk/4HqTUzdtoE10ByhKiLrVrM533/0Wn6HUbZxeX2NyOCm3h\\n3hmEf8lnqvesu8VNG9oFU+Zg6jOlR+DMbr5sjbZqrAFbxzJarCbseA0v7TgePk9c\\nR0Gqq4gxE8SNFeXfo9BCKZGbFyO1HozdOHfn1Yi5v3jjichTi+/NOU6Q+hOTt40K\\nOyyjFaXTESYK7FuMIQr/AKE4z25S7YXhSYzZ1eoBLQKBgQDVATDk504MBPbDw05u\\nddccAJ2+EJMxRdZ02kUQhslpRtBXfBrzsCvlgIldtjViWtMyNgS3IitUvn8LaLiu\\nBk5ANcmY0zLvUVItzkSlcQIfti72xCv7TXBb6JRlnEU8Ury1PkmNNIsCz4Mvmdrr\\nBld1PrsrHLICPVyBKGxSD2pIJwKBgQC95XPU84TIaXT9hmxI1K64DyhnjFURfYsN\\nR8FUutjRhJfP7uGXlEAVWbGPXJzgUXKSAGmTosW2U00pbLDnqpKsOCkcrg72LzoX\\npwSIFN4d+DUxAiiv2lmcpPv0QoxSAzwo29CGB3KUDp+uQAn7ykrHZPv5kXkrZI83\\nNW776HRIvwKBgCCoThbKX+z31d2t1BibeJ+SeJzrbaXgT0EbNfuSrs61TLwdCwTR\\nvekyC92SUh0CT98i6RYq5vnXOyNEqwwDa3vswzvrJaQLk83yEhRLf0fdZtXuvc+J\\nz/BHeHkbvIHDLaucISrFI4sKxdJGhIoELuSK+adifuhZABXNXM44DtOBAoGAdn94\\niKptcaJdHteDOu99F95dmEA51XBCFUdxnS37nsLe+SJYI/6e/rO52XwxaMlqdTFM\\nsJQiTGQvAmqQ+f+3B0EEZqE3NOjDl37hlQlwkcMRbkOqoBHhWZ6amxHr/kGGiPii\\nZwC+vyVg/5t2n2jcwRCDLHla43kc5Zayav/jkIECgYEAuloxPTPrkrRrqhTeOVxU\\nUJAn6HhzI9Bs6dp3stMIckbCGXjnuFSVSfJerWDVKQ8TG06YYBoO8wySxU+fWmJs\\n0L3Lf6F1qJVBcl6hRqFyvpGQm0UP6RPSsUgLCqBOP1usVluB3mtSOpLpml80+LB/\\n/wcyBaewRDwccFh5mgJoZlk=\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-nv1d0@botlobby-43cb4.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"107165520974040775180\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-nv1d0%40botlobby-43cb4.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}\n"

            val stream = ByteArrayInputStream(jsonString.toByteArray(Charsets.UTF_8))

            val googleCredentials = GoogleCredentials.fromStream(stream)
                .createScoped(arrayListOf(firebaseMessagingScope))

            googleCredentials.refresh()



            token = googleCredentials.accessToken.tokenValue

        } catch (ex: Exception) {
            Log.e("error", ex.message.toString())

        }

        Log.i("MY TOKEN!", token.toString())

        return token
    }
}
