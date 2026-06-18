package app.feature.profile

data class ProfileUiState(
    val aboutTitle: String = "Über WindKlar",
    val aboutText: String = "WindKlar macht öffentliche Windenergiedaten verständlich: Windparks, Produktion, Klimawirkung, kommunaler Nutzen und Datenqualität werden so erklärt, dass Unsicherheiten sichtbar bleiben.",
    val version: String = "Version 1.0.0 (MVP)",
    val attribution: String = "Marktstammdatenregister (MaStR)",
    val limitations: List<String> = emptyList(),
)
