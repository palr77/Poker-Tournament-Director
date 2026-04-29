package com.poker.tournamentdirector.domain.model

data class SeasonSummary(
    val id: Long,
    val name: String,
    val isActive: Boolean,
    val buyInCents: Long,
)
