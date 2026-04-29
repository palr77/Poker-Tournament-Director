package com.poker.tournamentdirector.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector
import kotlin.reflect.KClass

data class MainDestination(
    val route: Any,
    val routeClass: KClass<out Any>,
    val label: String,
    val icon: ImageVector,
)

val MainDestinations = listOf(
    MainDestination(
        route = AppRoute.Season,
        routeClass = AppRoute.Season::class,
        label = "Season",
        icon = Icons.Filled.Event,
    ),
    MainDestination(
        route = AppRoute.Night,
        routeClass = AppRoute.Night::class,
        label = "Night",
        icon = Icons.Filled.Groups,
    ),
    MainDestination(
        route = AppRoute.Clock,
        routeClass = AppRoute.Clock::class,
        label = "Clock",
        icon = Icons.Filled.Timer,
    ),
    MainDestination(
        route = AppRoute.Stats,
        routeClass = AppRoute.Stats::class,
        label = "Stats",
        icon = Icons.Filled.BarChart,
    ),
    MainDestination(
        route = AppRoute.Tv,
        routeClass = AppRoute.Tv::class,
        label = "TV",
        icon = Icons.Filled.Tv,
    ),
)
