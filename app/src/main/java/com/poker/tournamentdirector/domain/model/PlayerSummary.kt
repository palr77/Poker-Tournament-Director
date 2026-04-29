package com.poker.tournamentdirector.domain.model

data class PlayerSummary(
    val id: Long,
    val displayName: String,
    val isGuest: Boolean,
)
