package com.example.quickgram.data.local

class TokenStorage(context: Context) {
    private val sharedPrefs = EncryptedSharedPreferences.create(
        "secure_prefs",
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveAccessToken(token: String) {
        sharedPrefs.edit().putString("access_token", token).apply()
    }

    fun getAccessToken(): String? =
        sharedPrefs.getString("access_token", null)

    fun saveRefreshToken(token: String) {
        sharedPrefs.edit().putString("refresh_token", token).apply()
    }

    fun getRefreshToken(): String? =
        sharedPrefs.getString("refresh_token", null)

    fun clearTokens() {
        sharedPrefs.edit().clear().apply()
    }
}
