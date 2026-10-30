# Falcon VPN — Android source prototype v0.9

This project is based on the supplied `VPNStarter-Android.zip` UI scaffold and begins integrating a local server/configuration manager.

## Implemented in this iteration
- App branding changed to Falcon VPN; application ID set to `com.falconvpn.app`.
- Config manager UI with manual text/URI entry, Android document picker import, clipboard import, per-profile export, and delete.
- Basic protocol identification for `vless://`, `vmess://`, `trojan://`, `ss://`, `ssh://`, `socks://`, `socks5://`, and JSON-looking configs.
- Local persistence of imported profile text using SharedPreferences.
- Duplicate exact-text imports are ignored.

## Important limitations
- This is still a prototype. The Connect button remains a UI demo and does not establish a VPN tunnel.
- Protocol identification is not protocol validation. Imported configs are stored as text and are **not yet parsed into an engine configuration or connected**.
- Subscription URLs are not fetched/updated; QR import, encrypted backups, secure credential storage, and SSH tunnel engine integration are not implemented.
- Local profile text is stored in ordinary app preferences, not encrypted. Treat configs as secrets and use only servers you own or are authorized to use.
- No APK build or device test is claimed. The project does not include a Gradle wrapper; open it in Android Studio and sync with a compatible Gradle installation.

## Next engineering steps
1. Add automated tests for URI/config parsing and malformed input.
2. Move persistence to a dedicated repository and use Android Keystore-backed encryption for secrets.
3. Choose an engine and verify its license/Android integration before implementing VLESS/VMess/Trojan/Shadowsocks support.
4. Implement `VpnService` lifecycle, consent flow, routing, DNS, and disconnect/error handling.
5. Add SSH as a separate engine path, then real connectivity and throughput diagnostics.

## Build
Open the `vpn-starter` folder in Android Studio and allow Gradle sync. The app uses Android Gradle Plugin 8.7.3, Kotlin 2.0.21, Compose, compile SDK 35, and min SDK 26.


## v0.9 engine integration checkpoint
- Added a protocol-neutral `VpnEngine` interface and typed config/result/state models under `com.falconvpn.app.vpn.engine`.
- Added `ENGINE_INTEGRATION.md` documenting the real-engine integration and device-test checklist.
- This is an architecture checkpoint, not a bundled VPN engine. No real tunnel is established yet.


## Rewarded time UI (v0.9)
The home screen now includes a connection-time card below the ad-space placeholder: 15 free minutes and a button for a rewarded video that is intended to grant +15 minutes. The rewarded-ad SDK and real countdown/entitlement enforcement are not integrated yet, so the button explicitly reports that limitation and does not grant fake time. VPN connection itself remains a prototype.

## v0.10 preparation update (2026-10-28)
- Added a SIP002 Shadowsocks link parser and sing-box JSON config builder under `com.falconvpn.app.vpn.engine`.
- The connect button no longer reports a fake successful connection; it clearly tells the user that a real engine is not integrated yet.
- This is not a working VPN APK yet. The `libbox` dependency, Android VPN service/platform bridge, build, and physical-device traffic tests are still outstanding.
- Never include a user's server password in source code, screenshots, logs, or a shared archive. Import private server links locally for testing.


## v0.12 progress checkpoint
- The SIP002 parser no longer depends on Android framework `Uri`/`Base64` helpers, making it testable as plain JVM code.
- Added unit tests for the provided Shadowsocks link shape and invalid-scheme rejection.
- Added JUnit 4 test dependency.
- This is still not a working VPN build: `FalconVpnService` intentionally stops until a compatible libbox `BoxService`/`PlatformInterface` adapter is implemented. No APK has been built or server connection verified in this environment.


## v0.13 build pipeline checkpoint (2026-10-28)
- Added a GitHub Actions workflow at `.github/workflows/android-build.yml` to run JVM unit tests, build a debug APK, and publish the APK as a short-lived workflow artifact.
- Bumped app version metadata to 0.13.0.
- This workflow has not been executed from this environment; it requires the project to be pushed to a GitHub repository and run there.
- **No real VPN tunnel is integrated yet.** The existing service intentionally stops rather than claiming success. The libbox Android API/PlatformInterface adapter, Android VPN consent flow, routing/DNS lifecycle, and device connectivity tests remain required.


## Current implementation checkpoint
See `IMPLEMENTATION_STATUS_v0.17.md`. The native engine is not yet integrated; this source package is not a working VPN APK.


## v0.19 checkpoint
Saved Shadowsocks profiles can now be listed, selected, and deleted from the Android UI. Selection is metadata only: it does not start a VPN connection. See `IMPLEMENTATION_STATUS_v0.19.md` for what remains before engine integration and APK build.


## v0.20 VPN lifecycle groundwork
The Android permission/notification lifecycle and native connect/disconnect bridge are wired, and the UI no longer fakes a connected state. **The real libbox service adapter is still incomplete**, so connection attempts fail explicitly and no tunnel is created yet. See `IMPLEMENTATION_STATUS_v0.20.md`.


## v0.21 status
The selected Shadowsocks profile is now serialized to a sing-box JSON file in app-private storage before service startup. The actual libbox `CommandServer` / `PlatformInterface` adapter is still incomplete, so this source does not yet establish a working VPN tunnel. Do not treat it as a completed VPN or build/release it as such.


## v0.22 status
The generated TUN configuration now uses the current `address` field with IPv4 and IPv6 prefixes. This is a configuration compatibility correction only; libbox runtime integration and device testing remain incomplete. See `IMPLEMENTATION_STATUS_v0.22.md`.


## v0.23 status
See `IMPLEMENTATION_STATUS_v0.23.md`. This update fixes the generated sing-box 1.14 DNS schema and removes the temporary password-bearing runtime config on stop/failure. It does not yet start libbox or create a VPN tunnel. APK build remains deferred.


## v0.24 status
Permission/lifecycle safety checks were hardened. Real libbox CommandServer/PlatformInterface/TUN integration is still pending and this revision does not claim a working VPN tunnel. APK build remains deferred.


## v0.27.0
See `IMPLEMENTATION_STATUS_v0.27.0.md` for the restored service chooser, AdMob + UMP implementation, test-ad safety switch, and remaining VPN engine/build gates.
