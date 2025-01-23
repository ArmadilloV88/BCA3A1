package bot.lobby.bot_lobby.ui.composables

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bot.lobby.bot_lobby.models.User
import bot.lobby.bot_lobby.R

// Handles role access (Teamsprofile)

@Composable
fun PlayerItem(user: User, role: String? = null, onProfileClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Player icon (using ic_player_tag.png)
        Image(
            painter = painterResource(id = R.drawable.ic_player_tag),
            contentDescription = "Player Icon",
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Player tag
        Text(
            text = user.username,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.weight(1f))

        // Role button
        Button(
            onClick = { /* Handle role button click if needed */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            border = BorderStroke(1.dp, Color.Gray),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = role!!.uppercase(), fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.width(8.dp))

        // View Profile IconButton
        IconButton(
            onClick = onProfileClick
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_eye_profile),
                contentDescription = "View Profile",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
