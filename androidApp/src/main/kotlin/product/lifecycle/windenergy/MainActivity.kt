package product.lifecycle.windenergy

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import app.App
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import app.data.local.db.AppDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val databaseName = "windklar.db"
        preseedDatabaseFromAssets(applicationContext, databaseName)
        val driver = AndroidSqliteDriver(
            schema = AppDatabase.Schema,
            context = applicationContext,
            name = databaseName,
            callback = object : AndroidSqliteDriver.Callback(AppDatabase.Schema) {
                override fun onConfigure(db: SupportSQLiteDatabase) {
                    super.onConfigure(db)
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
        val database = AppDatabase(driver)

        val locationProvider = app.core.location.AndroidLocationProvider(applicationContext)

        setContent {
            App(database, locationProvider)
        }
    }

    private fun preseedDatabaseFromAssets(context: Context, databaseName: String) {
        val targetFile = context.getDatabasePath(databaseName)
        
        if (targetFile.exists()) {
            var hasIndex = false
            runCatching {
                android.database.sqlite.SQLiteDatabase.openDatabase(
                    targetFile.path,
                    null,
                    android.database.sqlite.SQLiteDatabase.OPEN_READONLY
                ).use { db ->
                    db.rawQuery(
                        "SELECT 1 FROM sqlite_master WHERE type='index' AND name='idx_wind_turbine_park_id'",
                        null
                    ).use { cursor ->
                        if (cursor.moveToFirst()) {
                            hasIndex = true
                        }
                    }
                }
            }.onFailure { error ->
                println("MainActivity: Failed to check database indexes: ${error.message}")
            }
            
            if (hasIndex) {
                println("MainActivity: Database is valid and has indexes.")
                return
            }
            
            println("MainActivity: Database is missing indexes. Deleting old database to replace it...")
            runCatching {
                context.deleteDatabase(databaseName)
            }.onFailure { error ->
                println("MainActivity: Failed to delete old database: ${error.message}")
            }
        }
        
        runCatching {
            context.assets.open("windklar_seed.db").use { input ->
                targetFile.parentFile?.mkdirs()
                targetFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            println("MainActivity: Copied preseed database to ${targetFile.absolutePath}")
        }.onFailure { error ->
            println("MainActivity: No preseed database available, falling back to JSON import: ${error.message}")
        }
    }

}
