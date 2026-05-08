package app.feature.detail

data class ParkDetailUiState(
    val parkId: String,
    val name: String = "Demo-Windpark",
    val municipality: String = "Demo-Gemeinde",
)
