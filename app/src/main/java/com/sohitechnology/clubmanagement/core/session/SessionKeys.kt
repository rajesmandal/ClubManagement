package com.sohitechnology.clubmanagement.core.session

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object SessionKeys {
    // user session
    val TOKEN = stringPreferencesKey("token")
    val USER_ID = intPreferencesKey("user_id")
    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    val COMPANY_ID = stringPreferencesKey("company_id")
    val DEVICE_ID = stringPreferencesKey("device_id") // stable device id

    // app settings
    val THEME_MODE = stringPreferencesKey("theme_mode")
    val IS_APP_LOCK_ENABLED = booleanPreferencesKey("is_app_lock_enabled")

    // user profile
    val ROLE = stringPreferencesKey("role")
    val USER_NAME = stringPreferencesKey("user_name")
    val FULL_NAME = stringPreferencesKey("full_name")
    val PROFILE_IMAGE = stringPreferencesKey("profile_image")
}
