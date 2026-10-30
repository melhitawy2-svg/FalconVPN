# Falcon VPN v0.27.0 — UI restoration + AdMob integration stage

## Included
- Bundled `app/src/main/assets/index.html` synchronized to the latest supplied preview: service chooser restored (protocol/service list with dedicated forms) and country-flag country grid removed from the server/service chooser flow.
- Added Google Mobile Ads Android SDK 25.5.0 and UMP SDK 4.0.0.
- Added the AdMob App ID supplied by the owner to AndroidManifest.xml.
- Added native banner placement bridge for the existing `.ad-slot` location in the HTML.
- Added native interstitial loading and a first-connected-state hook with a 30-minute cooldown. It will only display after the engine reports CONNECTED.
- Added native rewarded-ad flow. The UI receives the +15-minute reward only from the Google SDK earned-reward callback; JavaScript no longer simulates or grants rewards.
- Rewarded time is persisted locally, consumed by a future successful VPN session, or added to the active native timer if a session exists.
- Added UMP consent update/form flow and only initializes ad requests when `canRequestAds()` is true.
- Development builds intentionally use Google's official test ad unit IDs (`useTestAds = true`). Production ad unit IDs supplied by the owner are retained in source and require an explicit switch after review/testing.
- Version set to 0.27.0 / versionCode 34.

## Not completed / must not be represented as working
- The sing-box/libbox `CommandServer` + `PlatformInterface` + Android TUN adapter is still not implemented. `FalconVpnService` continues to fail closed rather than claiming a false VPN connection.
- Since the VPN engine does not yet reach CONNECTED, the first-connect interstitial hook and session extension cannot be end-to-end verified in this source state.
- No Gradle wrapper, Gradle executable, Android SDK, or device is available in this environment. Therefore no Android build/APK installation or runtime ad test was performed.
- The AdMob account's privacy messaging and app review status still need owner-side verification in AdMob.

## Build and release gate
1. Open the project in Android Studio or use an environment with Android SDK + Gradle.
2. Sync Gradle and resolve dependencies.
3. Test UMP consent in the UK/EEA and other target geographies.
4. Test banner, interstitial, and rewarded formats using test units only.
5. Complete the libbox/TUN adapter and verify real traffic before enabling VPN connection claims.
6. After AdMob review and test validation, set `useTestAds = false` to use the supplied production ad units.

## Added in execution follow-up
- Added `.github/workflows/android-build.yml` so GitHub Actions can build a debug APK on pushes to `main`/`master` or manually via Actions > Falcon VPN Android Build > Run workflow.
- The workflow uses JDK 17 and Gradle 8.9, runs `:app:assembleDebug`, and uploads `app-debug.apk` as a downloadable workflow artifact.
- This workflow has been authored locally but has NOT yet been uploaded to GitHub or executed. Its first run is the actual Gradle/Kotlin compile check.
