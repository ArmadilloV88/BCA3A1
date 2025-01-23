package bot.lobby.bot_lobby.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import bot.lobby.bot_lobby.models.Event

@Composable
fun EventItem(event: Event) {
    var expanded by remember { mutableStateOf(false) }  // State to track whether the event details are expanded

    // Create a surface with a border for each event
    Surface(
        modifier = Modifier
            .fillMaxWidth()  // Fill available width
            .padding(vertical = 4.dp),  // Add vertical padding between event items
        border = BorderStroke(1.dp, Color.Gray),  // Add a grey border around the event box
        shape = MaterialTheme.shapes.small,  // Apply small rounded corners to the box
    ) {
        // Column to arrange event content vertically
        Column(
            modifier = Modifier.padding(8.dp)  // Add padding inside the event box
        ) {
            // First row contains the event date and title and a dropdown arrow to expand the details
            Row(
                modifier = Modifier.fillMaxWidth(),  // Fill available width
                verticalAlignment = Alignment.CenterVertically  // Center the content vertically
            ) {
                // Display the event date first, then title
                Text(text = "${event.startDate} - ${event.title}")  // Display start date first and then title
                Spacer(modifier = Modifier.weight(1f))  // Push the arrow to the right side
                // Dropdown arrow to expand/collapse event details
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Expand event details"
                    )  // Dropdown icon
                }
            }

            // If expanded is true, display the event details
            if (expanded) {
                Column {
                    // Display additional event details like description, start date, end date, team, and team members
                    Text("Description: ${event.description}")
                    Text("Start Date: ${event.startDate}")
                    Text("End Date: ${event.endDate}")
                    Text("Team: ${event.team}")
                    Text("Team Members: ${event.teamMembers.joinToString()}")
                }
            }
        }
    }
}