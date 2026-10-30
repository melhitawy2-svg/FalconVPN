# Falcon VPN v0.25.4 — Arabic country service selector

- Preserved the existing v41 blue interface and existing server/service layout.
- Added a compact horizontal row of all 22 Arab League country flags above the server selector.
- Added automatic best-effort country detection using IP country lookup; manual selection overrides detection.
- Selecting a country updates the active flag and filters the single service dropdown to providers/categories for that country.
- Added an explicit empty-state option: country-specific configurations must still be supplied and verified before they can be offered. No fake VPN profiles were created.
- Detection requires internet access; if lookup fails, Egypt is the default and users can choose manually.
- Not built or device-tested in this environment. This is a source-project update, not a ready APK.
