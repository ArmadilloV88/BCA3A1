package bot.lobby.bot_lobby.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PeopleOutline
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//used by Navigationtab/Teams

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun TeamListItem(
    team: Team,
    onView: () -> Unit
) {
    // Get the current date in xx/xx/xxxx format
    val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                BorderStroke(1.dp, Color.Gray),
                shape = RoundedCornerShape(8.dp)
            ) // Add a grey border with rounded corners
            .clickable { onView() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // First Row: Icon, Team tag and name, Teams Button, and View Profile Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_player_tag),
                    contentDescription = "Player Tag Icon",
                    modifier = Modifier.size(40.dp)
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

                AssistChip(
                    onClick = onView,
                    trailingIcon = {
                        Icon(Icons.Default.PeopleOutline, null)
                    },
                    label = {
                        Text(
                            text = "${team.userIdsAndRoles?.size} / 10", // Example for team count
                            fontSize = 14.sp
                        )
                    },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color.White,
//                        contentColor = Color.Black
//                    ),
//                    border = BorderStroke(1.dp, Color.Gray),
//                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
//                        .height(36.dp)
//                        .width(90.dp)
                )

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

            // Add a horizontal divider
//            Divider(
//                color = Color.Gray,
//                thickness = 1.dp,
//                modifier = Modifier.padding(vertical = 8.dp)
//            )
//
//            // Second Row: "Next announcement:" text and today's date
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                // Text for "Next announcement:"
//                Text(
//                    text = "Next announcement:",
//                    color = Color.Black,
//                    fontSize = 14.sp,
//                    modifier = Modifier.weight(1f) // This will take up the available space on the left
//                )
//
//                Spacer(modifier = Modifier.width(8.dp))
//
//                // Today's date aligned to the right
//                Text(
//                    text = currentDate,
//                    color = Color.Black,
//                    fontSize = 14.sp
//
//                )
//            }
        }
    }
//    Spacer(modifier = Modifier.height(16.dp))
}
