package com.poker.tournamentdirector.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.poker.tournamentdirector.domain.model.PremiumStatus
import com.poker.tournamentdirector.domain.repository.PremiumStatusRepository
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

@Singleton
class DataStorePremiumStatusRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : PremiumStatusRepository {
    override val premiumStatus: Flow<PremiumStatus> = dataStore.data
        .catch { throwable ->
            if (throwable is IOException) {
                emit(emptyPreferences())
            } else {
                throw throwable
            }
        }
        .map { preferences ->
            PremiumStatus(
                isPremium = preferences[PreferencesKeys.IsPremium] ?: false,
                onboardingCompleted = preferences[PreferencesKeys.OnboardingCompleted] ?: false,
            )
        }

    override suspend fun setPremium(isPremium: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IsPremium] = isPremium
        }
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.OnboardingCompleted] = completed
        }
    }

    private object PreferencesKeys {
        val IsPremium = booleanPreferencesKey("is_premium")
        val OnboardingCompleted = booleanPreferencesKey("onboarding_completed")
    }
}
