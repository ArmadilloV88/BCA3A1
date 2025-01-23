package bot.lobby.bot_lobby.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun FullScreenModal(
    onClose: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = { }) {

        Scaffold { innerPadding ->
            Surface(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 25.dp)
            ) {
                Box {
                    Column(Modifier.fillMaxSize()) {
                        content()
                    }


                    Button(
                        onClick = onClose,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter) // Align to bottom-right corner
                            .padding(bottom = 75.dp),
                    ) {
                        Icon(Icons.Default.Close, "Close Modal")
                        Spacer(Modifier.width(4.dp))
                        Text("Close")
                    }
                }

            }
        }
    }
}