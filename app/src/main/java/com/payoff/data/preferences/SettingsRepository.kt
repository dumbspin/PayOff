package com.payoff.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        val KEY_APP_LOCK_PIN = stringPreferencesKey("app_lock_pin")
        val KEY_USE_BIOMETRIC = booleanPreferencesKey("use_biometric")
        val KEY_USSD_BUDGET = intPreferencesKey("ussd_budget")
        val KEY_IS_ONBOARDED = booleanPreferencesKey("is_onboarded")
    }

    val isOnboarded: Flow<Boolean> = dataStore.data.map { it[KEY_IS_ONBOARDED] ?: false }
    
    suspend fun setOnboarded(state: Boolean) {
        dataStore.edit { it[KEY_IS_ONBOARDED] = state }
    }

    val appLockPin: Flow<String?> = dataStore.data.map { it[KEY_APP_LOCK_PIN] }
    
    suspend fun setAppLockPin(pin: String) {
        dataStore.edit { it[KEY_APP_LOCK_PIN] = pin }
    }

    val useBiometric: Flow<Boolean> = dataStore.data.map { it[KEY_USE_BIOMETRIC] ?: false }
    
    suspend fun setUseBiometric(enabled: Boolean) {
        dataStore.edit { it[KEY_USE_BIOMETRIC] = enabled }
    }

    val ussdBudget: Flow<Int?> = dataStore.data.map { it[KEY_USSD_BUDGET] }
    
    suspend fun setUssdBudget(budget: Int) {
        dataStore.edit { it[KEY_USSD_BUDGET] = budget }
    }
}
