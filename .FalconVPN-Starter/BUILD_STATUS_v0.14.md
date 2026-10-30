# Falcon VPN v0.14 — build pipeline correction

## Work performed
- Corrected GitHub Actions to run Gradle commands from `vpn-starter/`, where `settings.gradle.kts` and the app module actually live.
- Corrected the uploaded APK artifact path to include `vpn-starter/` from the repository root.
- Bumped Android version metadata to `versionCode 14`, `versionName 0.14.0`.

## What this fixes
The previous workflow attempted to run Gradle at the repository root and looked for the APK under the wrong directory. This version fixes those path mismatches.

## Not yet verified
The workflow has not been run on GitHub from this environment. No APK has been built here because this environment has Java but no Gradle executable or Android SDK. This project still does not implement the libbox PlatformInterface/TUN lifecycle; a successful APK build would not by itself mean the VPN tunnel works.
