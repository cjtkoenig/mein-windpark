package app.core.model

data class WindPark(
    val id: String,
    val name: String,
    val municipalityId: String,
    val municipalityName: String,
    val latitude: Double,
    val longitude: Double,
    val turbineCount: Int,
    val installedCapacityKw: Long?,
    val isFavorite: Boolean = false,
    val sourceName: String = "",
    val sourceUrl: String = "",
    val sourceUpdatedAt: String = "",
    val dataQuality: String = "",
) {
    val municipality: String get() = municipalityName
}

