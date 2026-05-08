package app.core.model

data class WindPark(
    val id: String,
    val name: String,
    val municipality: String,
    val isFavorite: Boolean = false,
)
