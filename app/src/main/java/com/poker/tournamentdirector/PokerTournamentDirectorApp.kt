package com.poker.tournamentdirector

import android.app.Application
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PokerTournamentDirectorApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
    }
}
