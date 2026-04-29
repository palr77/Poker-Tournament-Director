# Architecture Standards

**Layered Clean Architecture**

- **presentation**: Compose screens, ViewModels (MVI), UI state
- **domain**: UseCases, Entities, Repositories interfaces, Business rules
- **data**: Room DAOs, Mappers, Local DataSource, BillingRepository, AdsRepository

**Offline-First Strategy**
- All reads/writes go through Room first.
- ExportSnapshot entity stores night results for CSV/WhatsApp.
- No network required except Billing & Ads.

**State Management**
- MVI with Orbit-MVI or simple StateFlow + SharedFlow.
- Single source of truth = Room DB.

**Feature Modules**
- feature-season, feature-night, feature-clock, feature-stats, feature-paywall, feature-tv