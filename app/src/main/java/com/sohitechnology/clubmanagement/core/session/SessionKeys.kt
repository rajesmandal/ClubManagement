package com.sohitechnology.clubmanagement.core.session

import androidx.datastore.preferences.core.*

object SessionKeys {
    val TOKEN = stringPreferencesKey("token")          // auth token
    val USER_ID = intPreferencesKey("user_id")      // user id
    val IS_LOGGED_IN = booleanPreferencesKey("logged") // login flag
    val COMPANY_ID = stringPreferencesKey("company_id")        // company id
    val DEVICE_ID = stringPreferencesKey("device_id") // stable device id
}
