package com.poker.tournamentdirector.domain.repository

import kotlinx.coroutines.flow.Flow

interface AdsRepository {
    val shouldShowAds: Flow<Boolean>

    val bannerAdUnitId: String

    val rewardedAdUnitId: String
}
