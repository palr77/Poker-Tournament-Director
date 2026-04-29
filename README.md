# Poker Tournament Director

Android app skeleton for an offline-first poker league and tournament night manager.

## Stack

- Kotlin 2.3
- Android Gradle Plugin 9.1
- Jetpack Compose + Material 3
- Compose Navigation with typed routes
- Hilt + KSP
- Room
- DataStore Preferences
- Google Play Billing
- Google Mobile Ads SDK

## Project Shape

- `presentation`: Compose screens, navigation, ViewModels, UI state
- `domain`: models and repository contracts
- `data`: Room-backed repositories, Billing, Ads, DataStore implementations
- `core`: database, DataStore, and DI modules

## Open In Android Studio

Open this folder as an existing Android project. Android Studio should use the Gradle settings in:

- `settings.gradle.kts`
- `build.gradle.kts`
- `app/build.gradle.kts`
- `gradle/libs.versions.toml`

The app package is `com.poker.tournamentdirector`.
