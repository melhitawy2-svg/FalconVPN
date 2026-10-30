# Falcon VPN v0.25.3 — bulk Shadowsocks import

## Implemented in source
- Added native `importShadowsocksLinks(text)` bridge method for newline-separated SIP002 `ss://` links.
- Imports up to 100 non-empty, non-comment lines per operation and caps input size at 200 KB.
- Each valid link is parsed natively and saved through the existing Android Keystore AES-GCM encrypted profile store.
- Invalid rows are reported by their line number only; credentials are never returned to JavaScript or written to logs.
- Returns refreshed saved-profile metadata after import and increments version metadata to 0.25.3 / versionCode 28.

## Not implemented / not verified
- The existing UI has not yet been wired to call this bulk-import bridge method.
- Only Shadowsocks SIP002 links are supported by this importer; other protocols remain unsupported for real use.
- No live connection, ping test, subscription URL fetching, AdMob, APK build, or device test.
- Build cannot be verified here because Android SDK and Gradle are unavailable.

**Safety:** Importing profiles only stores credentials encrypted on-device. It does not establish a VPN connection.
