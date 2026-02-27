package com.sohitechnology.clubmanagement

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.sohitechnology.clubmanagement.core.worker.MembershipExpiryWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class ClubApp : Application(), ImageLoaderFactory, Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()

    override fun onCreate() {
        super.onCreate()
        Log.d("ClubApp", "Application onCreate - scheduling worker")
        scheduleMembershipExpiryCheck()
    }

    private fun scheduleMembershipExpiryCheck() {
        val workRequest = PeriodicWorkRequestBuilder<MembershipExpiryWorker>(
            1, TimeUnit.DAYS
        ).build()

        // Using REPLACE for testing to ensure the worker runs immediately on app start
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "MembershipExpiryWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
        Log.d("ClubApp", "Worker scheduled with REPLACE policy")
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .crossfade(true)
            .build()
    }
}
