package com.example.plantify.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.plantify.data.local.PlantDatabase
import com.example.plantify.data.repository.PlantRepository

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val database = PlantDatabase.getDatabase(applicationContext)
        val repository = PlantRepository(database.plantDao())

        return try {
            repository.syncWithSupabase()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
