package product.lifecycle.windenergy

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.view.Gravity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import app.App
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import app.data.local.source.SourceDatabase
import app.data.local.user.UserDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val sourceDatabaseName = "windklar_source.db"
        val userDatabaseName = "windklar_user.db"
        try {
            ensureSourceDatabaseFromAssets(applicationContext, sourceDatabaseName)
        } catch (error: Throwable) {
            setContentView(fatalStartupView(error.message ?: error.toString()))
            return
        }

        val sourceDriver = AndroidSqliteDriver(
            schema = SourceDatabase.Schema,
            context = applicationContext,
            name = sourceDatabaseName,
            callback = object : AndroidSqliteDriver.Callback(SourceDatabase.Schema) {
                override fun onConfigure(db: SupportSQLiteDatabase) {
                    super.onConfigure(db)
                    db.setForeignKeyConstraintsEnabled(true)
                    db.enableWriteAheadLogging()
                    runCatching {
                        db.execSQL("PRAGMA synchronous = NORMAL")
                        db.execSQL("PRAGMA temp_store = MEMORY")
                        db.execSQL("PRAGMA cache_size = -64000")
                    }.onFailure { error ->
                        println("MainActivity: Failed to apply performance PRAGMAs: ${error.message}")
                    }
                }
            }
        )
        val userDriver = AndroidSqliteDriver(
            schema = UserDatabase.Schema,
            context = applicationContext,
            name = userDatabaseName,
            callback = object : AndroidSqliteDriver.Callback(UserDatabase.Schema) {
                override fun onConfigure(db: SupportSQLiteDatabase) {
                    super.onConfigure(db)
                    runCatching {
                        db.execSQL("PRAGMA synchronous = NORMAL")
                        db.execSQL("PRAGMA temp_store = MEMORY")
                    }.onFailure { error ->
                        println("MainActivity: Failed to apply user DB PRAGMAs: ${error.message}")
                    }
                }
            }
        )
        val sourceDatabase = SourceDatabase(sourceDriver)
        val userDatabase = UserDatabase(userDriver)

        val locationProvider = app.core.location.AndroidLocationProvider(applicationContext)

        setContent {
            App(sourceDatabase, userDatabase, locationProvider)
        }
    }

    private fun ensureSourceDatabaseFromAssets(context: Context, databaseName: String) {
        val targetFile = context.getDatabasePath(databaseName)
        val bundledChecksum = readBundledSnapshotChecksum(context)
            ?: error("Gebündelte Stammdatenbank enthält keine Snapshot-Metadaten.")

        val installedChecksum = targetFile.takeIf { it.exists() }?.let { readSnapshotChecksum(it.path) }
        if (installedChecksum == bundledChecksum) {
            println("MainActivity: Source database is current ($installedChecksum).")
            return
        }

        if (targetFile.exists()) {
            println("MainActivity: Replacing source database ($installedChecksum -> $bundledChecksum).")
            context.deleteDatabase(databaseName)
        }

        runCatching {
            context.assets.open(SOURCE_SEED_ASSET).use { input ->
                targetFile.parentFile?.mkdirs()
                targetFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            println("MainActivity: Copied preseed database to ${targetFile.absolutePath}")
        }.onFailure { error ->
            throw IllegalStateException("Stammdatenbank konnte nicht aus dem App-Bundle kopiert werden.", error)
        }

        val copiedChecksum = readSnapshotChecksum(targetFile.path)
        check(copiedChecksum == bundledChecksum) {
            "Kopierte Stammdatenbank ist unvollständig oder beschädigt."
        }
    }

    private fun readBundledSnapshotChecksum(context: Context): String? {
        val tempFile = File(context.cacheDir, SOURCE_SEED_ASSET)
        return try {
            context.assets.open(SOURCE_SEED_ASSET).use { input ->
                tempFile.outputStream().use { output -> input.copyTo(output) }
            }
            readSnapshotChecksum(tempFile.path)
        } finally {
            tempFile.delete()
        }
    }

    private fun readSnapshotChecksum(path: String): String? = runCatching {
        SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY).use { db ->
            db.rawQuery(
                """
                SELECT checksum_sha256
                FROM snapshot_metadata
                ORDER BY imported_at DESC
                LIMIT 1
                """.trimIndent(),
                null,
            ).use { cursor ->
                if (cursor.moveToFirst()) cursor.getString(0) else null
            }
        }
    }.getOrNull()

    private fun SQLiteDatabase.hasTable(tableName: String): Boolean =
        rawQuery(
            "SELECT 1 FROM sqlite_master WHERE type='table' AND name=?",
            arrayOf(tableName)
        ).use { cursor -> cursor.moveToFirst() }

    private fun fatalStartupView(message: String): TextView =
        TextView(this).apply {
            text = "WindKlar konnte die lokalen Stammdaten nicht laden.\n\n$message"
            gravity = Gravity.CENTER
            setPadding(48, 48, 48, 48)
        }

    private companion object {
        const val SOURCE_SEED_ASSET = "windklar_source_seed.db"
    }
}
