package com.poker.tournamentdirector.core.di

import com.poker.tournamentdirector.data.ads.AdMobAdsRepository
import com.poker.tournamentdirector.data.billing.GooglePlayBillingRepository
import com.poker.tournamentdirector.data.preferences.DataStorePremiumStatusRepository
import com.poker.tournamentdirector.data.repository.RoomBountyRepository
import com.poker.tournamentdirector.data.repository.RoomExportRepository
import com.poker.tournamentdirector.data.repository.RoomNightRepository
import com.poker.tournamentdirector.data.repository.RoomPlayerRepository
import com.poker.tournamentdirector.data.repository.RoomSeasonRepository
import com.poker.tournamentdirector.domain.repository.AdsRepository
import com.poker.tournamentdirector.domain.repository.BillingRepository
import com.poker.tournamentdirector.domain.repository.BountyRepository
import com.poker.tournamentdirector.domain.repository.ExportRepository
import com.poker.tournamentdirector.domain.repository.NightRepository
import com.poker.tournamentdirector.domain.repository.PlayerRepository
import com.poker.tournamentdirector.domain.repository.PremiumStatusRepository
import com.poker.tournamentdirector.domain.repository.SeasonRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindPremiumStatusRepository(
        repository: DataStorePremiumStatusRepository,
    ): PremiumStatusRepository

    @Binds
    @Singleton
    abstract fun bindBillingRepository(
        repository: GooglePlayBillingRepository,
    ): BillingRepository

    @Binds
    @Singleton
    abstract fun bindAdsRepository(
        repository: AdMobAdsRepository,
    ): AdsRepository

    @Binds
    @Singleton
    abstract fun bindSeasonRepository(
        repository: RoomSeasonRepository,
    ): SeasonRepository

    @Binds
    @Singleton
    abstract fun bindPlayerRepository(
        repository: RoomPlayerRepository,
    ): PlayerRepository

    @Binds
    @Singleton
    abstract fun bindNightRepository(
        repository: RoomNightRepository,
    ): NightRepository

    @Binds
    @Singleton
    abstract fun bindBountyRepository(
        repository: RoomBountyRepository,
    ): BountyRepository

    @Binds
    @Singleton
    abstract fun bindExportRepository(
        repository: RoomExportRepository,
    ): ExportRepository
}
