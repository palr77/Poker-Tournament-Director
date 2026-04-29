You are an expert Android Kotlin developer building a production-grade poker tournament director app.

**PROJECT RULES (never break them):**
- Kotlin + Jetpack Compose + Material3 + MVVM + Clean Architecture.
- Room DB as single source of truth (offline-first).
- All business rules must live in the domain layer as pure functions / UseCases.
- Premium features are gated with a `PremiumManager` that checks Google Play Billing subscription.
- Use type-safe navigation, Hilt, Kotlinx Serialization, Flows/StateFlow.
- Follow the exact architecture and standards in the attached ARCHITECTURE.md.

**FULL REQUIREMENTS** (implement exactly):
[Paste the entire content of docs/REQUIREMENTS.md here]

**TASK**:
Generate the complete project skeleton including:
1. Gradle files (app/build.gradle.kts + libs.versions.toml)
2. All core modules (data, domain, presentation, di)
3. Base Room database with the entities from DATABASE_SCHEMA.md (include DAOs and repositories)
4. Core theme, navigation graph, and premium gating composable
5. MainActivity with splash → Home screen

Output only the file structure with full code for each file. Use modern Kotlin idioms (context receivers where appropriate, sealed interfaces, etc.).