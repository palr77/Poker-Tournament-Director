package com.poker.tournamentdirector.presentation.night

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poker.tournamentdirector.domain.repository.AdsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class NightViewModel @Inject constructor(
    adsRepository: AdsRepository,
) : ViewModel() {
    val uiState: StateFlow<NightUiState> = adsRepository.shouldShowAds
        .map { shouldShowAds ->
            NightUiState(
                shouldShowAds = shouldShowAds,
                bannerAdUnitId = adsRepository.bannerAdUnitId,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NightUiState(),
        )
}

data class NightUiState(
    val shouldShowAds: Boolean = false,
    val bannerAdUnitId: String = "",
)
