package app.feature.search

data class SearchUiState(
    val query: String = "",
    val recentParkIds: List<String> = emptyList(),
)
