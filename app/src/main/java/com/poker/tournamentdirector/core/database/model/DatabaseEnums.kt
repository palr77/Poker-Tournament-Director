package com.poker.tournamentdirector.core.database.model

enum class NightStatus {
    Scheduled,
    Active,
    Closed,
}

enum class PlayerKind {
    League,
    Guest,
}

enum class NightPlayerStatus {
    Registered,
    Active,
    Eliminated,
    Winner,
}

enum class ActionType {
    BuyIn,
    Rebuy,
    AddOn,
    Elimination,
    Bounty,
    SeatAssignment,
    ClockAdjustment,
}

enum class EliminationReason {
    Standard,
    Bounty,
    NoShow,
    AdminCorrection,
}

enum class ClockStatus {
    Ready,
    Running,
    Paused,
    Finished,
}

enum class ExportType {
    Csv,
    WhatsAppText,
}
