# G7 architecture and domain delta

Verdict: **pass** for the two findings in [the initial architecture review](g7-architecture.md). Checker: `review_architecture`, 2026-11-11. HEAD remains `0e30558b1ef86d79b89d135216a412c59f03eb93`; the current sorted production `*Announcement*.kt` SHA-256 inventory hashes to `f3d90eb4b97da566aea45fb989eaa1a91febeca1181d89d716a655b6e97d054f`. The lifecycle helper binds the full current snapshot.

| Prior finding | Delta result |
| --- | --- |
| Persistence adapter differed from approved JPA design | Resolved: `JpaAnnouncementStore` uses a transaction-bound JPA `EntityManager`; domain and use-case ports remain framework-free. Native PostgreSQL SQL provides the uniqueness-based insert and version compare-and-set, without a managed announcement entity. This is PostgreSQL-specific, consistent with the target database and approved design; the independent PostgreSQL tests cover the actual adapter and concurrent operations. |
| Accepted ADR retained draft naming | Resolved: `docs/adr/0005-tournament-announcement-publication.md` has a numbered heading and records the maintainer's acceptance. Current design/verification references were checked; prior reports retain their historical paths as evidence. |

Reviewed the replacement adapter, numbered ADR, revised ANN-005/design bounds, source references and independent [G5](g5-approved-bounds-delta.md) and [G6](g6-approved-bounds-delta.md) deltas. No Maven run was started in this review because another reviewer was executing it. The database tests do not deterministically force every possible SQL interleaving; this review does not substitute for correctness/security or test lenses, UAT or merge approval.
