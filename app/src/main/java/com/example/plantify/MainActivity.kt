package com.example.plantify

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.work.*
import com.example.plantify.data.worker.SyncWorker
import com.example.plantify.ui.PlantifyApp
import com.example.plantify.ui.theme.PlantifyTheme
import java.util.concurrent.TimeUnit

//perlu diingat! file utama hanya untuk navigasi!

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setupSyncWorker()

        setContent {
            val profileViewModel: ProfileViewModel = viewModel()
            val isDarkMode by profileViewModel.isDarkMode.collectAsState()

            PlantifyTheme(darkTheme = isDarkMode) {
                PlantifyApp(profileViewModel = profileViewModel)
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
