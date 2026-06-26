package app.feature.favorites

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.data.repository.SavedPlacesRepository
import kotlinx.coroutines.launch

import app.core.util.formatGermanNumber

class FavoritesViewModel(private val repository: SavedPlacesRepository) : ViewModel() {
    var uiState: FavoritesUiState by mutableStateOf(FavoritesUiState())
        private set

    private var hasLoaded = false
    private var isLoading = false

    fun loadData(force: Boolean = false) {
        if (isLoading || (!force && hasLoaded)) return
        isLoading = true
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val favs = repository.getFavoriteWindParks()
                val recents = repository.getRecentWindParks(5)
                val favRegionSummaries = repository.getFavoriteRegionSummaries()

                // Batch load all metrics for favorites and recents to avoid N+1 DB queries
                val batchParkIds = (favs.map { it.id } + recents.map { it.id }).distinct()
                val batchMetricsList = repository.getMetricsForParks(batchParkIds)
                val batchMetricsByParkId = batchMetricsList.groupBy { it.subjectId }

                val favUiList = favs.map { park ->
                    val metrics = batchMetricsByParkId[park.id] ?: emptyList()
                    val prodMetric = metrics.firstOrNull { it.metricType == "annual_production" }
                    val co2Metric = metrics.firstOrNull { it.metricType == "co2_savings" }

                    val prodStr = formatProduction(prodMetric?.value)
                    val co2Str = formatCo2(co2Metric?.value)

                    FavoriteParkUiModel(
                        id = park.id,
                        name = park.name,
                        distance = "Gemeinde ${park.municipalityName}",
                        production = prodStr,
                        co2Reduction = co2Str,
                        thumbnail = getThumbnailForId(park.id),
                        isFavorite = park.isFavorite,
                    )
                }

                val favRegionUiList = favRegionSummaries.map { region ->
                    FavoriteRegionUiModel(
                        id = region.regionId,
                        name = region.name,
                        type = region.regionType,
                        typeLabel = when (region.regionType.lowercase()) {
                            "city" -> "Gemeinde"
                            "district" -> "Landkreis"
                            "state" -> "Bundesland"
                            else -> "Region"
                        },
                        production = formatProduction(region.annualProductionKwh),
                        co2Reduction = formatCo2(region.co2SavingsKg),
                        thumbnail = getThumbnailForId(region.regionId),
                        isFavorite = true
                    )
                }

                val recentUiList = recents.map { park ->
                    val metrics = batchMetricsByParkId[park.id] ?: emptyList()
                    val prodMetric = metrics.firstOrNull { it.metricType == "annual_production" }
                    val co2Metric = metrics.firstOrNull { it.metricType == "co2_savings" }

                    val prodStr = formatProduction(prodMetric?.value)
                    val co2Str = formatCo2(co2Metric?.value)

                    FavoriteParkUiModel(
                        id = park.id,
                        name = park.name,
                        distance = "Gemeinde ${park.municipalityName}",
                        production = prodStr,
                        co2Reduction = co2Str,
                        thumbnail = getThumbnailForId(park.id),
                        isFavorite = park.isFavorite,
                    )
                }

                uiState = FavoritesUiState(
                    parks = favUiList,
                    regions = favRegionUiList,
                    recents = recentUiList,
                    isLoading = false,
                    hasLoaded = true,
                )
                hasLoaded = true
            } catch (e: Throwable) {
                uiState = uiState.copy(
                    isLoading = false,
                    hasLoaded = true,
                )
            } finally {
                isLoading = false
            }
        }
    }

    private fun formatProduction(value: Double?): String {
        if (value == null) return "k.A."
        val gwh = value / 1_000_000.0
        return "${formatGermanNumber(gwh, 1)} GWh"
    }

    private fun formatCo2(value: Double?): String {
        if (value == null) return "k.A."
        val tons = value / 1000.0
        return "${formatGermanNumber(tons.toInt())} t"
    }

    private fun getThumbnailForId(id: String): FavoriteParkThumbnail {
        val hash = id.hashCode().coerceAtLeast(0)
        return when (hash % 3) {
            0 -> FavoriteParkThumbnail.Nordsee
            1 -> FavoriteParkThumbnail.Ostsee
            else -> FavoriteParkThumbnail.Alpen
        }
    }
}
