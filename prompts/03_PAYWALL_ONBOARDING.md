Create the complete paywall and onboarding flow for an Android Kotlin Compose app:

- Free tier (with AdMob banner + rewarded ads)
- Monthly subscription = 50 MXN
- Yearly subscription = 500 MXN

Requirements:
- Splash screen → Onboarding carousel (3-4 screens explaining core value)
- Subscription screen with three cards (Free | Monthly | Yearly) using Google Play Billing
- Restore purchases button
- After successful purchase, save premium status in DataStore and remove ads
- Free users see banner at bottom of night dashboard and occasional rewarded ad after night close
- All core poker features (clock, eliminations, payouts) work in Free tier

Provide full Compose screens, ViewModel, BillingRepository, and AdMob integration code.