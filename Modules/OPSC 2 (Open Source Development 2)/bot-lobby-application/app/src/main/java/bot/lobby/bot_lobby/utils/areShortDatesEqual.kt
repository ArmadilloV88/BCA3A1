package bot.lobby.bot_lobby.utils

import ch.benlu.composeform.formatters.dateShort
import java.util.Date

fun areShortDatesEqual(shortDate: String, date: Date): Boolean {
    return shortDate == dateShort(date)
}