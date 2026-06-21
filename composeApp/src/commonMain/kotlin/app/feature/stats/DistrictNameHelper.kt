package app.feature.stats

object DistrictNameHelper {
    private val districtNames = mapOf(
        "01051" to "Dithmarschen",
        "01054" to "Nordfriesland",
        "01055" to "Ostholstein",
        "03452" to "Aurich",
        "03454" to "Emsland",
        "05762" to "Höxter",
        "05774" to "Paderborn",
        "12073" to "Uckermark",
        "13075" to "Vorpommern-Greifswald",
        "13076" to "Ludwigslust-Parchim",
        "14626" to "Görlitz",
    )

    private val stateNames = mapOf(
        "01" to "Schleswig-Holstein",
        "02" to "Hamburg",
        "03" to "Niedersachsen",
        "04" to "Bremen",
        "05" to "Nordrhein-Westfalen",
        "06" to "Hessen",
        "07" to "Rheinland-Pfalz",
        "08" to "Baden-Württemberg",
        "09" to "Bayern",
        "10" to "Saarland",
        "11" to "Berlin",
        "12" to "Brandenburg",
        "13" to "Mecklenburg-Vorpommern",
        "14" to "Sachsen",
        "15" to "Sachsen-Anhalt",
        "16" to "Thüringen",
    )

    fun stateNameFor(districtId: String): String =
        stateNames[districtId.take(2)] ?: "Deutschland"

    fun labelFor(districtId: String, contextLabel: String): String {
        districtNames[districtId]?.let { return it }

        val context = contextLabel
            .trim()
            .takeIf { it.isNotBlank() && it != districtId }
            ?: "AGS-Kreis $districtId"
        return "Region um $context (${stateNameFor(districtId)})"
    }
}
