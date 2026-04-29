You are an expert Android Kotlin Compose developer using Clean Architecture, Hilt, DataStore, Google Play Billing v7, and AdMob (2026 standards).

**Use MASTER_PROJECT_CONTEXT + all docs/ files for exact requirements.**

We now have the complete data layer. Implement the full **Paywall & Onboarding flow**.

Requirements (exact):
- Free tier: All core features work + non-intrusive AdMob banner on Night Dashboard + rewarded ad after night close, limit to 6 players in free tier. Do not allow for season record keeping. Cannot share stats nor session.
- Monthly subscription: 50 MXN (product ID: monthly_premium)  - Full features & free updates
- Yearly subscription: 500 MXN (product ID: yearly_premium) - Full features & free updates
- Splash screen → Onboarding carousel (4 screens explaining core value)
- Subscription screen with three clear cards (Free | Monthly | Yearly) showing benefits
- "Restore purchases" button
- After successful purchase: save premium status in DataStore, remove all ads
- Premium status observed app-wide via Hilt + DataStore

Deliver complete, production-ready code:
1. Compose screens: SplashScreen, OnboardingScreen, SubscriptionScreen
2. ViewModels (MVI pattern)
3. BillingRepository (full BillingClient integration, purchase flow, querySkuDetails, etc.)
4. AdMob integration (BannerAd + RewardedAd) + PremiumStatus provider
5. Navigation logic and premium checks
6. Dark poker-themed UI (green/black/gold)

Make it fully compatible with the existing Hilt + Room + Navigation setup from the skeleton.