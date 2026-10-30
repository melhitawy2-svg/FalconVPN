# Falcon VPN v0.20 — VPN lifecycle groundwork

## Implemented in this source revision
- Main screen power button calls the native Android bridge rather than simulating a connected state.
- Native bridge checks that a saved profile is selected before requesting VPN permission.
- Added Android VPN consent flow using `VpnService.prepare()`.
- Requests notification permission on Android 13+ before starting the service.
- Service starts as a foreground service with a notification channel and stop action.
- Runtime state (`DISCONNECTED`, `NEEDS_PERMISSION`, `CONNECTING`, `ERROR`) is persisted and returned to the WebView.
- Permission denial, missing profile, and incomplete engine integration fail safely; the app does not create an empty TUN or claim `CONNECTED`.
- Removed the previous fake UI connection toggle.

## Important: engine integration is NOT complete
The dependency on `com.github.singbox-android:libbox:1.14.0` exists, but this revision does **not** yet instantiate the real libbox `CommandServer`/`BoxService` or implement the complete generated `io.nekohasekai.libbox.PlatformInterface` adapter. Therefore no VPN tunnel or traffic forwarding is established. The service deliberately displays an explicit error instead of black-holing traffic or reporting a false connection.

## Required next implementation step
Port/adapt the required platform integration from the official sing-box Android client under its GPL-3.0-or-later license, including `PlatformInterfaceWrapper`, `TunOptions` -> `VpnService.Builder`, FD ownership, default network monitoring, `CommandServer.startOrReloadService()`, and close/revoke lifecycle. Review license and attribution before redistribution. Then compile against the exact libbox 1.14.0 Java API and test on a device.

## Verification
- ZIP integrity and JavaScript syntax should be checked after packaging.
- Android/Gradle compilation and device tests were not run in this environment.
- APK build remains intentionally deferred until the requested UI and engine integration work is complete.
