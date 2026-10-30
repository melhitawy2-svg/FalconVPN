# Falcon VPN v0.25.6 — external import groundwork

## Included
- Android intent entry points for `ss://` links, shared text, and common text/JSON/octet-stream file MIME types.
- Reads incoming content URIs through `ContentResolver` (no broad storage permission).
- Detects Shadowsocks share links in incoming text, parses them with the existing parser, stores profiles in the existing encrypted profile store, and selects the last successfully imported profile.
- Shows a user-facing status message and rejects unsupported formats rather than silently pretending to convert them.

## Important limitations
- This is not a universal configuration converter. A config from an app such as LTS can be a proprietary JSON/YAML schema, a subscription link, or a protocol-specific share URI. Its exact format must be identified and a matching parser/normalizer implemented.
- Current incoming import support is for `ss://` share links only. VLESS, VMess, Trojan, Clash YAML, sing-box JSON, and app-specific exports are not converted by this change.
- A profile can be imported and selected, but a working VPN tunnel still depends on completing and testing the native sing-box/libbox integration.
- Android build and device tests have not been run in this environment.
