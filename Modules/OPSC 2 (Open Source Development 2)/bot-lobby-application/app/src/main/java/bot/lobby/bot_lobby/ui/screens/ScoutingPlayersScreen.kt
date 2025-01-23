package bot.lobby.bot_lobby.ui.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import bot.lobby.bot_lobby.models.User
import bot.lobby.bot_lobby.ui.composables.FullScreenModal
import bot.lobby.bot_lobby.ui.composables.PlayerListItem
import bot.lobby.bot_lobby.ui.composables.PlayerProfile
import bot.lobby.bot_lobby.ui.theme.BlueStandard
import bot.lobby.bot_lobby.view_models.SessionViewModel
import bot.lobby.bot_lobby.view_models.TeamViewModel
import bot.lobby.bot_lobby.view_models.UserViewModel
import bot.lobby.bot_lobby.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ScoutingPlayersScreen(
    userViewModel: UserViewModel = viewModel(), // Initialize PlayerViewModel
    teamViewModel: TeamViewModel = viewModel() // Initialize TeamViewModel
) {
    // Collect searchQuery and filtered players from the PlayerViewModel
    val searchQuery by UserViewModel.searchQuery.collectAsState()
    val isSearching by UserViewModel.isSearching.collectAsState()
    val searchedUsers by UserViewModel.searchedUsers.collectAsState()
    val searchError by UserViewModel.searchError.collectAsState()

    val sessionViewModel = SessionViewModel(LocalContext.current)
    val session by sessionViewModel.session.collectAsState()

    // Focus manager for clearing the focus when search is triggered
    val focusManager = LocalFocusManager.current
    var isDialogOpen by remember { mutableStateOf(false) }
    var userToView by remember { mutableStateOf<User?>(null) }


    // Main Column layout to structure the screen
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize()
            .padding(2.dp)
    ) {

        // Row for the Search Bar, Search Icon, and Refresh Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 0.dp, bottom = 4.dp), // Adjusts padding for a tight layout
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search TextField for searching players by tag

            TextField(
                value = searchQuery, // Binds the searchQuery state
                onValueChange = { userViewModel.updateSearchQuery(it) }, // Updates searchQuery state
                placeholder = { Text(stringResource(id = R.string.search_player_tag)) }, // Placeholder text for search field
                modifier = Modifier
                    .weight(1f) // Expands the search field to take full available width
                    .padding(end = 4.dp),
                shape = RoundedCornerShape(16.dp), // Rounded corner for aesthetic
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search), // Search keyboard action
                keyboardActions = KeyboardActions(
                    onSearch = { focusManager.clearFocus() } // Clears focus when search is triggered
                ),
                singleLine = true, // Restrict search bar to one line
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = BlueStandard,  // Custom theme colors
                    unfocusedContainerColor = BlueStandard,
                    cursorColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedPlaceholderColor = Color.White,
                    unfocusedPlaceholderColor = Color.White
                )
            )

            // Search IconButton to trigger search
            IconButton(onClick = {
                focusManager.clearFocus() // Clears focus when search button is clicked
                Log.d("PlayersTab", "Search triggered")

//                userViewModel.searchForUsers()
                UserViewModel.clearSearchQuery()
            }, enabled = searchQuery.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = stringResource(id = R.string.search_clear) // Describes the search button
                )
            }

            // Refresh IconButton to clear search and reload the data
//            IconButton(onClick = {
//                userViewModel.clearSearchQuery() // Clears the search query
//            }) {
//                Icon(
//                    imageVector = Icons.Default.Refresh,
//                    contentDescription = "Refresh Icon" // Describes the refresh button
//                )
//            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Display the number of players found after filtering
        Text(
            "Players found: ${if (searchedUsers.isNullOrEmpty()) 0 else searchedUsers?.size}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))


        // Player List within LazyColumn for scrolling through players
        if (isSearching) {
            Text(stringResource(id = R.string.searching))
        } else if (searchQuery.isEmpty()) {
            Text(stringResource(id = R.string.enter_player_name))
        } else if (!searchError.isNullOrEmpty()) {
            searchError?.let { Text(it) }
        } else if (searchedUsers!!.isEmpty()) {
            Text(stringResource(id = R.string.no_players_found))
        } else {
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(searchedUsers!!) { user ->
                        // Pass navController to PlayerListItem to enable navigation
                        PlayerListItem(
                            user = user,
                            teams = session?.usersTeams,
                            canView = true,
                            onView = {
                                isDialogOpen = true
                                userToView = user
                            })
                    }
                }
            }
        }
    }


    if (isDialogOpen) {
        FullScreenModal(onClose = {
            isDialogOpen = false
            userToView = null
        }) {
            PlayerProfile(user = userToView!!)
        }
    }
}
