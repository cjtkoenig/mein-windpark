package app.navigation

sealed interface Route {
    val title: String

    data object Map : Route {
        override val title: String = "Karte"
    }

    data object Search : Route {
        override val title: String = "Suche"
    }

    data object Faq : Route {
        override val title: String = "FAQ"
    }

    data class Detail(val parkId: String) : Route {
        override val title: String = "Windpark"
    }
}

val topLevelRoutes: List<Route> = listOf(
    Route.Map,
    Route.Search,
    Route.Faq,
)
