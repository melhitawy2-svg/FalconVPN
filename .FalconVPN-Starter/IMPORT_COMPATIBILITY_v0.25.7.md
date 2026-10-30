# Falcon VPN v0.25.7 — VLESS share-link import groundwork

## Added
- Parses common `vless://UUID@host:port?...` share links.
- Handles TLS + WebSocket transport, SNI, WebSocket Host header, and double-encoded paths such as `%252Frealtime`.
- Stores VLESS profiles in the existing encrypted Android profile store.
- Incoming Android intents can accept `vless://` links; incoming text import recognizes `ss://` and `vless://`.
- Runtime config writer now emits a sing-box VLESS outbound for TLS + WS and Shadowsocks outbound for SS profiles.

## Limits
- This source change has not been compiled or device-tested here because Android SDK/Gradle are unavailable in this environment.
- A valid imported profile is not proof the remote server is reachable, nor that the VPN tunnel works. The native libbox/TUN integration remains incomplete in this project.
- VLESS `reality` and non-WS non-TCP transports are intentionally rejected by the config writer until their full parameters are modeled. The parser may read the URI, but it will not generate a working runtime config for those modes.
- Android URL-handler behavior can vary by source app; if Instagram does not expose Falcon VPN for a link, use Share/Open with or paste the link into Falcon's import UI.
