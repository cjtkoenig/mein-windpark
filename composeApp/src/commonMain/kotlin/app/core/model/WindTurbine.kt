package app.core.model

data class WindTurbine(
    val id: String,
    val windParkId: String,
    val name: String,
    val municipalityId: String,
    val municipalityName: String,
    val latitude: Double,
    val longitude: Double,
    val installedCapacityKw: Long?,
    val status: String?,
    val turbineType: String?,
    val manufacturer: String?,
    val model: String?,
    val hubHeightM: Double?,
    val rotorDiameterM: Double?,
    val sourceName: String,
    val sourceUrl: String,
    val sourceUpdatedAt: String,
    val dataQuality: String,
)
