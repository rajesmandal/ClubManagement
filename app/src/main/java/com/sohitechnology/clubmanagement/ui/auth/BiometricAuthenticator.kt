package com.sohitechnology.clubmanagement.ui.auth

import android.content.Context
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class BiometricAuthenticator @Inject constructor(
    @ActivityContext private val context: Context
) {
    private val TAG = "BiometricAuth"

    fun canAuthenticate(): Int {
        val biometricManager = BiometricManager.from(context)
        val authenticators = BIOMETRIC_STRONG or DEVICE_CREDENTIAL
        val result = biometricManager.canAuthenticate(authenticators)
        
        when (result) {
            BiometricManager.BIOMETRIC_SUCCESS -> Log.d(TAG, "App can authenticate using biometrics.")
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> Log.e(TAG, "No biometric features available on this device.")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> Log.e(TAG, "Biometric features are currently unavailable.")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> Log.e(TAG, "The user has not enrolled any biometrics or device credentials.")
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> Log.e(TAG, "A security update is required.")
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> Log.e(TAG, "The requested authenticators are not supported.")
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> Log.e(TAG, "Biometric status unknown.")
        }
        return result
    }

    fun promptBiometric(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        onSuccess: () -> Unit,
        onError: (Int, CharSequence) -> Unit,
        onFailed: () -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Log.d(TAG, "Authentication succeeded!")
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Log.e(TAG, "Authentication error: $errorCode -> $errString")
                    onError(errorCode, errString)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Log.w(TAG, "Authentication failed (wrong biometric).")
                    onFailed()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .build()

        try {
            Log.d(TAG, "Showing biometric prompt...")
            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            Log.e(TAG, "Error during authentication: ${e.message}")
            onError(-1, e.message ?: "Unknown error")
        }
    }
}
