package com.poker.tournamentdirector.data.ads

import android.content.Context
import com.poker.tournamentdirector.R
import com.poker.tournamentdirector.domain.repository.AdsRepository
import com.poker.tournamentdirector.domain.repository.PremiumStatusRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class AdMobAdsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    premiumStatusRepository: PremiumStatusRepository,
) : AdsRepository {
    override val shouldShowAds: Flow<Boolean> =
        premiumStatusRepository.premiumStatus.map { it.shouldShowAds }

    override val bannerAdUnitId: String =
        context.getString(R.string.admob_banner_ad_unit_id)

    override val rewardedAdUnitId: String =
        context.getString(R.string.admob_rewarded_ad_unit_id)
}
