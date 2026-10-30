# Falcon VPN v0.25 — v41 UI/native bridge merge checkpoint

## Completed in this source revision
- Replaced the bundled Android WebView asset with the latest v41 design (including the video gate prototype and 15-minute preview UI).
- Removed the HTML-only fake CONNECTED transition and hard-coded demo throughput from the connect path.
- Routed the post-ad connect action through the existing `FalconNative.connectVpn()` Android bridge.
- Routed disconnect through `FalconNative.disconnectVpn()` and polls `FalconNative.engineStatus()` for honest Android service state.
- Kept the v0.24 Android permission/lifecycle and secure profile-store implementation intact.

## Important remaining blockers
- `FalconVpnService` still intentionally fails closed after preparing the sing-box config. The actual libbox `CommandServer`/`PlatformInterface`/TUN integration is NOT implemented.
- No real VPN tunnel can connect in this revision. `engineIntegrated` remains false.
- The video selector is still a local HTML preview, not Google AdMob.
- The 15-minute HTML preview timer is not a real service-side tunnel timeout; service-side timer must be implemented with the engine.
- No Android SDK/Gradle build, device test, or live server test has been run here.

## Next engineering step
Resolve and inspect the exact libbox 1.14.0 AAR/API (or use the matching official Android example), implement the version-matched platform adapter and TUN descriptor lifecycle, then compile and test with an authorized server profile. Do not set `engineIntegrated=true` until live tunneled traffic is verified.
