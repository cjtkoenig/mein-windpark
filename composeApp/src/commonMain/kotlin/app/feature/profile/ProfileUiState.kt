package app.feature.profile

data class ProfileUiState(
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val language: String = "Deutsch",
    val aboutTitle: String = "Über WindKlar",
    val aboutText: String = "WindKlar ist eine Bürgerinitiative für Transparenz in der Windenergie. Gemeinsam schaffen wir Vertrauen in erneuerbare Energien.",
    val version: String = "Version 1.0.0",
)
