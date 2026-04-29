You are an expert Android Kotlin developer using Clean Architecture, Room, and Hilt (2026 standards).

Project: TournamentDirector (package: com.poker.tournamentdirector)

Using the EXACT data requirements from section 8 of the spec and all Business Rules (BR-01 to BR-28) and Functional Requirements (FR-01 to FR-30) from Tournament Director Requirements v1.4:

Create the complete data layer:

1. All @Entity data classes:
   - Season, SeasonDate, Player, SeasonPlayerRegistration
   - Night, NightPlayerState, NightActionEvent, BlindLevel
   - ClockState, NightResult, SeasonStanding
   - BountyEvent, BountyLedger, GuestParticipation, ExportSnapshot

2. Proper Room annotations:
   - @PrimaryKey, @ForeignKey, @Index, @Relation where needed
   - TypeConverters for: Instant/LocalDateTime, enums (ActionType, EliminationReason, etc.), BigDecimal/Money as Long (cents)

3. All DAO interfaces with queries needed for:
   - Night operations (actions, eliminations, rebuys/add-on enforcement)
   - Standings calculation
   - Bounty player detection (tied #1 in season points)
   - Payout calculations
   - Guest impact stats
   - Historical seasons

4. Repository interfaces (domain layer) and Room implementations (data layer) following Clean Architecture.

5. Business rule enforcement helpers:
   - Max 2 rebuys per player per night
   - Add-on only allowed if rebuys < 2
   - Prevent self-elimination
   - Guest rules (night-only, no season payout)
   - Seat assignment logic (Date 1 random, Date 2+ ranking-based)

Output:
- Full code for all entities
- TypeConverters.kt
- All DAOs
- Repository interfaces + implementations
- Any sealed classes for ActionType, etc.

Follow exact field names and rules from the spec. Use Kotlin 2.0+ features (data objects, sealed interfaces, etc.).