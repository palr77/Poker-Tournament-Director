package com.poker.tournamentdirector.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.poker.tournamentdirector.core.database.TournamentDirectorDatabase
import com.poker.tournamentdirector.core.database.dao.NightDao
import com.poker.tournamentdirector.core.database.dao.PlayerDao
import com.poker.tournamentdirector.core.database.dao.SeasonDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import androidx.datastore.preferences.core.PreferenceDataStoreFactory

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private const val USER_PREFERENCES_FILE_NAME = "user_preferences.preferences_pb"

    @Provides
    @Singleton
    fun provideTournamentDirectorDatabase(
        @ApplicationContext context: Context,
    ): TournamentDirectorDatabase =
        Room.databaseBuilder(
            context,
            TournamentDirectorDatabase::class.java,
            TournamentDirectorDatabase.DATABASE_NAME,
        ).build()

    @Provides
    fun provideSeasonDao(database: TournamentDirectorDatabase): SeasonDao = database.seasonDao()

    @Provides
    fun providePlayerDao(database: TournamentDirectorDatabase): PlayerDao = database.playerDao()

    @Provides
    fun provideNightDao(database: TournamentDirectorDatabase): NightDao = database.nightDao()

    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(USER_PREFERENCES_FILE_NAME) },
        )
}
