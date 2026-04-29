package com.poker.tournamentdirector.domain.model

data class PremiumStatus(
    val isPremium: Boolean = false,
    val onboardingCompleted: Boolean = false,
) {
    val shouldShowAds: Boolean
        get() = !isPremium
}
