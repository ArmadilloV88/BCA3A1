package bot.lobby.bot_lobby.services

/**
 * Object to manage user credentials. Stores email and OAuth tokens.
 */
object CredentialsManager {
    private val credentials = mutableMapOf<String, String>()

    /**
     * Store email with its associated token (e.g., OAuth token).
     */
    fun addToken(email: String, token: String) {
        val normalizedEmail = email.trim().lowercase()
        credentials[normalizedEmail] = token
    }

    /**
     * Retrieve the stored token for a given email.
     */
    fun getToken(email: String): String? {
        val normalizedEmail = email.trim().lowercase()
        return credentials[normalizedEmail]
    }

    /**
     * Clear the stored token for a given email.
     */
    fun clearToken(email: String) {
        val normalizedEmail = email.trim().lowercase()
        credentials.remove(normalizedEmail)
    }

    /**
     * Validate if a token matches the stored one for the given email.
     */
    fun validateToken(email: String, token: String): Boolean {
        val normalizedEmail = email.trim().lowercase()
        return credentials[normalizedEmail] == token
    }
}