# ADR Draft: Passwordless Login via Magic Links and Email OTP

## Status

Draft. This document records a July 2026 research snapshot answering a narrow question: can a SaaS product define passwordless login using only magic links and email-delivered one-time codes, and how would that compare to stronger alternatives? It is exploratory research for RallyOn's not-yet-built "Authentifizierung & Autorisierung" module (`wiki/Architecture/Modules.md`), not a scheduled implementation. `application/organizer` has no frontend today, so there is no login UI this ADR displaces. This ADR does not change anything in `3rd_party/iam` or `service/tournamentmgmt`. If a mechanism like this is ever adopted, it must preserve the existing Keycloak `rallyon_user_id` claim contract and JWT issuer/audience validation described in `3rd_party/iam/AGENTS.md` — either as a Keycloak-native authenticator/flow, or as a separate service that delegates final token issuance to Keycloak rather than replacing it.

## Context

The question assumes no particular constraints on scale, tech stack, hosting model, or build-vs-buy. Under that assumption, login via **magic links** and **email-delivered one-time codes** is technically straightforward but architecturally weaker than WebAuthn/passkeys, and in many B2B scenarios weaker than enterprise SSO/OIDC. The reason is structural: both mechanisms ultimately depend on the security and deliverability of the user's email mailbox — the mailbox is simultaneously the identity anchor and the delivery channel for the authentication proof. NIST classifies both "OTP entered manually" and "out-of-band" methods as **not phishing-resistant**, and explicitly states that methods which don't prove possession of a specific device — such as email — must not be used for out-of-band authentication, because email accounts are comparatively easy to compromise. [NIST SP 800-63B §5.1.3.1](https://pages.nist.gov/800-63-3/sp800-63b.html), [NIST SP 800-63-4 Authenticators](https://pages.nist.gov/800-63-4/sp800-63b/authenticators/)

A terminology fix matters here: a code delivered by email is not "TOTP" in the strict RFC 6238 sense. Real TOTP requires the prover and verifier to hold a **shared secret** and derive the same code locally from that secret plus the current time — the secret never leaves the user's device or authenticator app. [RFC 6238](https://www.rfc-editor.org/rfc/rfc6238.html) An emailed code instead transports the proof itself over mail infrastructure. This isn't just naming pedantry — it changes the threat model, and product copy, logs, and APIs should call this **email OTP** or **email code**, never "TOTP," to avoid implying a security guarantee it doesn't have.

The recommended product shape is to **not build magic link and email OTP as two separate systems**, but as two UX resolutions of one shared login-challenge primitive. The primary path should be a magic link bound to browser/device state; email OTP is the fallback when link-scanning, mail-app/browser switching, or cross-device opens break the link flow. Okta's own email authenticator implements exactly this pairing — a magic link callback that checks browser state and falls back to manual OTP entry if the check fails — and Auth0 documents the same same-browser/same-device requirement (particularly acute on iOS, where the OS forces email links open in the default browser regardless of which browser initiated the login). [Okta email (OTP/magic link) integration guide](https://developer.okta.com/docs/guides/authenticators-okta-email/main/), [Auth0: magic link same-browser requirement](https://support.auth0.com/center/s/article/passwordless-magic-link-error-The-link-must-be-opened-on-the-same-device-and-browser-from-which-you-submitted-your-email-address)

## Proposed Decision

If RallyOn ever needs a low-friction bootstrap or fallback login method, it should be modeled as follows:

1. **One shared `LoginChallenge` primitive**, not two systems. A challenge carries purpose, recipient, expiry, state binding, attempt count, delivery metadata, and single-use status. Magic link and email OTP are two ways to resolve the same challenge.
2. **Magic link is primary, email OTP is fallback.** The link is bound to the browser/state that started the login; on mismatch, fall back to manual OTP entry in the original tab rather than silently failing.
3. **Treat this as a low-to-medium-risk mechanism only.** It's appropriate for bootstrap flows, account recovery, or low-stakes accounts — not as the sole method for admin, finance, or otherwise sensitive access.
4. **Plan an explicit roadmap to passkeys and/or OIDC-SSO** for anything higher-value. NIST requires at least one phishing-resistant option at AAL2, and WebAuthn/passkeys are the standard way to get there. [FIDO2/WebAuthn phishing resistance](https://askmeidentity.com/resources/standards/fido2-and-passkeys-explained/)

## Options Considered

| Model | Security level | UX friction | Recommended use |
| --- | --- | --- | --- |
| **Magic link + email OTP** | Low–medium (not phishing-resistant; mailbox is a single point of failure) | Low–medium | Low/medium-risk SaaS, bootstrap, recovery, self-serve trials |
| **Password + app-based TOTP 2FA** | Medium–high | Medium | Existing products where passkeys/SSO aren't yet fully rolled out |
| **Enterprise SSO / OIDC** | High, but dependent on the IdP's own configuration | Low for end users | B2B, mid-market/enterprise, compliance- and admin-heavy products |
| **WebAuthn / Passkeys** | Very high (phishing-resistant, origin-bound public-key credentials) | Very low after enrollment | Modern SaaS, admins, developer tools, high-value accounts |
| **Social login** | Medium–high, provider-dependent | Low | B2C, community, marketplace products |

WebAuthn/passkeys are phishing-resistant because the credential is cryptographically bound to the relying-party origin — the browser refuses to present it to a look-alike domain, so even a successful phishing lure can't extract a usable credential. [FIDO2/WebAuthn phishing resistance](https://askmeidentity.com/resources/standards/fido2-and-passkeys-explained/) Magic links and email OTP have no equivalent binding; a leaked or intercepted link/code is directly usable by whoever holds it.

## Hardening Guidance (if implemented)

- **Bind the magic link to browser-held state**, conceptually similar to how PKCE (RFC 7636) binds a front-channel-delivered authorization code to a value only the originating client holds, preventing bare bearer-token interception. [RFC 7636](https://www.rfc-editor.org/rfc/rfc7636.html) Supabase's recommended email flow follows this shape: the link carries only a `token_hash`, which is exchanged server-side for a session rather than embedding a live session token in the URL. [Supabase: token_hash / PKCE email flow](https://supabase.com/docs/guides/auth/auth-email-passwordless)
- **Never put a session token in a URL.** OWASP's Session Management guidance requires rotating the session ID on login, and using `Secure`, `HttpOnly`, and `SameSite` cookies rather than URL-carried tokens, which leak via logs, browser history, and referrers. [OWASP Session Management Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Session_Management_Cheat_Sheet.html)
- **Single-use, short-lived, rate-limited challenges.** Consume atomically; reject replay. If any JWT is used for a callback or intermediate token, validate `aud`, `iss`, and `typ` explicitly per RFC 8725 to prevent token substitution across different token kinds issued by the same system. [RFC 8725](https://www.rfc-editor.org/rfc/rfc8725.html)
- **Generic responses to prevent user enumeration**, and rate limiting on login/resend to prevent brute force and mailbox-bombing, per OWASP's Authentication guidance. [OWASP Authentication Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html)
- **Treat email deliverability as an auth availability concern, not a marketing concern.** Google's sender requirements (SPF and/or DKIM for all senders, SPF+DKIM+DMARC and spam rate below 0.3% for bulk senders, with enforcement ramping since November 2025) directly affect whether login emails arrive at all. [Gmail email sender guidelines](https://support.google.com/a/answer/81126?hl=en)
- **Minimize and time-box retention** of challenges, OTP values, and auth logs. EDPB guidance on GDPR Article 25 (privacy by design/default) requires deleting or anonymizing data as soon as its processing purpose ends, which applies directly to login-challenge and OTP storage. [EDPB Guidelines 4/2019 on Article 25](https://www.edpb.europa.eu/sites/default/files/files/file1/edpb_guidelines_201904_dataprotection_by_design_and_by_default_v2.0_en.pdf)

## Consequences

- This ADR introduces no code, dependency, or configuration changes today. It does not modify `3rd_party/iam`, Keycloak realm configuration, or `service/tournamentmgmt`'s resource-server setup.
- If ever implemented, email becomes part of the authentication-critical path, not just a notification channel — SPF/DKIM/DMARC, volume control, and bounce/complaint monitoring become auth reliability requirements, not "email team" concerns.
- Any implementation must preserve the existing Keycloak `rallyon_user_id` claim contract and issuer/JWKS validation rules (`3rd_party/iam/AGENTS.md`); this mechanism should complement Keycloak (e.g., as a custom authenticator/flow, or a thin service that still ends in Keycloak-issued tokens) rather than bypass it.
- Adopting email-only login for any RallyOn role should be a conscious, risk-tiered choice, not a default — it is not phishing-resistant and should not be the only method available to admin or otherwise sensitive accounts long-term.

## Open Follow-Up Work

- Confirm which RallyOn user roles (organizer/admin vs. participant/audience) this mechanism would ever apply to, and whether any of them are too high-risk for email-only login.
- Decide whether such a mechanism would live inside Keycloak as a custom authenticator/flow, or as a separate service that delegates to Keycloak for final token issuance.
- Revisit once the organizer frontend stack (see `docs/adr/draft-organizer-platform-target.md`) and the "Authentifizierung & Autorisierung" module are actually scheduled — this ADR is not actionable until then.
- If pursued, evaluate a concrete roadmap step toward WebAuthn/passkeys and/or OIDC-SSO for higher-risk roles, rather than treating email-only as a permanent end state.

## References

- `docs/adr/draft-organizer-platform-target.md`
- `wiki/Architecture/Modules.md`
- `3rd_party/iam/AGENTS.md`
- [NIST SP 800-63B, §5.1.3.1 Out-of-Band Verifiers](https://pages.nist.gov/800-63-3/sp800-63b.html)
- [NIST SP 800-63-4 Authenticators](https://pages.nist.gov/800-63-4/sp800-63b/authenticators/)
- [RFC 6238: TOTP Algorithm](https://www.rfc-editor.org/rfc/rfc6238.html)
- [RFC 7636: Proof Key for Code Exchange (PKCE)](https://www.rfc-editor.org/rfc/rfc7636.html)
- [RFC 8725: JSON Web Token Best Current Practices](https://www.rfc-editor.org/rfc/rfc8725.html)
- [FIDO2/WebAuthn phishing resistance explained](https://askmeidentity.com/resources/standards/fido2-and-passkeys-explained/)
- [OWASP Session Management Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Session_Management_Cheat_Sheet.html)
- [OWASP Authentication Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html)
- [Okta email (OTP/magic link) integration guide](https://developer.okta.com/docs/guides/authenticators-okta-email/main/)
- [Auth0: magic link same-browser/device requirement](https://support.auth0.com/center/s/article/passwordless-magic-link-error-The-link-must-be-opened-on-the-same-device-and-browser-from-which-you-submitted-your-email-address)
- [Supabase: passwordless email logins (token_hash / PKCE)](https://supabase.com/docs/guides/auth/auth-email-passwordless)
- [Gmail email sender guidelines](https://support.google.com/a/answer/81126?hl=en)
- [EDPB Guidelines 4/2019 on Article 25 (Data Protection by Design and by Default)](https://www.edpb.europa.eu/sites/default/files/files/file1/edpb_guidelines_201904_dataprotection_by_design_and_by_default_v2.0_en.pdf)
