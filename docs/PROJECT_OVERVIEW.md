# Poker Tournament Director – Android App

**Goal**  
A complete, offline-first poker league manager for Android (Kotlin) that handles full seasons, nightly tournaments, poker clock, TV display, money segmentation (season pool / night pot / bounty), player stats, eliminations, payouts, guests, and exports.

**Target Devices**  
Android 8.0+ (API 26+). Optimized for tablets (night ops) and phones (admin). TV display works in landscape fullscreen or via browser casting.

**Monetization (new requirement)**
- **Free tier**: All core features + ads (AdMob rewarded + banner).
- **Monthly** – 50 MXN  
- **Yearly** – 500 MXN  
Subscriptions via Google Play Billing remove ads and unlock any future premium features.

**Tech Stack (2026 best practices)**
- **Language**: Kotlin 2.0+
- **UI**: Jetpack Compose + Material 3 + Adaptive (for tablet/TV)
- **Architecture**: Clean Architecture + MVI (or MVVM with Orbit-MVI)
- **Local DB**: Room + DataStore (offline-first, local-first)
- **Dependency Injection**: Hilt
- **Async**: Kotlin Coroutines + Flow
- **Navigation**: Compose Navigation + Type-safe
- **Billing**: Google Play Billing Library v7+
- **Ads**: Google Mobile Ads SDK (AdMob)
- **Exports**: CSV (Apache Commons CSV or manual) + WhatsApp-ready text
- **Clock**: Precise CountDownTimer + Coroutine timer
- **Testing**: JUnit5 + Turbine + MockK + Compose UI tests
- **Build**: Gradle Kotlin DSL, version catalogs

**Project Status**  
v1.0 MVP = Season + Night + Clock + Basic payouts + Paywall