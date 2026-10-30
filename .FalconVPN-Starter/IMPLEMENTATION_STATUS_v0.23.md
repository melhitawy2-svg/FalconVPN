# Falcon VPN v0.23 — DNS schema and lifecycle cleanup

## Implemented in this revision
- Updated generated sing-box DNS server configuration to the typed server format (`type: udp`, `server`, `server_port`, `detour`) and set `dns.final`.
- Bumped Android version to 0.23.0 / versionCode 23.
- Delete the temporary runtime configuration (which contains the selected proxy password) on explicit stop and failed startup, as well as revoke/destroy.
- Clarified the runtime error message: preparing JSON is not equivalent to starting libbox or establishing a TUN tunnel.

## Not yet implemented / not verified
- `PlatformInterface` implementation compatible with the generated libbox 1.14.0 Java API.
- `CommandServer` lifecycle and callback handlers.
- `OpenTun` using `VpnService.Builder.establish()` and correct file-descriptor ownership.
- Real engine start/stop and verified tunneled traffic.
- Android build/install test. APK remains intentionally deferred.

This revision does not claim to provide a working VPN connection.
