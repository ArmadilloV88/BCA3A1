package bot.lobby.bot_lobby.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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

//USed by scouting

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoutTeamListItem(
    team: Team,
    onView: () -> Unit = {}
) {
    // Main Box for each team
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                BorderStroke(1.dp, Color.Gray),
                shape = RoundedCornerShape(8.dp)
            ) // Border for each box
            .padding(8.dp) // Padding inside the box
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp) // Padding inside the box
        ) {
            // First Row: Icon, Team tag and name, Teams Button, and View Profile Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_player_tag),
                    contentDescription = "Player Tag Icon",
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = team.tag,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                    )
                    Text(
                        text = team.name,
                        fontSize = 11.sp,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        // Handle teams button action here
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(1.dp, Color.Gray),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .width(120.dp)
                ) {
                    Text(
                        text = "${if (team.userIdsAndRoles?.isEmpty() == true) 0 else team.userIdsAndRoles?.size} / 10", // Example for team count
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // View Profile Button with icon
                // This should call the TeamProfile.kt
//                IconButton(
//                    onClick = onView
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Visibility,
//                        contentDescription = "View Team Profile",
//                        modifier = Modifier.size(32.dp)
//                    )
//                }

            }

//            Spacer(modifier = Modifier.height(8.dp))

            // Second Row: Open and Join Buttons
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                // Open Button
//                Button(
//                    onClick = {
//                        // Handle open action
//                    },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color.White,
//                        contentColor = Color.Black
//                    ),
//                    border = BorderStroke(1.dp, Color.Gray),
//                    shape = RoundedCornerShape(8.dp),
//                    modifier = Modifier
//                        .height(36.dp)
//                        .weight(1f)
//                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_open_book),
//                        contentDescription = "Open",
//                        modifier = Modifier.size(20.dp)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(text = "Open")
//                }
//
//                Spacer(modifier = Modifier.width(8.dp))
//
//                // Join Button
//                Button(
//                    onClick = {
//                        // Handle join action
//                    },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color.White,
//                        contentColor = Color.Black
//                    ),
//                    border = BorderStroke(1.dp, Color.Gray),
//                    shape = RoundedCornerShape(8.dp),
//                    modifier = Modifier
//                        .height(36.dp)
//                        .weight(2f)
//                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_square_plus),
//                        contentDescription = "Join",
//                        modifier = Modifier.size(20.dp)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(text = "Join")
//                }
//            }
        }
    }

//    Spacer(modifier = Modifier.height(16.dp))
}
