package app.feature.start

data class StartUiState(
    val appName: String = "WindKlar",
    val subtitle: String = "Transparente Windenergie fuer Deutschland",
    val highlights: List<String> = listOf(
        "Entdecken Sie Windparks in Ihrer Naehe",
        "Verstehen Sie erneuerbare Energie",
        "Schuetzen Sie unsere Umwelt",
    ),
    val ctaLabel: String = "Jetzt entdecken",
)
