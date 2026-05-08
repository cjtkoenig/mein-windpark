package app

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.core.ui.theme.MeinWindparkTheme
import app.navigation.AppNavHost

@Composable
@Preview
fun App() {
    MeinWindparkTheme {
        AppNavHost()
    }
}
