package bot.lobby.bot_lobby.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Composable function to display the Teams header with number of teams
@Composable
fun TeamsHeader(totalTeams: Int, maxTeams: Int = 10) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp),
        horizontalAlignment = Alignment.CenterHorizontally // Center the contents horizontally
    ) {
        // Teams Heading in bold and black
        Text(
            text = "Teams",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Display number of teams (formatted as XX/XX) in bold, black, and the same size as the Teams heading
        Text(
            text = "$totalTeams/$maxTeams",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        )
    }
}
