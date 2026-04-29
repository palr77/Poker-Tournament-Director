You are an expert Android Kotlin developer using Clean Architecture, Room, Hilt, and Kotlin 2.0+.

**Use MASTER_PROJECT_CONTEXT + all docs/ files for exact requirements.**

Implement the **complete data layer** now.

Create:
1. All Room @Entity classes from section 8 of the spec (Season, SeasonDate, Player, SeasonPlayerRegistration, Night, NightPlayerState, NightActionEvent, BlindLevel, ClockState, NightResult, SeasonStanding, BountyEvent, BountyLedger, GuestParticipation, ExportSnapshot).
2. TypeConverters.kt (Instant/LocalDate, enums, Money as Long cents).
3. All DAO interfaces with queries needed for night operations, standings, bounty detection, payouts, guests, historical data.
4. Repository interfaces (domain) + Room implementations (data).
5. Business rule helpers (max 2 rebuys, add-on logic, prevent self-elimination, bounty players, guest rules, seat assignment).

Output only the complete, production-ready code files. Make it fully compilable with the existing Hilt + Room setup.