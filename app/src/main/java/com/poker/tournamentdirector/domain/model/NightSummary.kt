package com.poker.tournamentdirector.domain.model

data class NightSummary(
    val id: Long,
    val seasonId: Long,
    val nightNumber: Int,
    val statusLabel: String,
)
