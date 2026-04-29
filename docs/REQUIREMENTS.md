# Tournament Director App - Consolidated Requirements
**Version**: 1.4 + Initial Features (April 2026)
**Target**: Android (Kotlin + Jetpack Compose) – Freemium model

## Core MVP Features (implement first)
- Full poker clock with configurable blind schedule + ante (break at level 5, ante = 20% BB from level 6).
- Configurable buy-in, rebuy (max 2), add-on rules (add-on only when rebuys not depleted).
- Unlimited players, dates, tables **(Premium)**.
- Seat assignment: random (Date 1) or ranking-based (Date 2+) **(Premium)**.
- Night operations: buy-in/rebuy/add-on/elimination with mandatory “eliminated-by” (prevent self-elim).
- Guest players (night-only, full participation, no season payout eligibility).
- Bounty system: current #1 tied players = bounties; their money tracked separately; bounty winner = final elimination of a bounty player.
- Live session sharing on same Wi-Fi network (Premium): seamless handoff between devices; non-premium can join but not re-share.
- Configurable payout schedule (editable % per place, default 60/30/10 for night & season).
- Basic night & season statistics (points 9/6/3/1, Fish, Sniper, standings).

## Premium Features (unlock via subscription)
- Unlimited players/dates/tables.
- Advanced seat assignment.
- Configurable layout & color scheme.
- Live session share / multi-device continuity.
- Season-over-season statistics with archetypes:
  - Sniper (most knockouts)
  - Donkey (most times eliminated)
  - Passive (few eliminations but reaches payouts)
  - Bad & Unlucky (most 2nd places)
  - Bubble Boy (always eliminated just before payouts)
  - King of Poker (most wins + points)
- Season-finale guest impact report.

## Business Rules & Financial Segmentation
(See original v1.4 FR-21–FR-30, BR-01–BR-28, RR-01–RR-10 – all must be enforced.)

## Non-Functional
- 100% offline-capable (local-first, Room DB).
- Lightweight, no mandatory cloud.
- Mobile-friendly + dedicated TV cast page (fullscreen timer + blinds).
- Exports: CSV + WhatsApp-ready summary.