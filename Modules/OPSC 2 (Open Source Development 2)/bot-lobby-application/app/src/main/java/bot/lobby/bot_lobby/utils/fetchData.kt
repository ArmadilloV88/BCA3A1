package bot.lobby.bot_lobby.utils

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import bot.lobby.bot_lobby.models.FetchResponse
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


fun <T> fetchData(fetchFunction: () -> FetchResponse<T>): Result<T> {
    val newResult = Result<T>()

    runBlocking {
        launch {
            newResult.isLoading.value = true

            val fetchResponse = fetchFunction()

            if (fetchResponse.errors.isNullOrEmpty()) {
                newResult.data.value = fetchResponse.data
            } else {
                newResult.isError.value = true
                newResult.error.value = fetchResponse.errors
            }

            newResult.isLoading.value = false
        }
    }
    // The following was adapted from stackoverflow.com
    // Author: milan (https://stackoverflow.com/users/514306/milan)
    // Link: https://stackoverflow.com/questions/56659045/kotlin-coroutines-how-to-get-coroutinescope-for-the-current-thread
//    val scope = CoroutineScope(coroutineContext)

    return newResult
}

data class Result<T>(
    val isLoading: MutableState<Boolean> = mutableStateOf(false),
    val data: MutableState<T?> = mutableStateOf(null),
    val isError: MutableState<Boolean> = mutableStateOf(false),
    val error: MutableState<String?> = mutableStateOf(null)
)