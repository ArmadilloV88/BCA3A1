package bot.lobby.bot_lobby.models

import android.util.Log
import com.google.auth.oauth2.GoogleCredentials
import java.io.ByteArrayInputStream

class AccessToken {
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

