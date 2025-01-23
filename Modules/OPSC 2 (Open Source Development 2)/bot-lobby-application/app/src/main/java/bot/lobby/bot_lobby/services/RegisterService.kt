package bot.lobby.bot_lobby.services

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import bot.lobby.bot_lobby.api.RetrofitInstance
import bot.lobby.bot_lobby.api.UserApi
import bot.lobby.bot_lobby.models.User
import bot.lobby.bot_lobby.utils.BiometricAuthHelper.registerBiometricData
import retrofit2.Response

class RegisterService(
    private val userApi: UserApi,
    private val activity: AppCompatActivity // Pass the AppCompatActivity as a parameter
) {

    suspend fun register(newUser: User): Response<User> {
        // Only register biometrics if isBiometricEnabled is true
        if (newUser.isBiometricEnabled) {
            val registeredUser = registerBiometricData(newUser, activity)
            newUser.biometrics = registeredUser.biometrics
        }

        // Proceed with user registration via API
        val response = userApi.register(RetrofitInstance.apiKey, newUser)
        if (response.isSuccessful) {
            Log.d("RegisterService", "Response: ${response.body()?.toString()}")
        } else {
            Log.e("RegisterService", "Error: ${response.errorBody()?.string()}")
        }
        return response
    }
}
