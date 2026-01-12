package com.sohitechnology.clubmanagement.core.session

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "club_datastore") // datastore instance

@Singleton
class AppDataStore @Inject constructor (@ApplicationContext private val context: Context) {

    // save any type
    suspend fun <T> save(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { prefs ->
            prefs[key] = value // save value
        }
    }

    // read any type as Flow
    fun <T> read(key: Preferences.Key<T>, default: T): Flow<T> =
        context.dataStore.data.map { prefs ->
            prefs[key] ?: default // fallback value
        }

    suspend fun <T> readOnce(
        key: Preferences.Key<T>,
        default: T
    ): T {
        return context.dataStore.data.first()[key] ?: default
    }


    // remove specific key
    suspend fun <T> remove(key: Preferences.Key<T>) {
        context.dataStore.edit { prefs ->
            prefs.remove(key) // delete key
        }
    }

    // clear all data
    suspend fun clear() {
        context.dataStore.edit { it.clear() } // logout / reset
    }
}
