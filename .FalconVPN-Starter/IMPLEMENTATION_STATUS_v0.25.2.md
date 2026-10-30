# Falcon VPN v0.25.2 — server profile management preparation

## Implemented in source
- Added a native bridge method to save manually entered Shadowsocks profiles.
- Shadowsocks credentials are passed directly to Android native code and stored by the existing AES-GCM Android Keystore-backed `SecureProfileStore`; the password is not returned to JavaScript.
- Added native profile refresh on app load and after save, and native selection of saved Shadowsocks profiles.
- The server list displays saved Shadowsocks name, host, port and cipher method.
- Other protocol forms remain preview-only in this revision. Credential fields are redacted from their in-memory preview object; they are not persisted as working profiles.
- Updated version metadata to 0.25.2 / versionCode 27.

## Not implemented / not verified
- VLESS, VMess, Trojan, Hysteria2, TUIC, WireGuard and subscription parsing/storage for the real engine.
- libbox CommandServer + PlatformInterface + Android TUN handoff.
- Real ping/latency checks, automatic server selection, AdMob rewarded ads, APK build, device testing or live-server testing.
- Build cannot be verified in this environment because Android SDK and Gradle are unavailable.

**Safety:** Saving/selecting a profile does not mean the VPN is connected. The app continues to fail closed until the real engine integration is completed and tested.
