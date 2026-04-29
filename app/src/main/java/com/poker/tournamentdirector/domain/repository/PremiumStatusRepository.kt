package com.poker.tournamentdirector.domain.repository

import com.poker.tournamentdirector.domain.model.PremiumStatus
import kotlinx.coroutines.flow.Flow

interface PremiumStatusRepository {
    val premiumStatus: Flow<PremiumStatus>

    suspend fun setPremium(isPremium: Boolean)

    suspend fun setOnboardingCompleted(completed: Boolean)
}
