package app.feature.map

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import app.core.ui.components.ScreenPlaceholder

@Composable
fun MapScreen(
    onParkSelected: (parkId: String) -> Unit,
) {
    ScreenPlaceholder(
        title = "Windparks in deiner Umgebung",
        body = "Hier entsteht die Kartenansicht mit Windparks, Favoriten und Gemeinde-Daten.",
        action = {
            Button(onClick = { onParkSelected("demo-park") }) {
                Text("Demo-Windpark ansehen")
            }
        },
    )
}
