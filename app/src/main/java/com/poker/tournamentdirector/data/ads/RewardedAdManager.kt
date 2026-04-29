package com.poker.tournamentdirector.data.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.poker.tournamentdirector.domain.repository.AdsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RewardedAdManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val adsRepository: AdsRepository,
) {
    private var rewardedAd: RewardedAd? = null

    fun preload() {
        RewardedAd.load(
            context,
            adsRepository.rewardedAdUnitId,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                }
            },
        )
    }

    fun show(
        activity: Activity,
        onRewardEarned: () -> Unit = {},
    ): Boolean {
        val ad = rewardedAd ?: return false
        rewardedAd = null
        ad.show(activity) { _ ->
            onRewardEarned()
        }
        preload()
        return true
    }
}
