# Falcon VPN v0.21 — sing-box config preparation

## Implemented
- Added `SingBoxConfigWriter`, which converts the selected saved Shadowsocks profile into a sing-box JSON configuration.
- Writes the config only to the app-private files directory and avoids logging credentials.
- The VPN service prepares the config during connection startup and removes it on stop, revoke, or service destruction.
- Version bumped to 0.21.0 / code 21.
- Kept fail-closed behavior: the service does not create a dummy TUN and does not report CONNECTED.

## Not yet implemented
- The real libbox `CommandServer` lifecycle and the complete generated `PlatformInterface` implementation are still missing.
- No TUN descriptor is opened and no packets are forwarded. Thus this revision is not yet a functioning VPN connection.
- Android/Gradle compilation and device tests have not been run; APK build is intentionally deferred.

## Next required work
Integrate the exact `io.nekohasekai.libbox` 1.14.0 generated Java API, including `PlatformInterface`, `TunOptions` -> `VpnService.Builder.establish()`, FD ownership, default-network callbacks, `CommandServer.startOrReloadService()`, and clean shutdown. Review GPL-3.0-or-later obligations before redistribution.
