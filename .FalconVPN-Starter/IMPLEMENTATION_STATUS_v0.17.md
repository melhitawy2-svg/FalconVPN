# Falcon VPN v0.17 — implementation checkpoint

## Implemented in this pass
- Added a narrow native JavaScript bridge to the bundled local WebView asset.
- Added native validation of Shadowsocks SIP002 links using the existing Kotlin parser.
- The import result now shows protocol, profile label, host, port, and cipher method without returning or logging the password.
- The UI explicitly says that validation does not save the profile or connect to the server.
- Added a native engine-status response that reports `ENGINE_NOT_INTEGRATED` honestly.
- Updated app version metadata to `0.17.0` / versionCode `17`.

## Validation performed
- Extracted the bundled inline JavaScript and ran `node --check`: passed.
- Source-level review only. No Gradle/Android SDK build was run, no APK was generated, and no device/server connection was tested.

## Remaining before APK build
1. Review the complete blue UI and replace any remaining preview-only actions with explicit status or real implementations.
2. Implement encrypted local profile persistence and selection.
3. Integrate the exact sing-box libbox Android API and `PlatformInterface` lifecycle.
4. Implement Android VPN consent, foreground service/notification, `VpnService.Builder.establish()` TUN handoff, routing/DNS, and engine callbacks.
5. Connect the UI to real native connection states and add lifecycle/config tests.
6. Build APK only after the above work, then install and test against a live server.

## Safety invariant
Falcon VPN must not show CONNECTED, fabricated throughput, or a running timer until the real engine confirms an active tunnel.
