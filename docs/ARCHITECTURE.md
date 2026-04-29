# Architecture & Tech Stack Standards

## Project Structure (Multi-module recommended later)
/app
├── data/               # Room, DAOs, Repositories
├── domain/             # Models, UseCases, Business rules
├── presentation/       # Compose UI, ViewModels, Navigation
├── di/                 # Hilt modules
├── feature/            # Feature modules (clock, night, stats, etc.)
├── core/               # utils, theme, navigation, premium checks
└── docs/

## Tech Stack (2026 Android best practices)
- **Language**: Kotlin 2.0+
- **UI**: Jetpack Compose + Material3 + Adaptive
- **Architecture**: MVVM + Clean Architecture + UseCases
- **Database**: Room (offline-first, single source of truth)
- **State**: Kotlin Flows + StateFlow + Compose State
- **DI**: Hilt
- **Navigation**: Jetpack Navigation Compose + Type-safe
- **Premium / IAP**: Google Play Billing Library v7 (subscriptions)
- **Live Sharing (Premium)**: Android Nearby Connections API (Wi-Fi Direct / same network)
- **Exports**: Kotlinx Serialization + CSV writer
- **Testing**: JUnit5 + Turbine + MockK + Compose UI tests
- **Build**: Gradle Kotlin DSL + Version Catalogs

## Coding Standards
- Package-by-feature.
- All business rules in domain layer (testable).
- No logic in Composables.
- Use sealed classes/interfaces for events/actions.
- Premium feature gating via `PremiumManager` + `isPremium()` flow.