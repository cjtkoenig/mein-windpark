package app.core.ui.components

import androidx.compose.runtime.Composable
import platform.CoreLocation.CLLocationManager

@Composable
actual fun rememberLocationPermissionLauncher(onResult: (Boolean) -> Unit): () -> Unit {
    return {
        val manager = CLLocationManager()
        manager.requestWhenInUseAuthorization()
        onResult(true)
    }
}
