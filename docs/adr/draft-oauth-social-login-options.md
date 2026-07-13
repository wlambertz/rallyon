# ADR Draft: OAuth/OIDC-Based Social Login Options

## Status

Draft. This document records a July 2026 research snapshot answering a narrow question: should RallyOn add "Sign in with Google/Microsoft/etc." as a login option, and if so, how? Like `docs/adr/draft-passwordless-login-magic-links-otp.md`, this is exploratory research for RallyOn's not-yet-built "Authentifizierung & Autorisierung" module (`wiki/Architecture/Modules.md`), not scheduled work. `application/organizer` has no frontend today, so there is no login UI this ADR displaces, and this ADR changes nothing in `3rd_party/iam`, `admin/keycloak/`, or `service/tournamentmgmt`. This ADR cross-references, but does not modify, the "Social login" row already present in the passwordless ADR's comparison table.

## Context

"OAuth login" is a common but imprecise phrase. OAuth 2.0 (RFC 6749) is an **authorization** framework — it answers "what can this application access?" via scoped access tokens. It was never designed as an authentication protocol. What people actually mean by "Sign in with Google" is **OpenID Connect (OIDC)**, the authentication layer built on top of OAuth 2.0 that adds an ID token and standardizes identity claims — OIDC answers "who is this user?" [OAuth 2.0 vs OpenID Connect (Okta)](https://developer.okta.com/docs/concepts/oauth-openid/) This ADR uses **OIDC-based social login** as the precise term, the same way the passwordless ADR insists on "email OTP" rather than "TOTP."

RallyOn has no OAuth/OIDC social login today. `3rd_party/iam` is Keycloak-as-resource-server only (`keycloak-core` for JWT/claim handling, `keycloak-spring-starter` for Spring Security auto-configuration), and `admin/keycloak/` provisions only first-party realm login — no external identity provider is configured. Keycloak, however, already has this capability built in: **Identity Brokering** lets Keycloak treat external providers (Google, Microsoft, GitHub, and others) as upstream IdPs. When a user clicks "Sign in with Google," Keycloak redirects to Google, exchanges the authorization response for tokens, and either creates a new local user or links to an existing one via a configurable "first broker login" flow. [Keycloak identity brokering / social login setup](https://skycloak.io/blog/keycloak-social-login-google-apple-github/), [Red Hat build of Keycloak: Integrating identity providers](https://docs.redhat.com/en/documentation/red_hat_build_of_keycloak/26.0/html/server_administration_guide/identity_broker) If RallyOn ever adds social login, brokering through the existing Keycloak instance is the natural fit — it avoids standing up a second identity system and can preserve the existing `rallyon_user_id` claim contract that `3rd_party/iam/AGENTS.md` treats as security-sensitive.

## Proposed Decision

If RallyOn adds OIDC-based social login, it should follow four rules:

1. **Implement via Keycloak identity brokering**, not a parallel identity system. Configure Google/Microsoft (or other providers) as Keycloak identity providers, so brokered logins still terminate in a Keycloak-issued token carrying the existing `rallyon_user_id` claim and role mappings that `service/tournamentmgmt` already trusts.
2. **OAuth/OIDC must never be the sole login option for an account — this is a hard requirement, not a trade-off to weigh.** Every account must retain, or be prompted to set up, at least one first-party method (email OTP/magic link per the passwordless ADR, or eventually a passkey) alongside any linked social login. The reason is structural: the external IdP account becomes a single point of failure the moment it's the only path in. If Google suspends, locks, or the user loses access to that account, they lose access to RallyOn too — a risk documented repeatedly in real-world account-lockout cases with no reliable recourse. [Google account lockout consequences](https://www.androidpolice.com/2021/03/08/when-google-locks-you-out-of-your-account-begging-the-internet-for-help-is-your-first-and-last-resort/), [OAuth single point of failure](https://scorchingtech.com/oauth-security-risks-log-in-with-google-guide/)
3. **Never silently auto-link accounts on an unverified email claim.** Keycloak's first-broker-login flow must require proof of ownership (existing password/OTP/passkey confirmation) before linking a social login to an existing RallyOn account — matching general account-linking guidance from Google, Auth0, and Ory to never trust an `email` claim for linking decisions without independent verification or explicit user confirmation. [Secure account linking (Ory)](https://www.ory.com/blog/secure-account-linking-iam-sso-oidc-saml), [Google account linking with OAuth](https://developers.google.com/identity/account-linking/oauth-linking)
4. **Request minimal OIDC scopes** (`openid profile`, not broader), and disclose to the user exactly which claims are shared with RallyOn from the chosen provider, consistent with GDPR data-minimization expectations for third-party data sharing. [Privacy and GDPR using OAuth (Curity)](https://curity.io/resources/learn/privacy-and-gdpr/)

## Options Considered

| Option | Description | Assessment |
| --- | --- | --- |
| **No social login (status quo)** | Keep Keycloak first-party-only, plus whatever email/passkey methods are eventually adopted | Simplest, no third-party dependency, but no low-friction option for users who prefer not to manage another credential |
| **OIDC social login as an additive option, via Keycloak brokering (recommended if pursued)** | Google/Microsoft configured as Keycloak identity providers; every account keeps a first-party fallback method | Lowest signup friction for users who want it, reuses existing Keycloak infrastructure and claim contract, but adds dependency on external IdP policies for that login path |
| **OIDC social login as the sole login method** | No first-party fallback; account existence depends entirely on the linked provider account | **Explicitly rejected.** A suspended, deleted, or compromised provider account permanently locks the user out of RallyOn with no recourse — unacceptable for a product where organizers need reliable account access during live tournaments |

Realistic first-candidate providers, if pursued, are **Google and Microsoft** given RallyOn's likely organizer/participant user base — both are directly supported by Keycloak's identity-brokering feature without custom code. GitHub is a common third option in generic implementations but is a weaker fit for a badminton-tournament audience. [Keycloak social login setup](https://skycloak.io/blog/keycloak-social-login-google-apple-github/)

A note on security framing: OIDC-based social login is a **convenience/reach** option, not a security upgrade path the way passkeys are. NIST's federation model (SP 800-63C) treats the federation assertion as something the relying party validates, but the actual authentication strength (which factors the user proved) is established entirely at the IdP, outside RallyOn's control — RallyOn would be borrowing whatever assurance Google/Microsoft chose to enforce, not adding a distinct factor of its own. [NIST SP 800-63C: Federation and Assertions](https://pages.nist.gov/800-63-3/sp800-63c.html)

## Consequences

- No code, dependency, or Keycloak realm configuration changes are introduced by this ADR today.
- If implemented, a subset of logins would depend on external IdP availability and account-standing decisions outside RallyOn's control — mitigated, but not eliminated, by rule 2 (never the sole method).
- Implementation would require configuring Keycloak's first-broker-login flow and identity-provider settings in `admin/keycloak/` realm provisioning, and verifying that brokered logins still populate `rallyon_user_id` and role claims exactly as first-party logins do today, per the contract in `3rd_party/iam/AGENTS.md`.
- Product UX must handle the "user only has a social login, no first-party fallback yet" state — e.g., prompting to add an email/passkey method rather than allowing an OAuth-only account to exist unmitigated.

## Open Follow-Up Work

- Decide which provider(s) to support first (Google and/or Microsoft are the likely candidates).
- Design the UX for prompting OAuth-only signups to add a first-party fallback method, and for account-linking confirmation on first broker login.
- Revisit once the "Authentifizierung & Autorisierung" module and the organizer frontend stack (`docs/adr/draft-organizer-platform-target.md`) are actually scheduled — this ADR is not actionable until then.
- Cross-check this ADR's recommendations against `docs/adr/draft-passwordless-login-magic-links-otp.md` if both are pursued together, so the "first-party fallback" requirement here and the "low/medium-risk" framing there stay consistent.

## References

- `docs/adr/draft-passwordless-login-magic-links-otp.md`
- `docs/adr/draft-organizer-platform-target.md`
- `wiki/Architecture/Modules.md`
- `3rd_party/iam/AGENTS.md`
- [RFC 6749: The OAuth 2.0 Authorization Framework](https://www.rfc-editor.org/rfc/rfc6749.html)
- [OAuth 2.0 and OpenID Connect overview (Okta Developer)](https://developer.okta.com/docs/concepts/oauth-openid/)
- [NIST SP 800-63C: Federation and Assertions](https://pages.nist.gov/800-63-3/sp800-63c.html)
- [Keycloak Social Login: Google, Apple, and GitHub setup](https://skycloak.io/blog/keycloak-social-login-google-apple-github/)
- [Red Hat build of Keycloak: Integrating identity providers](https://docs.redhat.com/en/documentation/red_hat_build_of_keycloak/26.0/html/server_administration_guide/identity_broker)
- [OAuth "Log in with Google" single point of failure risks](https://scorchingtech.com/oauth-security-risks-log-in-with-google-guide/)
- [Google account lockout consequences](https://www.androidpolice.com/2021/03/08/when-google-locks-you-out-of-your-account-begging-the-internet-for-help-is-your-first-and-last-resort/)
- [Secure account linking (Ory)](https://www.ory.com/blog/secure-account-linking-iam-sso-oidc-saml)
- [Google Account Linking with OAuth](https://developers.google.com/identity/account-linking/oauth-linking)
- [Privacy and GDPR using OAuth (Curity)](https://curity.io/resources/learn/privacy-and-gdpr/)
