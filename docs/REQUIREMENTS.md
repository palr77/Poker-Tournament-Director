# Requirements – Tournament Director App v1.4 + Paywall

**Core Product Goal**  
(See attached original v1.4.docx for full FR/BR/RR)

**Key Additions**
- User onboarding with paywall (Free + Monthly 50 MXN + Yearly 500 MXN).
- Free tier shows non-intrusive banner + rewarded ads; paid removes all ads.
- All night operations remain fully functional in Free tier (core value must be usable).

**Data Entities** (section 8 of spec)
- Season, SeasonDate, Player, SeasonPlayerRegistration, Night, NightPlayerState, NightActionEvent, BlindLevel, ClockState, NightResult, SeasonStanding, BountyEvent, GuestParticipation, ExportSnapshot.

**Business Rules**  
(Full list from v1.4 FR-01 → RR-10 and BR-01 → BR-28 – use exact rules for payouts, bounties, rebuys (max 2), add-on logic, seat assignment, guest impact, tie handling, etc.)

**Non-Functional**
- 100% offline night operations (Room local DB).
- Lightweight – no backend server required for core functionality.
- TV display: dedicated fullscreen route, return button, Chromecast/AirPlay/Roku browser casting friendly.