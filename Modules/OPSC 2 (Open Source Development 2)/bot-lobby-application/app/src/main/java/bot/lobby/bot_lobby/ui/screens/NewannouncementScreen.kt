package bot.lobby.bot_lobby.ui.screens

import androidx.compose.foundation.layout.*


import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import bot.lobby.bot_lobby.models.Session
import bot.lobby.bot_lobby.models.Team
import bot.lobby.bot_lobby.ui.composables.formFields.Select
import bot.lobby.bot_lobby.view_models.AnnouncementViewModel
import bot.lobby.bot_lobby.view_models.SessionViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import bot.lobby.bot_lobby.models.Announcement
import bot.lobby.bot_lobby.R
import bot.lobby.bot_lobby.ui.composables.formFields.TextField


@Composable
fun NewAnnouncementScreen(
    session: Session,
    viewModel: AnnouncementViewModel,
    onCancel: () -> Unit,
    onPostAnnouncement: () -> Unit,
    currentUserId: String // Pass in the currently logged-in user ID
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedTeam: Team? by remember { mutableStateOf(null) }
    val context = LocalContext.current
    val sessionViewModel = viewModel { SessionViewModel(context) }

    var teamDropdownExpanded by remember { mutableStateOf(false) }


    LaunchedEffect(true) {
        sessionViewModel.refreshUsersTeams()
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        Text(
            text = stringResource(id = R.string.new_announcment),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(1.dp))
        Select<Team?>(
            label = stringResource(id =R.string.team),
            value = selectedTeam,
            options = session.usersTeams,
//                itemFormatter = form.category.optionItemFormatter,
            isRequired = true,
            onSelect = {
                if (it != null) {
                    selectedTeam = it
                }


//                teamDropdownExpanded = false
//                    onFormValueChange(
//                        value = it,
//                        form = form,
//                        fieldState = form.category
//                    )
            },
//                hasError = form.category.hasError(),
//                errorText = form.category.errorText,
            placeholderText = stringResource(id =R.string.select_team),
            itemFormatter = { team ->
                team?.tag!!
            }
//                canCreateIfEmpty = true,
//                creationContent = {
//                    var isDialogOpen by rememberSaveable { mutableStateOf(false) }
//
//                    Button(
//                        onClick = { isDialogOpen = true },
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Text("Create Category")
//                    }
//
//                    if (isDialogOpen) {
//                        CategoryCreationDialog(onDismiss = {
//                            isDialogOpen = false
//                        })
//                    }
//                }
        )

//        Spacer(modifier = Modifier.height(1.dp))
        HorizontalDivider(
            thickness = 1.dp,
//            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .align(Alignment.CenterHorizontally)
        )
//        Spacer(modifier = Modifier.height(1.dp))

        TextField(
            value = title,
            label = stringResource(id =R.string.title),
            isRequired = true,
            onChange = {
                title = it
//                onFormValueChange(title, form, form.title)
//                onFormValueChange(
//                    value = it,
//                    form = form,
//                    fieldState = form.description
//                )
            },
//            hasError = form.description.hasError(),
//            errorText = form.description.errorText,
            placeholderText = stringResource(id =R.string.add_title)
        )

//        Text(text = "Title *")
//        BasicTextField(
//            value = title,
//            onValueChange = { title = it },
//            modifier = Modifier
//                .fillMaxWidth()
//                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
//                .padding(8.dp),
//            textStyle = TextStyle(color = Color.Black)
//        )

//        Spacer(modifier = Modifier.height(16.dp))

//        Text(text = "Description *")
//        BasicTextField(
//            value = content,
//            onValueChange = { content = it },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(120.dp)
//                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
//                .padding(8.dp),
//            textStyle = TextStyle(color = Color.Black)
//        )

        TextField(
            value = description,
            label = stringResource(id =R.string.description),
            isRequired = false,
            onChange = {
                description = it
              
//                onFormValueChange(
//                    value = it,
//                    form = form,
//                    fieldState = form.description
//                )
            },
//            hasError = form.description.hasError(),
//            errorText = form.description.errorText,
            placeholderText = stringResource(id =R.string.add_description),
            singleLine = false,
            maxLines = 3,
            useTextArea = true
        )

        Spacer(modifier = Modifier.height(3.dp))

        Column {
            Button(
                enabled = title.isNotEmpty() && description.isNotEmpty() && selectedTeam != null,
                onClick = {

                    session.userLoggedIn.id?.let {

//                            viewModel.saveAnnouncement(
//                                title = title,
//                                body = description,
//                                forTeamId = it.id,
//                                createdByUserId = it1
//                            )

                        viewModel.postAnnouncement(
                            Announcement(
                                title = title,
                                body = description,
                                forTeamId = selectedTeam?.id!!,
                                createdByUserId = it
                            )
                        )


                        onPostAnnouncement()  // Trigger callback to close or refresh the screen
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
            ) {
                Text(stringResource(id =R.string.post_announcement))
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id =R.string.cancel))
            }
        }

    }
}
