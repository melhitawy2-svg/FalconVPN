# Falcon VPN — Engine integration checkpoint (v0.11)

## Completed in this checkpoint
- Pinned the libbox dependency in `app/build.gradle.kts` to `com.github.singbox-android:libbox:1.14.0`.
- Added the JitPack repository in `settings.gradle.kts`.
- Added the Android `VpnService` manifest declaration and baseline network/notification permissions.
- Added `FalconVpnService` as a lifecycle entry point with cleanup hooks.
- Retained the Shadowsocks SIP002 parser and sing-box JSON configuration builder.

## Important limitation
This is **not yet a functioning VPN tunnel**. `FalconVpnService` intentionally stops immediately because the libbox `PlatformInterface`, TUN file-descriptor handoff, service callbacks, and engine start/stop lifecycle are not implemented. The UI must not show CONNECTED until those pieces return verified engine state. No APK build or device/server test has been performed in this environment.

## Remaining implementation work
1. Implement the exact `io.nekohasekai.libbox` API required by the selected libbox version, including `PlatformInterface` and service callbacks.
2. Request system VPN consent using `VpnService.prepare()` from the Activity and handle the returned Activity result.
3. Start a foreground service only after consent and notification permission where required.
4. Generate and validate the config, start libbox, and attach its TUN descriptor to Android's `VpnService.Builder` flow as required by the version's API.
5. Surface engine callbacks/errors to the UI; only report CONNECTED after confirmed start.
6. Build with Android SDK/Gradle, install on a physical device, and test a server that the app owner is authorized to use.

## Licensing
Review the selected `singbox-android/libbox` and upstream sing-box licenses and notices before redistribution. Dependency availability does not itself mean the app is integrated or tested.
