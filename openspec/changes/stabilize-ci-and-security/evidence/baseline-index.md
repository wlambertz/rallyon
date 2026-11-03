# Baseline finding index for G2

Pre-implementation index only. Every original finding, score field, detected CPE/package and source reference is preserved in [the baseline JSON](baseline-findings.json). These are not accepted suppression rules or post-update results. Exact CVE applicability and final action must be independently checked during implementation.

| Report entry | Finding count | G2 disposition / validation focus |
| --- | --- | --- |
| `jackson-databind-2.21.4.jar` | 1 | Verify approved BOM-resolved update and manufacturer affected/fixed version ranges; retain unresolved genuine findings. |
| `keycloak-core-0.1.0-SNAPSHOT.jar` | 3 | Candidate wrong-product match: first-party IAM helper versus Keycloak server; prove identity before any narrow rule. |
| `kotlin-stdlib-2.3.21.jar` | 1 | Upgrade compiler/runtime/plugins together; distinguish build-cache and stdlib applicability rather than blanket-suppress. |
| `log4j-api-2.25.4.jar` | 1 | Verify approved BOM-resolved update and manufacturer affected/fixed version ranges; retain unresolved genuine findings. |
| `micrometer-registry-prometheus-1.17.0.jar` | 2 | Candidate wrong-product match: Java registry versus Prometheus server; verify actual package and vendor conditions. |
| `postgresql-42.7.11.jar` | 1 | Vendor-confirmed affected baseline JDBC version; verify patched resolved driver and all remaining identity matches. |
| `spring-core-7.0.8.jar` | 17 | Verify approved BOM-resolved update and manufacturer affected/fixed version ranges; retain unresolved genuine findings. |
| `spring-data-jpa-4.1.0.jar` | 1 | Verify approved BOM-resolved update and manufacturer affected/fixed version ranges; retain unresolved genuine findings. |
| `spring-security-core-7.1.0.jar` | 1 | Verify historical Framework/Security version conditions against the aligned updated platform; no broad Spring waiver. |
| `spring-security-oauth2-resource-server-7.1.0.jar` | 1 | Verify historical Framework/Security version conditions against the aligned updated platform; no broad Spring waiver. |
| `swagger-ui-5.32.2.jar: swagger-ui-bundle.js` | 17 | Inspect actual embedded JavaScript versions after the targeted WebJar update; retain RetireJS analysis. |
| `swagger-ui-5.32.2.jar: swagger-ui-es-bundle.js` | 17 | Inspect actual embedded JavaScript versions after the targeted WebJar update; retain RetireJS analysis. |
| `tomcat-embed-core-11.0.22.jar` | 19 | Verify approved BOM-resolved update and manufacturer affected/fixed version ranges; retain unresolved genuine findings. |

The 13 entries include separately analyzed embedded Swagger JavaScript resources; counts are report instances, not deduplicated CVE totals. No finding is reclassified merely because the application might not use its affected feature.
