# Falcon VPN v0.24 — permission and lifecycle hardening

## Implemented
- Bumped app version to 0.24.0 / versionCode 24.
- Added a defensive `VpnService.prepare()` check inside the service, so a direct/system start cannot proceed when VPN consent is missing.
- Added a dedicated `NEEDS_PERMISSION` failure path instead of classifying missing consent as a generic engine error.
- Kept runtime status honest: `ready` stays false until the real engine and tunnel are verified; `engineIntegrated` remains false.
- Preserved fail-closed behavior: the app does not establish a dummy TUN or display `CONNECTED` without a real engine.

## Still blocking the requested real libbox integration
- The Gradle dependency is declared, but this environment has no resolved `libbox.aar`/generated Java API and no Android SDK/Gradle installation available to compile against it.
- Therefore `PlatformInterface`, `CommandServerHandler`, `CommandServer` start/close lifecycle, and `OpenTun` FD ownership are not yet implemented or compile-verified in this revision.
- No APK was built, per the instruction to defer APK build until all modifications are finished.

## Validation
- Static source review only. Android compile, unit tests, permission-dialog test, service start/stop test, and live tunneled-traffic test have not been run.
