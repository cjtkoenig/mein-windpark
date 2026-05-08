package app.data.seed

interface SeedDataImporter {
    suspend fun importIfNeeded()
}
