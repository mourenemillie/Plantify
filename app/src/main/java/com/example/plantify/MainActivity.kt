package com.example.plantify

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.work.*
import com.example.plantify.data.worker.SyncWorker
import com.example.plantify.ui.PlantifyApp
import com.example.plantify.ui.theme.PlantifyTheme
import com.example.plantify.ui.theme.ThemeManager
import java.util.concurrent.TimeUnit
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import android.content.res.Configuration
import java.util.Locale

//perlu diingat! file utama hanya untuk navigasi!

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupSyncWorker()
        ThemeManager.init(this)

        setContent {
            val isDark by ThemeManager.isDarkMode.collectAsState()
            val currentLang by ThemeManager.language.collectAsState()

            val locale = Locale(currentLang)
            val configuration = Configuration(LocalConfiguration.current)
            configuration.setLocale(locale)
            val context = LocalContext.current.createConfigurationContext(configuration)

            CompositionLocalProvider(
                LocalContext provides context,
                LocalConfiguration provides configuration
            ) {
                PlantifyTheme(darkTheme = isDark) {
                    PlantifyApp()
                }
            }
        }
    }

    private fun setupSyncWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "SupabaseSync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}