# Falcon VPN v0.25.1 — session UI and honest ad-gate preparation

## Implemented in source
- Preserved the existing v41 UI layout and styling.
- Removed the local-video picker as a supposed rewarded-ad mechanism. A user-selected file cannot verify an ad impression or grant a reward.
- Replaced it with an explicit development-mode notice and a button to continue testing the connection flow without claiming an ad reward.
- The session display now reads `remainingSeconds` from the native `engineStatus()` bridge instead of incrementing a fake local connected timer.
- Updated app metadata to versionCode 26 / versionName 0.25.1.

## Still pending
- Native timer is only armed after a real libbox tunnel reports connected. The current service intentionally fails closed before that point.
- Google AdMob rewarded ads are not integrated; SDK callback verification and reward policy remain pending.
- No APK build or device test was possible in this environment because Android SDK and Gradle are unavailable.
- The VPN engine is not yet connected to a real TUN interface; do not treat the app as a working VPN.
