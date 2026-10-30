# Falcon VPN v0.19 — saved profile selection

## Implemented in this checkpoint
- Added native selection state for a saved Shadowsocks profile. Only the profile UUID is stored as selection metadata; profile credentials remain encrypted at rest by the existing Android Keystore AES-GCM store.
- Added native bridge methods to select a saved profile and query its safe metadata.
- Added a saved-profiles section in the blue UI: refresh list, display name/server/port/cipher, select a profile, and delete a profile.
- Refreshes the list after successful import and when the import panel opens.
- Removing the selected profile clears the selected-profile pointer.
- UI text explicitly states that selecting a profile does not connect the VPN.
- Version metadata bumped to 0.19.0 / versionCode 19.

## Still not complete
- sing-box/libbox `PlatformInterface` / `BoxService` integration and TUN descriptor handoff.
- Android VPN permission consent, foreground service notification and real engine lifecycle callbacks.
- Real connection state, DNS/routing verification, live server handshake and tunneled traffic tests.
- APK build/install is intentionally deferred until the requested design and service work is complete.

## Validation status
- Source-level edits completed.
- JavaScript syntax and Android compilation have not yet been validated in this environment; no APK is claimed to have been built.
