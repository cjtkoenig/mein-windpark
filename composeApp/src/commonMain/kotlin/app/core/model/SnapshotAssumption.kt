package app.core.model

data class SnapshotAssumption(
    val id: String,
    val label: String,
    val value: Double,
    val unit: String,
    val sourceName: String,
    val sourceUrl: String,
    val sourceDate: String,
    val calculationNote: String,
)
