# Poker Tournament Director – Development Roadmap (MVP → v1.0)

## Phase 0 – Project Setup (DONE)
- Android project skeleton (Compose + Hilt + Room + Billing + Ads)
- Architecture & coding standards

## Phase 1 – Core Data Layer (NEXT – do this first)
- All Room entities, DAOs, TypeConverters (from REQUIREMENTS.md section 8 + v1.4 spec)
- Repository pattern + business rule enforcement

## Phase 2 – Paywall & Onboarding (immediately after data layer)
- Splash → Onboarding carousel → Subscription screen (Free / 50 MXN monthly / 500 MXN yearly)
- AdMob integration + premium status in DataStore

## Phase 3 – Poker Clock & TV Display
- Blind levels configuration
- Full clock controls (Play/Pause/Next/Back/Restart)
- TV fullscreen mode + casting-friendly UI

## Phase 4 – Season & Night Wizards
- Season creation, roster, dates, amounts, blind template
- Night wizard + seat assignment logic

## Phase 5 – Night Operations
- Player actions (buy-in/rebuy/add-on/elimination + eliminated-by)
- Re-entry/add-on rules enforcement, bounty players, guest support

## Phase 6 – Stats, Payouts & Exports
- Night/season standings, payouts (60/30/10 + tie rules), bounty ledger
- CSV + WhatsApp-ready summaries
- Fish / Sniper / guest impact reports

## Phase 7 – Polish & Release
- Testing, dark theme (poker aesthetic), offline recovery, export testing
- Google Play submission (Free + subscriptions)

**Target MVP Release**: Full end-to-end season + one night (clock, eliminations, payouts, exports) with paywall live.