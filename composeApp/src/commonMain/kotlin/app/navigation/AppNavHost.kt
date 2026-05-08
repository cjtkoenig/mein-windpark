package app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import app.feature.detail.ParkDetailScreen
import app.feature.faq.FaqScreen
import app.feature.map.MapScreen
import app.feature.search.SearchScreen

@Composable
fun AppNavHost() {
    var currentRoute: Route by remember { mutableStateOf(Route.Map) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                topLevelRoutes.forEach { route ->
                    NavigationBarItem(
                        selected = currentRoute.isSameTopLevelRoute(route),
                        onClick = { currentRoute = route },
                        icon = { Text(route.navIconLabel()) },
                        label = { Text(route.title) },
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (val route = currentRoute) {
                Route.Map -> MapScreen(
                    onParkSelected = { parkId -> currentRoute = Route.Detail(parkId) },
                )

                Route.Search -> SearchScreen(
                    onParkSelected = { parkId -> currentRoute = Route.Detail(parkId) },
                )

                Route.Faq -> FaqScreen()
                is Route.Detail -> ParkDetailScreen(
                    parkId = route.parkId,
                    onBack = { currentRoute = Route.Map },
                )
            }
        }
    }
}

private fun Route.isSameTopLevelRoute(other: Route): Boolean =
    this::class == other::class

private fun Route.navIconLabel(): String = when (this) {
    Route.Map -> "K"
    Route.Search -> "S"
    Route.Faq -> "?"
    is Route.Detail -> ""
}
