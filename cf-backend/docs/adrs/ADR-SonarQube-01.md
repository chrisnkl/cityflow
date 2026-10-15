# Architectural Decision Record: SonarQube Adoption & Quality Gates

* **Status:** Adopted
* **Date:** 2026-10-11
* **Authors:** chrisnkl

## Context

Project requires an automated way to measure and enforce code quality, test coverage, and security compliance across the backend application. Currently, monitoring technical debt and missing test coverage is manual and inconsistent, leading to possible regressions in the codebase.

## Decision

Adopted **SonarQube** to perform static application security testing and code quality analysis.

#### sonar-project.properties:
```properties
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
Make sure you are on the cf-backend folder (on the spring-boot project).
````bash
 ./mvnw clean verify sonar:sonar
`````