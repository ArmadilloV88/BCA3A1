package bot.lobby.bot_lobby.ui.composables

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.PublicOff
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import bot.lobby.bot_lobby.models.Team
import bot.lobby.bot_lobby.models.User
import bot.lobby.bot_lobby.view_models.SessionViewModel
import bot.lobby.bot_lobby.view_models.TeamViewModel
import bot.lobby.bot_lobby.view_models.UserViewModel
import bot.lobby.bot_lobby.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerProfile(
    user: User,
    isPersonalProfile: Boolean = false,
) {
//    val userViewModel = UserViewModel()
//    val teamViewModel = TeamViewModel()

    val context = LocalContext.current

    val sessionViewModel = viewModel { SessionViewModel(context) }

    var teams by remember { mutableStateOf<List<Team>?>(null) }
    var error: String? by remember { mutableStateOf(null) }

//    var teamsUsers by remember { mutableStateOf<FetchResponse<List<User>>?>(null) }
    var isLoading by remember { mutableStateOf(true) }


    // State to manage the description field, initialized from the player's teams
    var userTag by remember { mutableStateOf(user.username) }
    var userBio by remember { mutableStateOf(user.bio) }
    var userIsLFT by remember { mutableStateOf(user.isLFT) }
    var userIsPublic by remember { mutableStateOf(user.isPublic) }

    if (!isPersonalProfile) {
        LaunchedEffect(true) {
            isLoading = true

            val response = TeamViewModel.getUsersTeams(user)

            if (response.errors.isNullOrEmpty()) {
                teams = response.data
            } else {
                error = response.errors
            }

            isLoading = false
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp),  // No padding around the entire column
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (!isPersonalProfile) {
            Spacer(modifier = Modifier.height(20.dp))
        }


        // Row for player image, tag, and action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()  // Row fills the available width
                .padding(0.dp),  // No internal padding
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Player image section
            Image(
                painter = painterResource(id = R.drawable.ic_team_tag),  // Sample image resource
                contentDescription = R.string.player_image.toString(),
                modifier = Modifier
                    .width(130.dp)
                    .height(130.dp)
                    .clip(RoundedCornerShape(16.dp))  // Rounded corners for the image
                    .border(
                        1.dp,
                        Color.Transparent,
                        RoundedCornerShape(16.dp)
                    ),  // Border for the image
                contentScale = ContentScale.Crop  // Cropped image to maintain aspect ratio
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Column for player information and buttons
            Column(
                modifier = Modifier
                    .weight(1f)  // Take up available width after image
                    .align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.spacedBy(8.dp)  // Space between elements
            ) {
                // Row with player tag and edit button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Display player's tag
//                    OutlinedTextField(
//                        value = userTag,
//                        onValueChange = { userTag = it },
//                        label = { Text("Tag") },
//                        fontWeight = FontWeight.Bold,
//                        style = MaterialTheme.typography.titleLarge
//                    )

                    OutlinedTextField(
                        value = userTag,
                        readOnly = !isPersonalProfile,
                        onValueChange = { userTag = it },
                        label = { R.string.user_tag },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                }

                // Row with LFT (Looking for Team) and Public buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // LFT Button
                    AssistChip(
                        onClick = { if (isPersonalProfile) userIsLFT = !userIsLFT },
                        label = { if (userIsLFT) Text(text = stringResource(id = R.string.lft)) else Text(text = stringResource(id = R.string.lft)) },
                        trailingIcon = {
                            if (userIsLFT) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = R.string.is_lft.toString(),
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = R.string.is_not_lft.toString(),
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }


                        },
                        modifier = Modifier
                            .weight(1f)
//                        modifier = Modifier.width(110.dp)
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color.White,
//                            contentColor = Color.Black
//                        ),
//                        border = BorderStroke(1.dp, Color.Gray),
//                        shape = RoundedCornerShape(8.dp)
                    )

                    // Public Button
                    AssistChip(
                        onClick = { if (isPersonalProfile) userIsPublic = !userIsPublic },
                        label = { if (userIsPublic) Text(text = stringResource(id = R.string.public1)) else Text(text = stringResource(id = R.string.private1)) },
                        trailingIcon = {
                            if (userIsPublic) {
                                Icon(
                                    imageVector = Icons.Default.Public,
                                    contentDescription = R.string.user_is_public.toString(),
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.PublicOff,
                                    contentDescription = R.string.user_is_private.toString(),
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        modifier = Modifier.weight(1f)
//                        modifier = Modifier.width(110.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color.White,
//                            contentColor = Color.Black
//                        ),
//                        border = BorderStroke(1.dp, Color.Gray),
//                        shape = RoundedCornerShape(8.dp)
                    )
                }

                // Invite Button for sending team invites
//                Button(
//                    onClick = { /* Handle Invite button click */ },
//                    modifier = Modifier.width(250.dp),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color.White,
//                        contentColor = Color.Black
//                    ),
//                    border = BorderStroke(1.dp, Color.Gray),
//                    shape = RoundedCornerShape(8.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Email,
//                        contentDescription = null,
//                        modifier = Modifier.size(16.dp)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text("Invite", fontSize = 12.sp)
//                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // TextField for player description input
        OutlinedTextField(
            label = { Text(R.string.bio.toString()) },
            readOnly = !isPersonalProfile,
            value = if (userBio.isNullOrEmpty()) "" else userBio!!,
            onValueChange = { newDesc -> userBio = newDesc },  // Update description state
            placeholder = { Text(R.string.enter_player_description.toString()) },  // Placeholder text
            modifier = Modifier
                .fillMaxWidth(),
//                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))  // Border styling
//                .background(Color.White),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),  // Keyboard options
            singleLine = false,
            maxLines = 3,
//            shape = RoundedCornerShape(8.dp),
//            colors = TextFieldDefaults.colors(
//                focusedContainerColor = Color.White,
//                unfocusedContainerColor = Color.White,
//                cursorColor = Color.Black,
//                focusedIndicatorColor = Color.Transparent,
//                unfocusedIndicatorColor = Color.Transparent,
//                disabledIndicatorColor = Color.Transparent
//            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isPersonalProfile) {
            Button(
                onClick = {
                    val updatedUser = User(
                        id = user.id,
                        role = user.role,
                        bio = userBio,
                        username = userTag,
                        password = user.password,
                        biometrics = user.biometrics,
                        teamIds = user.teamIds,
                        isPublic = userIsPublic,
                        isLFT = userIsLFT
                    )


                    sessionViewModel.updateUsersDetails(updatedUser)

                    UserViewModel.updateUser(updatedUser)

                    Toast.makeText(context, R.string.details_saved_successfully, Toast.LENGTH_SHORT)
                        .show()  // Show a confirmation toast
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.SaveAlt, contentDescription = stringResource(id = R.string.save_changes))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(id = R.string.save_changes), style = MaterialTheme.typography.bodyLarge)
            }
        }


        // TODO: NEED TO ADD TEAMS!
        // TODO: NEED TO ADD INVITE BUTTON BACK

        Spacer(modifier = Modifier.height(8.dp))


        if (!isPersonalProfile) {
            Spacer(Modifier.height(25.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.teams),
                    color = Color.Black,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "${if (user.teamIds.isNullOrEmpty()) 0 else user.teamIds!!.size} / 10",  // Assuming a max of 10 members
                    color = Color.Black,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = Color.Gray, thickness = 1.dp)
            Spacer(Modifier.height(16.dp))

            if (isLoading) {
                Text(stringResource(id = R.string.loading))
            } else if (!error.isNullOrEmpty()) {
                error?.let { Text(it) }
            } else if (teams.isNullOrEmpty()) {
                Text(stringResource(id = R.string.no_teams_found))
            } else {
                Box(
                    modifier = Modifier
                        .weight(1f)
                ) {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(teams!!) { team ->
                            TeamItem(team = team)
                        }
                    }
                }
            }
        }

    }
}
