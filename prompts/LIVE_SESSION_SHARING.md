Implement the "Share Live Session" premium feature using Android Nearby Connections API.

Requirements:
- Host device starts sharing active night session.
- Any device on same Wi-Fi can discover and join.
- Joined device receives real-time updates of clock, player states, actions.
- Non-premium devices can join but cannot re-share.
- Premium devices can re-share.
- Conflict resolution: last write wins + optimistic UI.

Deliver full implementation: Advertiser + Discoverer code, data sync model, UI flow (Share button → QR or direct connect → Join screen).