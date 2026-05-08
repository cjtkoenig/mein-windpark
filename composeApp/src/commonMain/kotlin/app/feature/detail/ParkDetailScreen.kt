package app.feature.detail

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import app.core.ui.components.ScreenPlaceholder

@Composable
fun ParkDetailScreen(
    parkId: String,
    onBack: () -> Unit,
) {
    ScreenPlaceholder(
        title = "Windpark-Details",
        body = "Daten zur Windenergieproduktion fuer Park '$parkId' und die zugehoerige Gemeinde.",
        action = {
            Button(onClick = onBack) {
                Text("Zur Karte")
            }
        },
    )
}
