You are an expert Android Kotlin Compose developer.

Project: TournamentDirector

Implement the complete user onboarding + paywall flow using Google Play Billing Library v7+ and AdMob.

Requirements (exactly):
- Free tier: full core functionality + non-intrusive banner ad on Night Dashboard + rewarded ad after night close
- Monthly subscription: 50 MXN (Google Play product ID: monthly_premium)
- Yearly subscription: 500 MXN (Google Play product ID: yearly_premium)
- Splash screen → Onboarding carousel (4 screens explaining season/night/clock/stats value)
- Subscription screen with three clear cards (Free | Monthly | Yearly) showing benefits
- "Restore purchases" button
- After successful purchase: save premium status in DataStore, remove all ads
- Premium status is observed across the app (Hilt + DataStore)

Deliver:
1. Full Compose screens (SplashScreen, OnboardingScreen, SubscriptionScreen)
2. ViewModels using MVI pattern
3. BillingRepository (with BillingClient, purchase flow, querySkuDetails, etc.)
4. AdMob integration (Banner + Rewarded) + PremiumStatus provider
5. Navigation routes and logic to block premium features only for ads (core features always work)
6. Dark poker-themed UI (green/black/gold)

Use existing Hilt setup from the skeleton. Make it production-ready with error handling and loading states.