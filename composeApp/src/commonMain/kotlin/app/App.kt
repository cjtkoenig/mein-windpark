package app

import androidx.compose.runtime.Composable
import app.core.location.LocationProvider
import app.core.ui.theme.WindklarTheme
import app.data.local.db.AppDatabase
import app.navigation.AppNavHost

@Composable
fun App(database: AppDatabase, locationProvider: LocationProvider) {
    WindklarTheme {
        AppNavHost(database, locationProvider)
    }
}

