package com.poker.tournamentdirector.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.poker.tournamentdirector.presentation.clock.ClockScreen
import com.poker.tournamentdirector.presentation.night.NightScreen
import com.poker.tournamentdirector.presentation.onboarding.OnboardingScreen
import com.poker.tournamentdirector.presentation.paywall.SubscriptionScreen
import com.poker.tournamentdirector.presentation.season.SeasonScreen
import com.poker.tournamentdirector.presentation.splash.SplashScreen
import com.poker.tournamentdirector.presentation.stats.StatsScreen
import com.poker.tournamentdirector.presentation.tv.TvScreen

@Composable
fun TournamentDirectorNavHost(
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val showBottomBar = MainDestinations.any { destination ->
        currentDestination?.hasRoute(destination.routeClass) == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                MainNavigationBar(
                    navController = navController,
                    currentDestination = currentDestination,
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoute.Splash,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable<AppRoute.Splash> {
                SplashScreen(
                    onNavigateToOnboarding = {
                        navController.navigateClearingBackStack(AppRoute.Onboarding)
                    },
                    onNavigateToMain = {
                        navController.navigateClearingBackStack(AppRoute.Season)
                    },
                )
            }
            composable<AppRoute.Onboarding> {
                OnboardingScreen(
                    onFinished = {
                        navController.navigate(AppRoute.Subscription) {
                            launchSingleTop = true
                        }
                    },
                )
            }
            composable<AppRoute.Subscription> {
                SubscriptionScreen(
                    onContinueToMain = {
                        navController.navigateClearingBackStack(AppRoute.Season)
                    },
                )
            }
            composable<AppRoute.Season> { SeasonScreen() }
            composable<AppRoute.Night> { NightScreen() }
            composable<AppRoute.Clock> { ClockScreen() }
            composable<AppRoute.Stats> { StatsScreen() }
            composable<AppRoute.Tv> { TvScreen() }
        }
    }
}

private fun NavHostController.navigateClearingBackStack(route: Any) {
    navigate(route) {
        popUpTo(graph.startDestinationId) {
            inclusive = true
        }
        launchSingleTop = true
    }
}
