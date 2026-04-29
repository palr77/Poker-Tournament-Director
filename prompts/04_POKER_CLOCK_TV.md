You are an expert Android Kotlin Compose developer.

Project: TournamentDirector

Implement the full Poker Clock + TV Display using the BlindLevel and ClockState entities from the data layer.

Features required (exact from FR-11, FR-12, FR-13, BR-13 to BR-15):
- Clock controls: Play, Pause, Restart Level, Back, Next
- Display: Previous blind, Current blind (small/big/ante), Next blind, Level number, Timer (MM:SS, large font)
- Break at level 5, ante starts at level 6 (20% of big blind)
- Blind structure loaded from current Season
- TV mode: dedicated fullscreen Compose screen (landscape, no navigation, huge timer, return button)
- TV page must be casting-friendly (Chromecast/AirPlay/Roku browser casting)

Deliver:
1. ClockViewModel (MVI) with precise timer using Coroutines + CountDownTimer fallback
2. ClockScreen (main night clock)
3. TvClockScreen (fullscreen TV version)
4. BlindLevel configuration UI (part of season setup – simple list editor)
5. Integration with Night repository (save/restore ClockState)

Use Material 3, large typography for TV visibility, and poker color scheme.