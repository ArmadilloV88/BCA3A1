package bot.lobby.bot_lobby.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bot.lobby.bot_lobby.models.Team
import bot.lobby.bot_lobby.R

//Used by Scouting

@Composable
fun TeamItem(team: Team) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Team icon (using ic_player_tag.png)
        Image(
            painter = painterResource(id = R.drawable.ic_player_tag),
            contentDescription = "Team Icon",
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Team tag
        Text(
            text = team.tag,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.weight(1f))

        // Number of team members as a disabled button
        Button(
            onClick = { /* Disabled button */ },
            enabled = false,  // Disable the button
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,  // White background
                disabledContainerColor = Color.White,  // White background when disabled
                contentColor = Color.Black,  // Black text color
                disabledContentColor = Color.Black // Black text color when disabled
            ),
            shape = RoundedCornerShape(8.dp),  // Small rounded corners
            modifier = Modifier
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))  // Grey border
                .size(80.dp, 40.dp)  // Square button with rounded edges
        ) {

            Text(text = "${team.userIdsAndRoles?.size}/${team.maxNumberOfUsers}")  // XX/XX format
        }
    }
}
