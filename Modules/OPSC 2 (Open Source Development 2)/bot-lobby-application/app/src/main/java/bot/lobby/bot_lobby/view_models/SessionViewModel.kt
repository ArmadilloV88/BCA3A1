package bot.lobby.bot_lobby.view_models

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bot.lobby.bot_lobby.db.LocalDatabase
import bot.lobby.bot_lobby.models.Session
import bot.lobby.bot_lobby.models.Team
import bot.lobby.bot_lobby.models.User
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SessionViewModel(context: Context) : ViewModel() {
    private val context by lazy { context }

    val session = LocalDatabase.getDatabase(context).sessionDao.getSession().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000L), null
    )


//    val session =
//        flow<Session> {
////            delay(1000L)
//            LocalDatabase.getDatabase(context).sessionDao.getSession()
//        }
//        .stateIn(
//        viewModelScope, SharingStarted.WhileSubscribed(5000L), null
//    )


    fun upsertSession(session: Session) {
        viewModelScope.launch {
            LocalDatabase.getDatabase(context).sessionDao.upsertSession(session)
        }
    }

    fun updateUsersDetails(updatedUser: User) {
        val updatedSession = Session(
            id = session.value?.id,
            userLoggedIn = updatedUser,
            usersTeams = session.value?.usersTeams ?: emptyList()
        )

//        Log.i("UPDATED USER BIO", updatedUser.bio.toString())

//        updatedSession?.userLoggedIn = updatedUser

        viewModelScope.launch {
            LocalDatabase.getDatabase(context).sessionDao.upsertSession(updatedSession)
        }
    }


    fun addTeamToUser(newTeam: Team, callback: (User?) -> Unit) {
//        _usersTeams.value += newTeam

        val updatedUser = session.value?.userLoggedIn
        val updatedUsersTeams = session.value?.usersTeams?.plus(newTeam)!!

//        val userCopy = session.value?.userLoggedIn
//
//        _userLoggedIn.value = userCopy

        var updatedTeamIds = session.value!!.userLoggedIn.teamIds?.toMutableList()

        if (updatedTeamIds == null) {
            updatedTeamIds = mutableListOf(newTeam.id)
        } else {
            updatedTeamIds += newTeam.id
        }

        updatedUser?.teamIds = updatedTeamIds.toList()


//        session.value?.userLoggedIn?.teamIds = updatedTeamIds.toList()

        val updatedSession = updatedUser?.let {
            Session(
                id = session.value!!.id,
                userLoggedIn = it,
                usersTeams = updatedUsersTeams
            )
        }

        viewModelScope.launch {
            if (updatedSession != null) {
                LocalDatabase.getDatabase(context).sessionDao.upsertSession(updatedSession)
            }
        }

        callback(session.value!!.userLoggedIn)
    }

    fun updateUsersTeam(team: Team) {
        // the following code was adapted from baeldung.com
        // Author: Albert Ache (https://www.baeldung.com/kotlin/author/albertache)
        // Link: https://www.baeldung.com/kotlin/list-mutable-change-element#:~:text=Using%20the%20map()%20Method,value%20we%20wish%20to%20update.
//        _usersTeams.value = _usersTeams.value.map {
//            if (it.id == team.id) {
//                team
//            } else {
//                it
//            }
//        }

        val updatedTeams = session.value?.usersTeams?.map {
            if (it.id == team.id) {
                team
            } else {
                it
            }
        }!!

        val updatedSession = Session(
            id = session.value!!.id,
            userLoggedIn = session.value!!.userLoggedIn,
            usersTeams = updatedTeams
        )

        viewModelScope.launch {
            LocalDatabase.getDatabase(context).sessionDao.upsertSession(updatedSession)
        }

    }


    suspend fun refreshUsersTeams() {
        val announcementViewModel = AnnouncementViewModel(context)

        session.value?.userLoggedIn?.let {
            var onlineProfile: User? = null
            val onlineProfileResponse = UserViewModel.getOnlineProfile(it.id!!)

            if (onlineProfileResponse.errors.isNullOrEmpty()) {
                onlineProfile = onlineProfileResponse.data!!
            }

            // get and save users teams
            var usersTeams = emptyList<Team>()
            val response = onlineProfile?.let { it1 -> TeamViewModel.getUsersTeams(it1) }

            if (response?.errors.isNullOrEmpty() && !(response?.data.isNullOrEmpty())) {
                usersTeams = response?.data!!
            }

            val newSession = Session(
                userLoggedIn = onlineProfile ?: session.value!!.userLoggedIn,
                usersTeams = usersTeams
            )

            upsertSession(newSession)

            newSession.userLoggedIn.teamIds?.forEach {
                announcementViewModel.subscribeToTeamAnnouncements(it)
            }
        }


    }


    fun signOut(
//        userViewModel: UserViewModel,
//        teamViewModel: TeamViewModel,
        callback: (user: User?) -> Unit = {}
    ) {
        UserViewModel.clearData()
        TeamViewModel.clearData()

        val userForCallback = session.value?.userLoggedIn

        viewModelScope.launch {
            session.value?.let { LocalDatabase.getDatabase(context).sessionDao.clearSession(it) }
        }

        callback(userForCallback)
    }


//    fun clearSession(session: Session){
//        viewModelScope.launch {
//            LocalDatabase.getDatabase(context).sessionDao.clearSession()
//        }
//    }

    fun removeTeamFromUser(team: Team, callback: (User?) -> Unit) {
//        _usersTeams.value += newTeam

        val updatedUser = session.value?.userLoggedIn
        val updatedUsersTeams = session.value?.usersTeams?.minus(team)!!

//        val userCopy = session.value?.userLoggedIn
//
//        _userLoggedIn.value = userCopy

        val updatedTeamIds = session.value!!.userLoggedIn.teamIds?.toMutableList()

        if (updatedTeamIds != null) {
            updatedTeamIds -= team.id
            updatedUser?.teamIds = updatedTeamIds.toList()
        }


//            session.value?.userLoggedIn?.teamIds = updatedTeamIds.toList()


        val updatedSession = updatedUser?.let {
            Session(
                id = session.value!!.id,
                userLoggedIn = it,
                usersTeams = updatedUsersTeams
            )
        }

        viewModelScope.launch {
            if (updatedSession != null) {
                LocalDatabase.getDatabase(context).sessionDao.upsertSession(updatedSession)
            }

            callback(updatedSession?.userLoggedIn)
        }


    }

//

//

//

//
//    fun setUsersTeams(teams: List<Team>){
//        _usersTeams.value = teams
//    }
}