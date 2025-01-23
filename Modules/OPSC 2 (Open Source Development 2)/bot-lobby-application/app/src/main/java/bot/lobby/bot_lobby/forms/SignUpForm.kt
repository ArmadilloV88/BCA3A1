package bot.lobby.bot_lobby.forms

import androidx.compose.runtime.mutableStateOf
import ch.benlu.composeform.FieldState
import ch.benlu.composeform.Form
import ch.benlu.composeform.FormField
import ch.benlu.composeform.validators.IsEqualValidator
import ch.benlu.composeform.validators.MinLengthValidator
import bot.lobby.bot_lobby.forms.validators.IsRequiredValidator
import bot.lobby.bot_lobby.forms.validators.MaxLengthValidator

class SignUpForm : Form() {

    override fun self(): Form {
        return this
    }

    // Email Field
    @FormField
    val username = FieldState(
        state = mutableStateOf<String?>(""),
        validators = mutableListOf(
            IsRequiredValidator(),
            MaxLengthValidator(100),
        )
    )

    // Password Field
    @FormField
    val password = FieldState(
        state = mutableStateOf<String?>(""),
        validators = mutableListOf(IsRequiredValidator(), MinLengthValidator(8))
    )

    // Password Confirmation Field
    @FormField
    val passwordConfirmation = FieldState(
        state = mutableStateOf<String?>(""),
        validators = mutableListOf(
            IsRequiredValidator(),
            MaxLengthValidator(100),
            IsEqualValidator({ password.state.value }, errorText = "Passwords don't match")
        )
    )
}
