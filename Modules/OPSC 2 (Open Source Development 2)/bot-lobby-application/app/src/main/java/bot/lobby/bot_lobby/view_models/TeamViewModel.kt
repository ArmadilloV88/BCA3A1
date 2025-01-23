package bot.lobby.bot_lobby.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bot.lobby.bot_lobby.api.RetrofitInstance
import bot.lobby.bot_lobby.models.FetchResponse
import bot.lobby.bot_lobby.models.Team
import bot.lobby.bot_lobby.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

object TeamViewModel : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _searchError: MutableStateFlow<String?> = MutableStateFlow(null)
    val searchError: StateFlow<String?> = _searchError.asStateFlow()

//    private val _searchedTeams: MutableStateFlow<List<Team>?> = MutableStateFlow(null)
//    val searchedTeams: StateFlow<List<Team>?> = _searchedTeams.asStateFlow()

    private val _refreshSearch: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val searchedTeams = combine(_searchQuery, _refreshSearch) { searchQuery, refreshSearch ->
        if (refreshSearch) {
            _refreshSearch.value = false
        }

        _isSearching.value = true

        // The following query was adapted from stackoverflow.com
        // Author: bgs (https://stackoverflow.com/users/2298058/bgs)
        // Link: https://stackoverflow.com/questions/17322228/check-if-a-column-contains-text-using-sql
        // Create a query string that will be used to search for all teams based on their ids
        val usernameQuery = "like.*${searchQuery}*"
        val isPublicQuery = "eq.true"


        // Fetch data
        val response: Any

        if (searchQuery.isNotEmpty()) {
            try {
                response = RetrofitInstance.TeamApi.getTeamsByName(
                    RetrofitInstance.apiKey,
                    name = usernameQuery,
                    isPublic = isPublicQuery
                )

                _isSearching.value = false
                response.body()?.toList()

            } catch (exception: Exception) {
                _searchError.value = exception.message.toString()
                Log.i("ERROR!", exception.message.toString())

                _isSearching.value = false
                emptyList()
            }
        } else {
            _isSearching.value = false
            emptyList()
        }

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun refreshSearch() {
        _refreshSearch.value = true
    }


//    fun getAllTeams() {
//        viewModelScope.launch {
//            try {
//                val response = RetrofitInstance.TeamApi.getTeams(RetrofitInstance.apiKey)
//                val body = response.body()
//                if (body != null) {
//                    _teamData.value = response.body()!!
//                }
//            } catch (exception: Exception) {
//                Log.i("ERROR!", exception.message.toString())
//            }
//        }
//    }

    fun getTeam(teamId: UUID, callback: (team: Team) -> Unit = {}) {
        Log.i("TEAM ID", teamId.toString())


        viewModelScope.launch {
            try {
                val response =
                    RetrofitInstance.TeamApi.getTeams(RetrofitInstance.apiKey, id = "eq.$teamId")
                val body = response.body()
                Log.i("BODY", body.toString())

                if (body != null) {
                    callback(body.first())
                }
            } catch (exception: Exception) {
                Log.i("GET TEAM ERROR", exception.message.toString())
            }
        }
    }

    suspend fun getUsersTeams(user: User): FetchResponse<List<Team>> {
        var teams: List<Team> = listOf()
        var errorMessage: String? = null

//        viewModelScope.launch {
        try {
            // Create a query string that will be used to search for all teams based on their ids
            var queryString = "in.("

            user.teamIds?.forEach { id ->
                queryString += "$id"

                queryString += if (user.teamIds!!.indexOf(id) == (user.teamIds!!.size - 1)) ")" else ","
            }


            // Fetch data
            val response =
                RetrofitInstance.TeamApi.getTeams(key = RetrofitInstance.apiKey, id = queryString)
            val body = response.body()
            if (body != null) {
                teams = body
            }
        } catch (exception: Exception) {
            errorMessage = exception.message.toString()
            Log.i("ERROR!", exception.message.toString())
        }
//        }

        return FetchResponse(teams, errorMessage)
    }


    fun createTeam(newTeam: Team, callback: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.TeamApi.createTeam(RetrofitInstance.apiKey, newTeam)

                val body = response.body()
                Log.i("RES!", response.toString())
                if (body != null) {
                    Log.i("TEAM DATA!", body.toString())

                    callback()
                }

            } catch (exception: Exception) {
                Log.i("HMMM!", exception.message.toString())
            }
        }

        refreshSearch()
    }

    fun updateTeam(team: Team) {
        viewModelScope.launch {
            try {
                RetrofitInstance.TeamApi.updateTeam(
                    RetrofitInstance.apiKey,
                    "eq.${team.id}",
                    team
                )
            } catch (exception: Exception) {
                Log.i("UPDATE TEAM ERROR", exception.message.toString())
            }
        }

        refreshSearch()
    }

    fun joinTeam(team: Team) {
        viewModelScope.launch {
            try {
                val res = RetrofitInstance.TeamApi.updateTeam(
                    RetrofitInstance.apiKey,
                    "eq.${team.id}",
                    team
                )
            } catch (exception: Exception) {
                Log.i("ERROR!", exception.message.toString())
            }
        }
    }

    fun leaveTeam(team: Team) {
        viewModelScope.launch {
            try {
                val res = RetrofitInstance.TeamApi.updateTeam(
                    RetrofitInstance.apiKey,
                    "eq.${team.id}",
                    team
                )
            } catch (exception: Exception) {
                Log.i("ERROR!", exception.message.toString())
            }
        }
    }

    fun deleteTeam(teamId: UUID) {
        viewModelScope.launch {
            try {
                RetrofitInstance.TeamApi.deleteTeam(
                    RetrofitInstance.apiKey,
                    "eq.$teamId",
                )
            } catch (exception: Exception) {
                Log.i("ERROR FOR!", exception.message.toString())
            }
        }

        refreshSearch()
    }


    fun clearSearchQuery() {
        _searchQuery.value = ""
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearData() {
        _searchQuery.value = ""

        _isSearching.value = false

        _searchError.value = null

//        _searchedTeams.value = null
    }
}


//import android.util.Log
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.bot_lobby.models.Member
//import com.example.bot_lobby.models.Team
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.SharingStarted
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.combine
//import kotlinx.coroutines.flow.stateIn

// Define the ViewModel to handle team data
//class TeamViewModel : ViewModel() {
//
//    // StateFlow for the search query
//    private val _searchQuery = MutableStateFlow("")
//    val searchQuery: StateFlow<String> = _searchQuery
//
//    // StateFlow for the list of teams
//    private val _teams = MutableStateFlow<List<Team>>(emptyList())
//    val teams: StateFlow<List<Team>> = _teams
//
//    // StateFlow to filter teams based on searchQuery and teams
//    val filteredTeams: StateFlow<List<Team>> = combine(_searchQuery, _teams) { query, teamList ->
//        if (query.isEmpty()) {
//            teamList
//        } else {
//            teamList.filter {
//                it.teamtag.contains(query, ignoreCase = true) ||
//                        it.teamname.contains(
//                            query,
//                            ignoreCase = true
//                        ) ||  // Include search by teamname
//                        it.members.any { member ->
//                            member.playertag.contains(
//                                query,
//                                ignoreCase = true
//                            )
//                        }
//            }
//        }
//    }.stateIn(
//        viewModelScope, // Use viewModelScope for lifecycle-awareness
//        SharingStarted.Lazily, // Sharing strategy
//        emptyList() // Initial value
//    )
//
//    // Initialize the ViewModel with default data
//    init {
//        loadInitialData()
//        Log.d("TeamViewModel", "ViewModel initialized with data")
//    }
//
//    // Load initial data (teams and members)
//    private fun loadInitialData() {
//        val initialTeams = listOf(
//            Team(
//                teamtag = "Team Tag 1",
//                teamname = "Alpha Team",
//                members = listOf(
//                    Member(playertag = "Player Tag 1", role = "Leader"),
//                    Member(playertag = "Player Tag 2", role = "Member"),
//                    Member(playertag = "Player Tag 3", role = "Member"),
//                    Member(playertag = "Player Tag 4", role = "Member")
//                ),
//                isPublic = true
//            ),
//            Team(
//                teamtag = "Team Tag 2",
//                teamname = "Bravo Team",
//                members = listOf(
//                    Member(playertag = "Player Tag 4", role = "Leader"),
//                    Member(playertag = "Player Tag 6", role = "Member")
//                ),
//                isPublic = false
//            ),
//            Team(
//                teamtag = "Team Tag 3",
//                teamname = "Charlie Team",
//                members = listOf(
//                    Member(playertag = "Player Tag 7", role = "Leader"),
//                    Member(playertag = "Player Tag 8", role = "Member")
//                ),
//                isPublic = true
//            ),
//            Team(
//                teamtag = "Team Tag 4",
//                teamname = "Delta Team",
//                members = listOf(
//                    Member(playertag = "Player Tag 9", role = "Leader"),
//                    Member(playertag = "Player Tag 10", role = "Member")
//                ),
//                isPublic = false
//            )
//        )
//        _teams.value = initialTeams
//        Log.d("TeamViewModel", "Loaded initial teams: ${initialTeams.size}")
//    }
//
//    // Update the search query
//    fun updateSearchQuery(query: String) {
//        _searchQuery.value = query
//        Log.d("TeamViewModel", "Search query updated to: $query")
//    }
//
//    // Reload data (for manual reload testing)
//    fun reloadData() {
//        loadInitialData()
//        Log.d("TeamViewModel", "Data reloaded")
//    }
//}
