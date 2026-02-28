package com.sohitechnology.gymstudio.hammer.core.util

import android.content.Context
import android.provider.Settings
import com.sohitechnology.gymstudio.hammer.core.session.AppDataStore
import com.sohitechnology.gymstudio.hammer.core.session.SessionKeys
import java.util.UUID

object DeviceUtil {

    suspend fun getOrCreateDeviceId(
        context: Context,
        dataStore: AppDataStore
    ): String {

        // read stored device id
        val savedId = dataStore.readOnce(SessionKeys.DEVICE_ID, "")
        if (savedId.isNotEmpty()) return savedId // already exists

        // try android id
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )

        // fallback uuid
        val deviceId = androidId ?: UUID.randomUUID().toString()

        // save once
        dataStore.save(SessionKeys.DEVICE_ID, deviceId)

        return deviceId
    }
}
