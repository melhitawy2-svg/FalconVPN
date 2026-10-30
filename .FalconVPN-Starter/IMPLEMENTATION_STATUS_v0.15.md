# Falcon VPN v0.15 — truthful connection state and parser check

## Changes made
- Removed the fake initial connected state and fake elapsed connection time from the WebView UI.
- The connect button no longer toggles to a fabricated connected state; it explains that the native engine is not yet wired.
- Kept the Android VPN service fail-closed: it does not create a tunnel or claim a connection.
- Bumped Android version metadata to 0.15.0.
- The existing JVM test includes the supplied SIP002 Shadowsocks link shape and checks host, port, method, password, and label parsing.

## Still required for real VPN connectivity
- Implement and compile the libbox 1.14.0 `PlatformInterface` and `BoxService` lifecycle adapter against the actual AAR signatures.
- Implement `OpenTun` using `VpnService.Builder.establish()` and correct FD ownership/routing/DNS behavior.
- Add Android VPN consent flow and foreground-service lifecycle/notification.
- Bridge config import/selection from the WebView UI to native code without exposing secrets to logs.
- Build with Android SDK/Gradle, install on a device, and verify server handshake and tunneled traffic.

## Verification limitation
This environment has Java but no Gradle executable or Android SDK configured, so this archive has not been compiled here. No live connection to the supplied server is claimed.
