# Falcon VPN v0.25.0 — native lifecycle preparation

## Changes in this revision
- Added a native 15-minute session countdown framework to `FalconVpnService`.
- Countdown is deliberately armed only through `startSessionTimerAfterEngineConnected()`, which must be called after libbox confirms a real working tunnel. It is not started by the current placeholder/failure path.
- The native status bridge now exposes `remainingSeconds` and `sessionEndsAt`.
- Stop, revoke, failure, and service-destroy paths clear the timer state.
- Updated Android version metadata to 0.25.0 / versionCode 25.

## Still not implemented / verified
- libbox `CommandServer` and `PlatformInterface` adapter and Android `VpnService.Builder.establish()` TUN descriptor handoff.
- Calling the timer-arm hook after a verified engine start and stopping the actual engine when the timer expires.
- AdMob rewarded-ad SDK and server-side entitlement. The current local video picker is not an ad integration.
- APK build, device testing, live server test. This environment has no Android SDK or Gradle installation.

**Important:** the app continues to fail closed and does not report CONNECTED or claim a VPN tunnel exists. The timer framework is dormant until the real engine start callback is implemented.
