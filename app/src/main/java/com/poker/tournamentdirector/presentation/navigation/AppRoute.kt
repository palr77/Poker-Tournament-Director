package com.poker.tournamentdirector.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface AppRoute {
    @Serializable
    data object Splash : AppRoute

    @Serializable
    data object Onboarding : AppRoute

    @Serializable
    data object Subscription : AppRoute

    @Serializable
    data object Season : AppRoute

    @Serializable
    data object Night : AppRoute

    @Serializable
    data object Clock : AppRoute

    @Serializable
    data object Stats : AppRoute

    @Serializable
    data object Tv : AppRoute
}
