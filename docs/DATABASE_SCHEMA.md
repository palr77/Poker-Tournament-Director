# Room Database Schema (initial)

Entities:
- Season (id, name, inscriptionFee, numberOfDates, startDate…)
- SeasonDate (id, seasonId, dateNumber, isCompleted)
- Player (id, name, isGuestForNight)
- Night (id, seasonDateId, nightPot, bountyTotal, status)
- NightPlayerState (nightId, playerId, buyIns, rebuys, addOns, status, seatNumber, points)
- BlindLevel (seasonId, level, smallBlind, bigBlind, ante, durationMinutes, isBreak)
- NightAction (id, nightId, playerId, type: BUY_IN/REBUY/ADD_ON/ELIMINATION, amount, eliminatedByPlayerId?, timestamp)
- BountyEvent (nightId, bountyPlayerId, winnerPlayerId, amount)
- SeasonStanding (seasonId, playerId, totalPoints, rank…)

Relations: One-to-many, foreign keys with cascading where appropriate.