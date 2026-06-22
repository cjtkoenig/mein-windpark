package app.data.local.dao

import app.data.local.db.AppDatabase
import app.data.local.db.Wind_turbine
import app.data.local.db.Metric as DbMetric
import app.data.local.db.Snapshot_metadata
import app.data.local.db.Data_hint
import app.core.model.WindTurbine
import app.core.model.Metric

// --- Turbine DAO ---
interface WindTurbineDao {
    suspend fun getByParkId(parkId: String): List<WindTurbine>
    suspend fun getAll(): List<WindTurbine>
    suspend fun getParkStatuses(): Map<String, String>
    suspend fun insertOrReplace(turbine: WindTurbine)
}

class SqlDelightWindTurbineDao(private val database: AppDatabase) : WindTurbineDao {
    override suspend fun getByParkId(parkId: String): List<WindTurbine> {
        return database.windTurbineQueries.selectWindTurbinesByParkId(parkId).executeAsList().map { it.toDomain() }
    }

    override suspend fun getAll(): List<WindTurbine> {
        return database.windTurbineQueries.selectAllWindTurbines().executeAsList().map { it.toDomain() }
    }

    override suspend fun getParkStatuses(): Map<String, String> {
        return database.windTurbineQueries.selectParkStatuses().executeAsList().associate { row ->
            row.wind_park_id to row.park_status
        }
    }
    override suspend fun insertOrReplace(turbine: WindTurbine) {

        database.windTurbineQueries.upsertWindTurbine(
            id = turbine.id,
            wind_park_id = turbine.windParkId,
            name = turbine.name,
            municipality_id = turbine.municipalityId,
            municipality_name = turbine.municipalityName,
            district_id = turbine.districtId,
            district_name = turbine.districtName,
            state_id = turbine.stateId,
            state_name = turbine.stateName,
            latitude = turbine.latitude,
            longitude = turbine.longitude,
            installed_capacity_kw = turbine.installedCapacityKw,
            status = turbine.status,
            turbine_type = turbine.turbineType,
            manufacturer = turbine.manufacturer,
            model = turbine.model,
            hub_height_m = turbine.hubHeightM,
            rotor_diameter_m = turbine.rotorDiameterM,
            source_name = turbine.sourceName,
            source_url = turbine.sourceUrl,
            source_updated_at = turbine.sourceUpdatedAt,
            data_quality = turbine.dataQuality
        )
    }

    private fun Wind_turbine.toDomain() = WindTurbine(
        id = id,
        windParkId = wind_park_id,
        name = name,
        municipalityId = municipality_id,
        municipalityName = municipality_name,
        districtId = district_id,
        districtName = district_name,
        stateId = state_id,
        stateName = state_name,
        latitude = latitude,
        longitude = longitude,
        installedCapacityKw = installed_capacity_kw,
        status = status,
        turbineType = turbine_type,
        manufacturer = manufacturer,
        model = model,
        hubHeightM = hub_height_m,
        rotorDiameterM = rotor_diameter_m,
        sourceName = source_name,
        sourceUrl = source_url,
        sourceUpdatedAt = source_updated_at,
        dataQuality = data_quality
    )
}

// --- Metric DAO ---

interface MetricDao {
    suspend fun getForSubject(subjectType: String, subjectId: String): List<Metric>
    suspend fun getAll(): List<Metric>
    suspend fun insertOrReplace(metric: Metric)
}

class SqlDelightMetricDao(private val database: AppDatabase) : MetricDao {
    override suspend fun getForSubject(subjectType: String, subjectId: String): List<Metric> {
        return database.metricQueries.selectMetricsForSubject(subjectType, subjectId).executeAsList().map { it.toDomain() }
    }

    override suspend fun getAll(): List<Metric> {
        return database.metricQueries.selectAllMetrics().executeAsList().map { it.toDomain() }
    }

    override suspend fun insertOrReplace(metric: Metric) {
        database.metricQueries.upsertMetric(
            id = metric.id,
            subject_type = metric.subjectType,
            subject_id = metric.subjectId,
            metric_type = metric.metricType,
            metric_value = metric.value,
            unit = metric.unit,
            period = metric.period,
            source_name = metric.sourceName,
            source_url = metric.sourceUrl,
            source_updated_at = metric.sourceUpdatedAt,
            data_quality = metric.dataQuality,
            calculation_note = metric.calculationNote
        )
    }

    private fun DbMetric.toDomain() = Metric(
        id = id,
        subjectType = subject_type,
        subjectId = subject_id,
        metricType = metric_type,
        value = metric_value,
        unit = unit,
        period = period,
        sourceName = source_name,
        sourceUrl = source_url,
        sourceUpdatedAt = source_updated_at,
        dataQuality = data_quality,
        calculationNote = calculation_note
    )
}

// --- Favorite DAO ---

interface FavoriteDao {
    suspend fun getFavoriteIds(): List<String>
    suspend fun isFavorite(parkId: String): Boolean
    suspend fun addFavorite(parkId: String, timestamp: Long)
    suspend fun removeFavorite(parkId: String)
}

class SqlDelightFavoriteDao(private val database: AppDatabase) : FavoriteDao {
    override suspend fun getFavoriteIds(): List<String> {
        return database.favoriteQueries.selectFavoriteIds().executeAsList()
    }

    override suspend fun isFavorite(parkId: String): Boolean {
        return database.favoriteQueries.isFavorite(parkId).executeAsOne()
    }

    override suspend fun addFavorite(parkId: String, timestamp: Long) {
        database.favoriteQueries.addFavorite(parkId, timestamp)
    }

    override suspend fun removeFavorite(parkId: String) {
        database.favoriteQueries.removeFavorite(parkId)
    }
}

// --- Recent Wind Park DAO ---

interface RecentWindParkDao {
    suspend fun getRecentWindParkIds(limit: Long): List<String>
    suspend fun recordRecentWindPark(parkId: String, timestamp: Long)
    suspend fun clear()
}

class SqlDelightRecentWindParkDao(private val database: AppDatabase) : RecentWindParkDao {
    override suspend fun getRecentWindParkIds(limit: Long): List<String> {
        return database.recentWindParkQueries.selectRecentWindParks(limit).executeAsList().map { it.id }
    }

    override suspend fun recordRecentWindPark(parkId: String, timestamp: Long) {
        database.recentWindParkQueries.recordRecentWindPark(parkId, timestamp)
    }

    override suspend fun clear() {
        database.recentWindParkQueries.clearRecentWindParks()
    }
}

// --- Data Hint DAO ---

interface DataHintDao {
    suspend fun insertOrReplace(
        id: String,
        category: String,
        confidence: String,
        status: String,
        description: String,
        windTurbineId: String?,
        windParkId: String?,
        municipalityId: String?,
        latitude: Double?,
        longitude: Double?,
        suggestedValue: String?,
        imageUri: String?,
        createdAt: Long,
        updatedAt: Long
    )
}

class SqlDelightDataHintDao(private val database: AppDatabase) : DataHintDao {
    override suspend fun insertOrReplace(
        id: String,
        category: String,
        confidence: String,
        status: String,
        description: String,
        windTurbineId: String?,
        windParkId: String?,
        municipalityId: String?,
        latitude: Double?,
        longitude: Double?,
        suggestedValue: String?,
        imageUri: String?,
        createdAt: Long,
        updatedAt: Long
    ) {
        database.dataHintQueries.upsertDataHint(
            id = id,
            category = category,
            confidence = confidence,
            status = status,
            description = description,
            wind_turbine_id = windTurbineId,
            wind_park_id = windParkId,
            municipality_id = municipalityId,
            latitude = latitude,
            longitude = longitude,
            suggested_value = suggestedValue,
            image_uri = imageUri,
            created_at_epoch_millis = createdAt,
            updated_at_epoch_millis = updatedAt
        )
    }
}

// --- Snapshot Metadata DAO ---

interface SnapshotMetadataDao {
    suspend fun getLatest(): Snapshot_metadata?
}

class SqlDelightSnapshotMetadataDao(private val database: AppDatabase) : SnapshotMetadataDao {
    override suspend fun getLatest(): Snapshot_metadata? {
        return database.snapshotMetadataQueries.selectLatestSnapshot().executeAsOneOrNull()
    }
}
