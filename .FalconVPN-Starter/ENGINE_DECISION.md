# Engine decision — Falcon VPN

Current candidate: Android `libbox` from sing-box, pinned to `1.14.0` in `app/build.gradle.kts`.

Reason: upstream distributes an Android library through JitPack and sing-box supports Shadowsocks outbound configuration plus Android TUN/VpnService integration.

Not yet complete: selecting and implementing the app-side service/command bridge, VPN consent flow, foreground notification, real connection state, diagnostics, and end-to-end tests.

License: upstream sing-box/libbox is GPL-3.0-or-later; check the actual artifact/repository license and any additional terms before distributing a closed-source or branded derivative.
