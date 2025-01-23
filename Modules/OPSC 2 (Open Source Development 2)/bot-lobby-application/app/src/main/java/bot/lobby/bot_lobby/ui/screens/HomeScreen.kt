package bot.lobby.bot_lobby.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bot.lobby.bot_lobby.R

// Main content composable for the Home Screen, responsible for rendering all UI elements
@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display the "Welcome Back!" heading centered on the screen
        Text(
            text = stringResource(id = R.string.welcome_back),
            style = MaterialTheme.typography.h4,
            color = Color.Black,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        // Placeholder for announcements or other sections that can be added later
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.announcements),
            fontSize = 18.sp,
            color = Color.Gray,
            modifier = Modifier.padding(16.dp)
        )
    }
}
