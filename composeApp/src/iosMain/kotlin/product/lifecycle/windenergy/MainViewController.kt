package product.lifecycle.windenergy

import app.App
import androidx.compose.ui.window.ComposeUIViewController
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.data.local.db.AppDatabase
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    val driver = NativeSqliteDriver(AppDatabase.Schema, "windklar.db")
    val database = AppDatabase(driver)
    val locationProvider = app.core.location.IosLocationProvider()
    return ComposeUIViewController { App(database, locationProvider) }
}
