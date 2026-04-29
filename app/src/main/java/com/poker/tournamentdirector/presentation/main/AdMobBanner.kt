package com.poker.tournamentdirector.presentation.main

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun AdMobBanner(
    adUnitId: String,
    modifier: Modifier = Modifier,
) {
    val adWidthDp = LocalConfiguration.current.screenWidthDp

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.getLargeAnchoredAdaptiveBannerAdSize(context, adWidthDp))
                this.adUnitId = adUnitId
                loadAd(AdRequest.Builder().build())
            }
        },
    )
}
