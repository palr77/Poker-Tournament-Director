# Kotlin Coding Standards

- Use Kotlin 2.0 conventions + detekt + ktlint
- Data classes for all entities
- Sealed classes/interfaces for UI state and events
- No magic numbers – use const val or sealed objects for blind levels, points (9/6/3/1), etc.
- Exhaustive when expressions
- Error handling with Result<T> or sealed Result classes
- Unit tests required for all UseCases and payout calculations