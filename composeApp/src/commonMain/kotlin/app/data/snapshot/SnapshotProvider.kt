package app.data.snapshot

import org.jetbrains.compose.resources.ExperimentalResourceApi
import windklar.composeapp.generated.resources.Res

interface SnapshotProvider {
    suspend fun readSnapshotJson(): String
    suspend fun readMetadataJson(): String
}

class ComposeResourceSnapshotProvider(
    private val path: String = "files/snapshots/windklar_snapshot.json",
    private val metadataPath: String = "files/snapshots/windklar_snapshot_metadata.json",
) : SnapshotProvider {
    @OptIn(ExperimentalResourceApi::class)
    override suspend fun readSnapshotJson(): String =
        Res.readBytes(path).decodeToString()

    @OptIn(ExperimentalResourceApi::class)
    override suspend fun readMetadataJson(): String =
        Res.readBytes(metadataPath).decodeToString()
}
