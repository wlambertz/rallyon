# ADR Draft: Organizer App Platform Target — PWA vs. Electron/Tauri-Wrapped Web App

## Status

Draft. This document records a July 2026 research snapshot answering a narrower question than the frontend framework/library choice: should `application/organizer` be built as a Progressive Web App (PWA), or as a plain web app intended to be wrapped later in a desktop shell such as Electron or Tauri? It is not yet an accepted project baseline. It does not itself introduce a frontend framework, UI library, or dependency — `application/organizer/AGENTS.md` still requires an explicit maintainer decision before any frontend stack is introduced.

## Context

`application/organizer` is currently a placeholder; the previous Angular frontend was removed while the frontend stack is reconsidered. The design brief in `wiki/design/frontend-spaceport-theme.md` frames the organizer UI as a "tournament operations terminal" for staff who need to "scan status and act," with a responsive Swiss-style grid (12 columns desktop, 8 tablet, 4 mobile) — implying desktop, tablet, and mobile use are all expected. `docs/adr/draft-design-tools-and-design-token-workflow.md` further notes RallyOn is expected to grow beyond one client runtime over time, so the design system is deliberately kept platform-agnostic.

No offline requirement is documented anywhere today. The "on-site operations terminal" framing (staff running tournaments at venues, potentially with unreliable Wi-Fi) makes offline resilience a plausible future need, but this is an inference, not a stated requirement.

## Proposed Decision

If this ADR is accepted, `application/organizer` should default to being built as a **Progressive Web App (PWA)** rather than a plain web app that assumes an Electron or Tauri desktop wrapper. Electron/Tauri wrapping should be deferred until a concrete desktop-only requirement is identified that a browser genuinely cannot satisfy — for example local hardware integration (USB scoreboards, label/bracket printers) or offline-first local storage needs beyond what a service worker can provide.

This ADR does not select a frontend framework or library; it only addresses the platform-delivery target once a framework decision is made.

## Options Considered

### Progressive Web App (PWA)

A PWA ships a single codebase installable directly from the browser, updates automatically on load, and runs across desktop, tablet, and mobile without separate per-platform builds. Its footprint is small (PWAs are commonly under 1MB versus Electron's 80–150MB+ baseline RAM/disk use), and service workers provide offline caching. The trade-off is that it is constrained to whatever native APIs browser vendors expose — deep filesystem or OS integration is limited.

The main weak point is iOS/Safari: there is no automatic install prompt (users must manually use "Add to Home Screen"), no background sync, tighter and evictable storage (a 7-day cap on unused PWAs), no App Store listing for discovery, and additional restrictions that have appeared in the EU where Apple has at times pulled standalone PWA support. For a staff-facing organizer tool, this mainly matters if staff are expected to use iPhones/iPads; desktop and Android usage are largely unaffected.

### Web App Wrapped in Electron

Electron gives a web app full native OS access — filesystem, system tray, native menus, and hardware such as USB devices or printers — by bundling a full Chromium and Node.js runtime with the app. This comes at a real resource cost: 80–150MB+ RAM at idle and 50MB+ installers are typical, plus a separate release/update pipeline per target OS. Electron apps are desktop-only; they cannot be installed on mobile devices.

### Web App Wrapped in Tauri

Tauri is a lighter-weight alternative to Electron: it reuses the operating system's native WebView instead of bundling Chromium, yielding roughly 20–40MB RAM usage and installers under 10MB, with meaningfully better battery life. Its cost is a Rust layer in the toolchain, which is a real addition for a project that otherwise favors explainable, incrementally learnable technology choices (per this repo's Educational Project Principle). Like Electron, Tauri is desktop-only.

### Common industry framing

A recurring pattern across the sources below: build the web app as a PWA first, and only reach for an Electron or Tauri shell once a specific capability the browser cannot expose is actually needed — wrap the PWA in a desktop shell once you've outgrown browser constraints, rather than designing for the desktop shell from the start.

## Consequences

- The organizer app gains multi-device reach (desktop, tablet, mobile) without maintaining a separate desktop build or release pipeline.
- If tournament staff are expected to use iPhones/iPads on-site, PWA install and offline behavior on iOS/Safari is weaker than on desktop or Android; this should be confirmed with the maintainer before committing if that usage pattern is likely.
- The option to add an Electron or Tauri desktop shell later stays open and low-cost, since it wraps an existing web app rather than requiring a rewrite — consistent with keeping experiments scoped and reversible.
- No decision is made here about which frontend framework or library to use; that remains a separate, explicit decision gated by `application/organizer/AGENTS.md`.

## Open Follow-Up Work

- Confirm with the maintainer whether tournament staff are expected to use iOS/Safari (iPhone/iPad) on-site, given the PWA limitations on that platform noted above.
- Revisit this ADR once an actual frontend framework/library is chosen for `application/organizer`, since PWA tooling (service worker setup, manifest, installability) is framework-dependent.
- Reassess if a concrete desktop-only requirement emerges (local hardware integration, offline-first storage beyond service-worker limits) that would justify introducing an Electron or Tauri wrapper.

## References

- RallyOn organizer design brief: `wiki/design/frontend-spaceport-theme.md`
- `application/organizer/AGENTS.md`
- `docs/adr/draft-design-tools-and-design-token-workflow.md`
- [PWA vs Electron: A Deep Dive (SimiCart)](https://simicart.com/blog/pwa-vs-electron/)
- [PWA vs Electron — Which Architecture Wins? (Clean Commit)](https://cleancommit.io/blog/pwa-vs-electron-which-architecture-wins/)
- [Why Basecamp Ditched Electron for a PWA (Tevpro)](https://tevpro.com/why-basecamp-ditched-electron-for-a-pwa-and-why-it-matters/)
- [PWA iOS Limitations and Safari Support 2026 (MagicBell)](https://www.magicbell.com/blog/pwa-ios-limitations-safari-support-complete-guide)
- [Tauri vs Electron: A Practical Guide (RaftLabs)](https://raftlabs.medium.com/tauri-vs-electron-a-practical-guide-to-picking-the-right-framework-5df80e360f26)
- [Electron vs. Tauri (DoltHub Blog)](https://www.dolthub.com/blog/2025-11-13-electron-vs-tauri/)
- [Tauri vs Electron 2026 (tech-insider.org)](https://tech-insider.org/tauri-vs-electron-2026/)
