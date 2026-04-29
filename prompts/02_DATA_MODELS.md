Using the exact data requirements from section 8 of the Tournament Director spec (Season, SeasonDate, Player, SeasonPlayerRegistration, Night, NightPlayerState, NightActionEvent, BlindLevel, ClockState, NightResult, SeasonStanding, BountyEvent, GuestParticipation, ExportSnapshot), create:

1. All Room @Entity data classes with proper @PrimaryKey, @Relation, indices, and foreign keys.
2. Type converters for Date, enums (ActionType: BUY_IN, REBUY, ADD_ON, ELIMINATION), Money amounts as Long (cents).
3. DAO interfaces with all necessary queries for night operations, standings, bounty calculations.
4. Repository interfaces and Room implementations following Clean Architecture.

Follow the business rules exactly (max 2 rebuys, add-on only if rebuys < 2, eliminated-by required, prevent self-elimination, guest rules, etc.).