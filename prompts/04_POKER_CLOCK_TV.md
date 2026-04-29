Create the complete Poker Clock screen and business logic for the Tournament Director app.

Requirements:
- Configurable blind levels per season (small, big, ante, duration).
- Break at level 5, ante starts at level 6 (20% of big blind).
- Controls: Play, Pause, Restart level, Back, Next.
- Display: Previous blind, Current blind (big numbers), Next blind, Level number, Countdown timer (MM:SS), Progress bar.
- Add-on level can be configured separately.
- Must work 100% offline.
- TV mode: fullscreen version with huge timer and return button.

Deliver:
- Domain model BlindLevel + ClockState
- ClockViewModel with all controls (use CountDownTimer or custom coroutine timer)
- Two Compose screens: NightClockScreen (operator) and TvClockScreen (fullscreen cast)
- Use Material3 cards, large typography, dark theme friendly.
- Include preview and tests stubs.