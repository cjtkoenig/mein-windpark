package app

import androidx.compose.runtime.Composable
import app.core.location.LocationProvider
import app.core.ui.theme.WindklarTheme
import app.data.local.source.SourceDatabase
import app.data.local.user.UserDatabase
import app.navigation.AppNavHost

@Composable
fun App(
    sourceDatabase: SourceDatabase,
    userDatabase: UserDatabase,
    locationProvider: LocationProvider,
) {
    WindklarTheme {
        AppNavHost(sourceDatabase, userDatabase, locationProvider)
    }
}
