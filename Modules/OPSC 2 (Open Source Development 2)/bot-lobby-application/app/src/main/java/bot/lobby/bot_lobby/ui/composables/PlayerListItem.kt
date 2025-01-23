package bot.lobby.bot_lobby.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bot.lobby.bot_lobby.models.Team
import bot.lobby.bot_lobby.models.User
import bot.lobby.bot_lobby.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerListItem(
    user: User,
    teams: List<Team>? = null,
    onView: () -> Unit = {},
    canView: Boolean = false,
    isRoleOwner: Boolean? = null
) {
    val focusRequester = remember { FocusRequester() }
    var expanded by remember { mutableStateOf(false) }
    var selectedTeam by remember { mutableStateOf("") }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                BorderStroke(1.dp, Gray),
                shape = RoundedCornerShape(8.dp)
            )

            .clickable { onView() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
//                .padding(4.dp)
        ) {
            // First Row: Player Icon, Player Name, LFT Button, and View Profile Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_player_tag),
                    contentDescription = "Player Icon",
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = user.username,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // LFT Button
//                user.teamIds?.isNotEmpty()?.let {
//                    LFTButton(
//                        inTeam = it,
//                        onClick = {
//                            // Handle LFT action
//                        }
//                    )
//                }

                Spacer(modifier = Modifier.width(8.dp))


                // View Profile Button
                if (canView) {
                    IconButton(onClick = onView) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "View Profile",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                } else if (isRoleOwner != null && isRoleOwner) { // Role badge
                    AssistChip(
                        onClick = { },
                        label = { Text(stringResource(R.string.team_role_owner)) },
                        // TODO add colors
                        // colors = ChipColors()
                    )
                }

            }

            // Second Row: Team Selection Dropdown and Invite Button
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 8.dp)
//            ) {
//                Box(modifier = Modifier.width(150.dp)) {
//                    ExposedDropdownMenuBox(
//                        expanded = expanded,
//                        onExpandedChange = {
//                            if (user.teamIds?.isEmpty() == true) {
//                                expanded = !expanded
//                            }
//                        }
//                    ) {
//                        val textColor =
//                            if (user.teamIds?.isEmpty() == true) Color.Black else Color.LightGray
//
//                        TextField(
//                            value = "", //if (user.teamIds.isNotEmpty()) user.teamIds.first() else selectedTeam
//                            onValueChange = {},
//                            placeholder = {
//                                Text(
//                                    text = "Team Tag",
//                                    fontSize = 12.sp,
//                                    textAlign = TextAlign.Center
//                                )
//                            },
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(48.dp)
//                                .border(1.dp, Gray, RoundedCornerShape(8.dp))
//                                .focusRequester(focusRequester)
//                                //                                .menuAnchor(type, enabled)
//                                .clickable(enabled = true) {
//                                    if (user.teamIds?.isEmpty() == true) {
//                                        expanded = !expanded
//                                        focusRequester.requestFocus()
//                                    }
//                                },
//                            readOnly = true,
//                            textStyle = TextStyle(fontSize = 12.sp, color = textColor),
//                            colors = TextFieldDefaults.colors(
//                                focusedContainerColor = Color.White,
//                                unfocusedContainerColor = Color.White,
//                                disabledContainerColor = Color.White,
//                                focusedIndicatorColor = Color.Transparent,
//                                unfocusedIndicatorColor = Color.Transparent,
//                                disabledTextColor = Gray
//                            ),
//                            trailingIcon = {
//                                Icon(
//                                    imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
//                                    contentDescription = "Dropdown",
//                                    modifier = Modifier.graphicsLayer {
//                                        alpha = if (user.teamIds?.isEmpty() == true) 1f else 0.3f
//                                    }
//                                )
//                            }
//                        )
//
//                        if (!teams.isNullOrEmpty()) {
//                            // Dropdown menu for selecting team
//                            ExposedDropdownMenu(
//                                expanded = expanded,
//                                onDismissRequest = { expanded = false },
//                                modifier = Modifier
//                                    .background(Color.White)
//                                    .border(1.dp, Gray, RoundedCornerShape(8.dp))
//                            ) {
//                                teams.forEach { team ->
//                                    DropdownMenuItem(
//                                        text = { Text(text = team.tag, fontSize = 14.sp) },
//                                        onClick = {
//                                            selectedTeam = team.tag
//                                            expanded = false
//                                        }
//                                    )
//                                }
//                            }
//                        }
//
//                    }
//                }
//
//                Spacer(modifier = Modifier.width(18.dp))
//
//                // Invite Button
//                user.teamIds?.isEmpty()?.let {
//                    InviteButton(
//                        enabled = it,
//                        onClick = {
//                            if (user.teamIds.isEmpty()) {
//                                // Handle invite action
//                            }
//                        },
//                        modifier = Modifier.width(150.dp)
//                    )
//                }
//            }
//        }
        }

//    Spacer(modifier = Modifier.height(16.dp))
    }

    @Composable
    fun LFTButton(inTeam: Boolean, onClick: () -> Unit) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            border = BorderStroke(1.dp, Gray),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .height(48.dp)
                .width(100.dp)
        ) {
            Icon(
                imageVector = if (inTeam) Icons.Default.Close else Icons.Default.Check,
                contentDescription = if (inTeam) "In Team" else "Looking for Team",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "LFT",
                fontSize = 14.sp
            )
        }
    }

    @Composable
    fun InviteButton(enabled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
        Button(
            onClick = onClick,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = if (enabled) Color.Black else Gray,
                disabledContainerColor = Color.White,
                disabledContentColor = Gray
            ),
            border = BorderStroke(1.dp, Gray),
            shape = RoundedCornerShape(8.dp),
            modifier = modifier
                .height(48.dp)
                .width(180.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MailOutline,
                contentDescription = "Invite to Team",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Invite to Team",
                fontSize = 12.sp
            )
        }
    }
}
