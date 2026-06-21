package app.feature.map

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.core.model.MapMarkerKind
import app.core.model.MapMarkerUiModel
import app.core.model.WindPark
import app.data.repository.WindParkRepository
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.floor

import app.core.location.LocationProvider

class MapViewModel(
    private val repository: WindParkRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {
    var uiState by mutableStateOf(MapUiState(isLoading = true))
        private set

    private var parkStatuses: Map<String, String> = emptyMap()

    init {
        loadMapData()
    }

    fun loadMapData() {
        viewModelScope.launch {
            try {
                println("MapViewModel: Starting loadMapData...")
                uiState = uiState.copy(isLoading = true)
                
                val allParks = repository.getWindParks()
                println("MapViewModel: Loaded ${allParks.size} wind parks from repository.")
                
                val statusMap = repository.getWindParkStatuses()
                println("MapViewModel: Loaded ${statusMap.size} park statuses from repository.")
                
                parkStatuses = statusMap

                uiState = uiState.copy(
                    isLoading = false,
                    parks = allParks,
                )
                applyFilters()
                println("MapViewModel: loadMapData finished successfully.")
            } catch (e: Throwable) {
                println("MapViewModel ERROR: loadMapData failed!")
                e.printStackTrace()
                uiState = uiState.copy(
                    isLoading = false,
                    parks = emptyList(),
                    filteredParks = emptyList()
                )
            }
        }
    }


    private fun determineParkStatus(statuses: List<String>): String {
        if (statuses.isEmpty()) return "Aktiv"
        val lowerStatuses = statuses.map { it.lowercase() }
        if (lowerStatuses.any { it.contains("bau") || it.contains("errichtung") }) {
            return "Im Bau"
        }
        if (lowerStatuses.any { it.contains("betrieb") || it.contains("aktiv") }) {
            return "Aktiv"
        }
        if (lowerStatuses.any { it.contains("stillgelegt") }) {
            return "Stillgelegt"
        }
        return "Geplant"
    }

    fun setStatusFilter(status: String) {
        uiState = uiState.copy(selectedStatus = status)
        applyFilters()
    }

    fun onQueryChange(newQuery: String) {
        uiState = uiState.copy(searchQuery = newQuery)
        if (newQuery.length >= 2) {
            viewModelScope.launch {
                val results = repository.searchWindParks(newQuery)
                uiState = uiState.copy(
                    searchResults = results,
                    showSearchOverlay = true
                )
            }
        } else {
            uiState = uiState.copy(
                searchResults = emptyList(),
                showSearchOverlay = false
            )
        }
    }

    fun onSearchResultSelected(park: WindPark) {
        uiState = uiState.copy(
            mapCenterLat = park.latitude,
            mapCenterLon = park.longitude,
            zoomLevel = 12.0f,
            selectedPark = park,
            previewSheetState = ParkPreviewSheetState.Expanded,
            selectedStatus = "Alle",
            filteredParks = parksForStatus("Alle"),
            showSearchOverlay = false,
            searchQuery = ""
        )
        applyFilters()
        viewModelScope.launch {
            repository.recordRecentWindPark(park.id)
            val metrics = repository.getMetricsForPark(park.id)
            uiState = uiState.copy(selectedParkMetrics = metrics)
        }
    }

    fun onParkClicked(park: WindPark) {
        uiState = uiState.copy(
            selectedPark = park,
            previewSheetState = ParkPreviewSheetState.Expanded
        )
        viewModelScope.launch {
            repository.recordRecentWindPark(park.id)
            val metrics = repository.getMetricsForPark(park.id)
            uiState = uiState.copy(selectedParkMetrics = metrics)
        }
    }

    fun onParkClickedById(parkId: String) {
        val park = uiState.parks.firstOrNull { it.id == parkId } ?: return
        onParkClicked(park)
    }

    fun submitDataHint(
        category: String,
        confidence: String,
        description: String,
        suggestedValue: String?,
        latitude: Double,
        longitude: Double,
        windParkId: String?,
        municipalityId: String?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.submitDataHint(
                category = category,
                confidence = confidence,
                description = description,
                status = "ready_for_review",
                windTurbineId = null,
                windParkId = windParkId,
                municipalityId = municipalityId,
                latitude = latitude,
                longitude = longitude,
                suggestedValue = suggestedValue,
                imageUri = null
            )
            onSuccess()
        }
    }

    fun startPinPlacement() {
        uiState = uiState.copy(
            isPinPlacementMode = true,
            placementMarkerLat = uiState.mapCenterLat,
            placementMarkerLon = uiState.mapCenterLon,
            selectedPark = null,
            selectedParkMetrics = emptyList()
        )
        applyFilters()
    }

    fun updatePlacementPinLocation(lat: Double, lon: Double) {
        uiState = uiState.copy(
            placementMarkerLat = lat,
            placementMarkerLon = lon
        )
        applyFilters()
    }

    fun cancelPinPlacement() {
        uiState = uiState.copy(isPinPlacementMode = false)
        applyFilters()
    }

    fun confirmPinPlacement(onConfirm: (Double, Double) -> Unit) {
        val lat = uiState.placementMarkerLat
        val lon = uiState.placementMarkerLon
        uiState = uiState.copy(isPinPlacementMode = false)
        applyFilters()
        onConfirm(lat, lon)
    }



    fun expandPreview() {
        if (uiState.selectedPark != null) {
            uiState = uiState.copy(previewSheetState = ParkPreviewSheetState.Expanded)
        }
    }

    fun minimizePreview() {
        if (uiState.selectedPark != null) {
            uiState = uiState.copy(previewSheetState = ParkPreviewSheetState.Minimized)
        }
    }

    fun dismissPreview() {
        uiState = uiState.copy(
            selectedPark = null,
            previewSheetState = ParkPreviewSheetState.Expanded,
            selectedParkMetrics = emptyList()
        )
    }

    fun centerOnLocation(lat: Double, lon: Double) {
        uiState = uiState.copy(
            mapCenterLat = lat,
            mapCenterLon = lon,
            zoomLevel = 10.0f
        )
        applyFilters()
    }

    fun centerOnUserLocation(onPermissionRequired: () -> Unit, onError: (String) -> Unit) {
        if (!locationProvider.hasPermission()) {
            onPermissionRequired()
            return
        }
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true)
                val location = locationProvider.getCurrentLocation()
                uiState = uiState.copy(isLoading = false)
                if (location != null) {
                    centerOnLocation(location.first, location.second)
                } else {
                    onError("Standort konnte nicht ermittelt werden.")
                }
            } catch (e: Throwable) {
                uiState = uiState.copy(isLoading = false)
                onError("Fehler bei der Ortung: ${e.message ?: e.toString()}")
            }
        }
    }

    fun onZoomChanged(zoom: Float) {
        uiState = uiState.copy(zoomLevel = zoom.coerceIn(5.0f, 18.0f))
        applyFilters()
    }

    fun onClusterClicked(lat: Double, lon: Double) {
        uiState = uiState.copy(
            mapCenterLat = lat,
            mapCenterLon = lon,
            zoomLevel = (uiState.zoomLevel + 2.0f).coerceIn(5.0f, 18.0f),
            selectedPark = null,
            selectedParkMetrics = emptyList(),
            previewSheetState = ParkPreviewSheetState.Expanded,
        )
        applyFilters()
    }

    fun onMapMoved(lat: Double, lon: Double, zoom: Float) {
        if (abs(uiState.mapCenterLat - lat) > 0.0001 || 
            abs(uiState.mapCenterLon - lon) > 0.0001 || 
            abs(uiState.zoomLevel - zoom) > 0.1) {
            uiState = uiState.copy(
                mapCenterLat = lat,
                mapCenterLon = lon,
                zoomLevel = zoom.coerceIn(5.0f, 18.0f)
            )
            applyFilters()
        }
    }

    fun onPanChanged(deltaLat: Double, deltaLon: Double) {
        uiState = uiState.copy(
            mapCenterLat = uiState.mapCenterLat + deltaLat,
            mapCenterLon = uiState.mapCenterLon + deltaLon
        )
    }

    private var allTurbines: List<app.core.model.WindTurbine>? = null

    private fun loadTurbinesAndApplyFilters(currentStatus: String, filteredParks: List<WindPark>) {
        if (allTurbines != null) {
            val filteredTurbines = filterTurbines(allTurbines!!, currentStatus)
            uiState = uiState.copy(
                filteredParks = filteredParks,
                mapMarkers = turbinesToMarkers(filteredTurbines)
            )
            return
        }
        viewModelScope.launch {
            try {
                val turbines = repository.getAllWindTurbines()
                allTurbines = turbines
                val filteredTurbines = filterTurbines(turbines, currentStatus)
                uiState = uiState.copy(
                    filteredParks = filteredParks,
                    mapMarkers = turbinesToMarkers(filteredTurbines)
                )
            } catch (e: Throwable) {
                e.printStackTrace()
                uiState = uiState.copy(
                    filteredParks = filteredParks,
                    mapMarkers = emptyList()
                )
            }
        }
    }

    private fun filterTurbines(turbines: List<app.core.model.WindTurbine>, statusFilter: String): List<app.core.model.WindTurbine> {
        if (statusFilter == "Alle") return turbines
        return turbines.filter { determineTurbineStatus(it.status) == statusFilter }
    }

    private fun determineTurbineStatus(status: String?): String {
        if (status == null) return "Aktiv"
        val lower = status.lowercase()
        if (lower.contains("bau") || lower.contains("errichtung")) return "Im Bau"
        if (lower.contains("betrieb") || lower.contains("aktiv")) return "Aktiv"
        if (lower.contains("stillgelegt")) return "Stillgelegt"
        return "Geplant"
    }

    private fun turbinesToMarkers(turbines: List<app.core.model.WindTurbine>): List<MapMarkerUiModel> {
        return turbines.map { turbine ->
            MapMarkerUiModel(
                id = turbine.id,
                latitude = turbine.latitude,
                longitude = turbine.longitude,
                kind = MapMarkerKind.Turbine,
                count = 1,
                parkId = turbine.windParkId
            )
        }
    }

    private fun applyFilters() {
        if (uiState.isPinPlacementMode) {
            uiState = uiState.copy(
                mapMarkers = listOf(
                    MapMarkerUiModel(
                        id = "placement_pin",
                        latitude = uiState.placementMarkerLat,
                        longitude = uiState.placementMarkerLon,
                        kind = MapMarkerKind.PlacementPin,
                        count = 1
                    )
                )
            )
            return
        }

        val currentStatus = uiState.selectedStatus
        val filteredParks = parksForStatus(currentStatus)
        if (uiState.zoomLevel > 14.0f) {
            loadTurbinesAndApplyFilters(currentStatus, filteredParks)
        } else {
            uiState = uiState.copy(
                filteredParks = filteredParks,
                mapMarkers = markersForZoom(filteredParks, uiState.zoomLevel)
            )
        }
    }

    private fun parksForStatus(status: String): List<WindPark> =
        if (status == "Alle") {
            uiState.parks
        } else {
            uiState.parks.filter { park -> statusForPark(park.id) == status }
        }

    private fun statusForPark(parkId: String): String =
        parkStatuses[parkId] ?: "Aktiv"

    private fun markersForZoom(parks: List<WindPark>, zoom: Float): List<MapMarkerUiModel> {
        val gridSize = when {
            zoom < 6.5f -> 1.5
            zoom < 7.5f -> 1.0
            zoom < 8.5f -> 0.65
            zoom < 9.5f -> 0.4
            zoom < 10.25f -> 0.22
            else -> null
        }

        if (gridSize == null) {
            return parks.map { park ->
                MapMarkerUiModel(
                    id = park.id,
                    latitude = park.latitude,
                    longitude = park.longitude,
                    kind = MapMarkerKind.Park,
                    count = 1,
                    parkId = park.id,
                )
            }
        }

        return parks
            .groupBy { park ->
                val latBucket = floor(park.latitude / gridSize).toInt()
                val lonBucket = floor(park.longitude / gridSize).toInt()
                latBucket to lonBucket
            }
            .map { (bucket, bucketParks) ->
                if (bucketParks.size == 1) {
                    val park = bucketParks.first()
                    MapMarkerUiModel(
                        id = park.id,
                        latitude = park.latitude,
                        longitude = park.longitude,
                        kind = MapMarkerKind.Park,
                        count = 1,
                        parkId = park.id,
                    )
                } else {
                    val lat = bucketParks.map { it.latitude }.average()
                    val lon = bucketParks.map { it.longitude }.average()
                    MapMarkerUiModel(
                        id = "cluster_${gridSize}_${bucket.first}_${bucket.second}",
                        latitude = lat,
                        longitude = lon,
                        kind = MapMarkerKind.Cluster,
                        count = bucketParks.size,
                    )
                }
            }
    }
}
