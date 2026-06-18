package app.core.model

data class Metric(
    val id: String,
    val subjectType: String,
    val subjectId: String,
    val metricType: String,
    val value: Double?,
    val unit: String,
    val period: String?,
    val sourceName: String,
    val sourceUrl: String,
    val sourceUpdatedAt: String,
    val dataQuality: String,
    val calculationNote: String?,
)
