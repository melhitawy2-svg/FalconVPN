# Falcon VPN v0.16 — implementation checkpoint

## Completed in this pass
- Removed the dashboard's initial `connected` map styling, which visually implied an active tunnel before any engine connection existed.
- Removed hard-coded upload/download speeds and the fabricated elapsed timer from the initial screen.
- Updated the DNS diagnostic hint so it does not imply a live VPN session.
- Bumped Android version metadata to `0.16.0` / versionCode `16`.

## Important status
This pass is a UI-integrity fix, not a working VPN engine. The Connect action still correctly reports that the real engine is not wired. `FalconVpnService` still fails closed and does not establish a TUN tunnel. No APK was built, and no live server/device test was run; APK building remains deferred until the design and services are implemented.

## Next implementation target
Implement and verify the Android VPN consent/service lifecycle, then integrate the exact libbox API and TUN descriptor handoff. Do not display CONNECTED or measured throughput until the engine confirms it.
