# Falcon VPN v0.22 — config compatibility preparation

## Changes in this revision
- Updated the TUN address field in the generated sing-box configuration from the removed legacy `inet4_address` field to the current `address` array.
- Added both IPv4 and IPv6 interface prefixes to the generated TUN configuration.
- Bumped Android version to 0.22.0 / versionCode 22.

## Important runtime status
This revision is still **not a working VPN connection**. The service remains fail-closed because the exact libbox 1.14.0 Android binding and its generated Java API are not present locally for compile-time verification. It does not create a TUN descriptor, does not start `CommandServer`, and must not display CONNECTED. The declared Gradle dependency alone is not proof that the API adapter compiles or works.

## Next required integration
1. Resolve/download the declared `com.github.singbox-android:libbox:1.14.0` artifact in a real Gradle/Android SDK environment.
2. Implement the exact generated `PlatformInterface` methods against that artifact, including `OpenTun` via `VpnService.Builder.establish()`, interface/network callbacks, and descriptor ownership.
3. Start/reload the real libbox service with the generated config; close it and the TUN FD on stop/revoke/error.
4. Build, install, and test actual tunneled traffic on a device before claiming success.

APK build remains deferred, as requested.
