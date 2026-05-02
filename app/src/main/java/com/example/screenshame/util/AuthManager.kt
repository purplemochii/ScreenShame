package com.example.screenshame.util

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class AuthManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "screenshame_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveCredentials(email: String, password: String) {
        prefs.edit()
            .putString("email", email)
            .putString("password", password)
            .putBoolean("is_logged_in", true)
            .apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean("is_logged_in", false)

    fun getEmail(): String = prefs.getString("email", "") ?: ""

    fun validateCredentials(email: String, password: String): Boolean {
        val savedEmail = prefs.getString("email", null)
        val savedPassword = prefs.getString("password", null)
        return if (savedEmail == null) {
            // first time — register
            saveCredentials(email, password)
            true
        } else {
            // returning — check credentials
            savedEmail == email && savedPassword == password
        }
    }

    fun logout() {
        prefs.edit().putBoolean("is_logged_in", false).apply()
    }
}