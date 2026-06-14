package app.feature.favorites

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import app.core.ui.components.ScreenPlaceholder

@Composable
fun FavoritesScreen(
    onParkSelected: (parkId: String) -> Unit,
) {
    ScreenPlaceholder(
        title = "Favoriten",
        body = "Hier entsteht die Liste deiner gespeicherten Windparks.",
        action = {
            Button(onClick = { onParkSelected("demo-park") }) {
                Text("Demo-Windpark ansehen")
            }
        },
    )
}
