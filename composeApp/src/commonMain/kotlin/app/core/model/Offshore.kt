package app.core.model

val offshoreMunicipalityIds = setOf(
    "offshore_north_sea",
    "offshore_baltic_sea",
)

fun String.isOffshoreMunicipalityId(): Boolean =
    this in offshoreMunicipalityIds

fun WindPark.isOffshore(): Boolean =
    municipalityId.isOffshoreMunicipalityId()

fun WindTurbine.isOffshore(): Boolean =
    municipalityId.isOffshoreMunicipalityId()
