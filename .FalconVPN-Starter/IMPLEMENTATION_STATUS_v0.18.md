# Falcon VPN v0.18 — encrypted profile persistence checkpoint

## Implemented
- Added native `SecureProfileStore` using AES-GCM encryption with an AES key held by Android Keystore.
- Shadowsocks links imported through the app are parsed natively and saved encrypted at rest; the password is not returned to the WebView or printed to logs.
- Added native bridge methods to list safe profile metadata and delete a saved profile.
- Duplicate imports matching name + host + port update the existing stored entry rather than creating another entry.
- Import UI now clearly reports that saving a profile does not mean the VPN is connected.
- Version metadata is now `0.18.0` / versionCode `18`.

## Not implemented yet
- No profile-selection UI is wired to the native saved profile list yet.
- The sing-box/libbox `PlatformInterface` / `BoxService` adapter and TUN descriptor handoff are still not implemented.
- VPN permission request flow, foreground notification lifecycle, real connect/disconnect callbacks, DNS/routing validation, and live tunnel telemetry remain pending.
- No APK was built, and no device/server connection test was run. APK build remains deferred until design and service integration are complete.

## Security notes
- Profile secrets are encrypted at rest with Android Keystore AES-GCM.
- The app currently stores profile records locally only. This is not cloud backup or account sync.
- Reinstalling/clearing app data can make the local profiles unavailable.
- Do not show CONNECTED, traffic speeds, or a connection timer until the real engine confirms an active tunnel.
