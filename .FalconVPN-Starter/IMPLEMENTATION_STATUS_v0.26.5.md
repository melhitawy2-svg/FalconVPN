# Falcon VPN v0.26.5 — UI synchronization

## Completed in this package
- Replaced the Android app bundled WebView asset `app/src/main/assets/index.html` with the latest Library preview `FalconVPN-preview-v0.26.5-server-lock-card-matched.html`.
- Kept the existing Android WebView bridge and native import/profile code unchanged. The latest HTML references bridge methods that are present in `MainActivity.kt` (connect/disconnect/status, saved profile list, Shadowsocks field save, VLESS link save/validation and profile selection).
- Updated Android `versionName` to `0.26.5` and `versionCode` to `33`.

## Not completed / must not be represented as working
- AdMob SDK and production ad units are not integrated in this package yet.
- Real sing-box/libbox `CommandServer` + `PlatformInterface` + TUN lifecycle integration is still incomplete. The VPN service deliberately fails closed and does not establish a tunnel or report CONNECTED.
- No Android Gradle build, APK installation, or device test was performed in this environment.

## Next implementation stage
- Integrate AdMob test ads first, then configure the supplied production ad unit IDs. Place the banner in the existing intended UI location and implement opt-in rewarded extension only after a verified reward callback.
- Complete and test the libbox platform adapter before enabling actual VPN sessions.
