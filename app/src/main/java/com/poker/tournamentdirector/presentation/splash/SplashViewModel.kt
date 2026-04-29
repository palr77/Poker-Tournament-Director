package com.poker.tournamentdirector.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poker.tournamentdirector.domain.repository.PremiumStatusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val premiumStatusRepository: PremiumStatusRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            delay(SPLASH_DELAY_MILLIS)
            val status = premiumStatusRepository.premiumStatus.first()
            _uiState.value = SplashUiState(
                isLoading = false,
                target = if (status.onboardingCompleted) {
                    SplashTarget.Main
                } else {
                    SplashTarget.Onboarding
                },
            )
        }
    }

    private companion object {
        const val SPLASH_DELAY_MILLIS = 700L
    }
}

data class SplashUiState(
    val isLoading: Boolean = true,
    val target: SplashTarget? = null,
)

sealed interface SplashTarget {
    data object Onboarding : SplashTarget
    data object Main : SplashTarget
}
