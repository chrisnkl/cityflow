# Architectural Decision Record: SonarQube Adoption & Quality Gates

* **Status:** Proposed
* **Date:** 2026-10-11
* **Authors:** Engineering Team

## Context

We need an automated way to measure and enforce code quality, test coverage, and security compliance across our applications. Currently, monitoring technical debt and missing test coverage is manual and inconsistent, leading to regressions in our codebase.

## Decision

We will adopt **SonarQube** to perform static application security testing (SAST) and code quality analysis.

## Technical Appendix: Enforcing Quality Gates Through Code

SonarQube evaluates the Quality Gate metrics on the server side. However, you can manage, configure, and enforce your **Coverage > 80%** requirement purely through code via your CI/CD configuration files and the SonarQube Web API.

### 1. Enforcing Pipeline Failure via Code (`sonar-project.properties`)

To break your build or block your pull request directly inside your infrastructure code when the Quality Gate fails, use the `sonar.qualitygate.wait` property.

Add this directly to your codebase repository's `sonar-project.properties` file:

#### sonar-project.properties:
```properties
# Core project identifiers
sonar.projectkey=cf-backend
sonar.projectname=cf-backend
sonar.projectversion=1.0

sonar.host.url=http://sonarcloud.io
sonar.token=<token>

sonar.sources=src/main/java
sonar.tests=src/test/java
sonar.java.binaries=target/classes
sonar.junit.reportPaths=target/surefire-reports
sonar.java.coveragePlugin=jacoco
sonar.jacoco.reportPaths=target/jacoco.exec

sonar.sourceEncoding=UTF-8
```

### 2. How to run locally
````bash
 mvn clean verify sonar:sonar
`````