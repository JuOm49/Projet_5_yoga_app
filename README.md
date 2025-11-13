# README: YOGA APP

## Front (Angular) — How to run and test

This document explains how to run the Angular frontend, run unit tests and run integration (Cypress) tests with coverage on Windows.

Prerequisites
- Node.js (>=14 recommended) and npm
- Angular CLI (optional for local serve): `npm i -g @angular/cli`
- A free port (default app uses 4200)
- (For CI e2e script) a build toolchain present (the project scripts manage build)

Install dependencies:
> npm install

Launch Front-end:
> npm run start;


## Ressources

### Mockoon env 

### Postman collection

For Postman import the collection
> ressources/postman/yoga.postman_collection.json


### MySQL
> SQL script for creating the schema is available `ressources/sql/script.sql`

> By default the admin account is:
- login: yoga@studio.com
- password: test!1234

### Run unit tests (Jest)
```powershell
# run unit tests
npm run test
# produce coverage report
npm run test:coverage
```

### Run Cypress integration (interactive)
> ensure the dev server is running (npm start or ng serve)


### Run full E2E (CI)
```powershell
# CI flow that build app, runs Cypress and generates coverage
npm run e2e:ci
# after tests complete, generate nyc report (if not done by script)
npm run e2e:coverage
```

Regenerate / view coverage report
```powershell
# After running unit, e2e tests that collect coverage:
npm run e2e:coverage
# Open HTML report (Windows)
start .\coverage\lcov-report\index.html
```
## backend

## Prerequisites
- Java 11
- IntelliJ IDEA on Windows
- Maven (used via IntelliJ Maven tool window or command line)

## Run the application
- Open the project and open the Spring Boot main class annotated with `@SpringBootApplication` under `src/main/java`.
- Right-click → Run the main class (or create a Spring Boot run configuration).

Alternative (command line):
- From project root: `mvn spring-boot:run`

## Unit tests
- In IntelliJ:
  - Maven tool window → Lifecycle → double‑click `test`
- CLI:
  - `mvn test`
- JaCoCo (unit) output:
  - execution file: `target/jacoco.exec`
  - report HTML: `target/site/jacoco-unit/index.html`
- Ensure that  `target/jacoco.exec` has been generated, then open `target/site/jacoco-unit/index.html` in a web browser to view the coverage report.

## Integration tests (IT)
- Name integration tests with the `*IT.java` suffix (configured for Failsafe).
- In IntelliJ:
  - Maven tool window → Lifecycle → double‑click `verify` (runs pre-integration-test → integration-test → post-integration-test)
- CLI:
  - `mvn verify`
- JaCoCo (IT) output:
  - execution file: `target/jacoco-it.exec`
  - report HTML: `target/site/jacoco-it/index.html`
- Ensure that `target/jacoco-it.exec` has been generated, then open `target/site/jacoco-it/index.html` in a web browser to view the coverage report.

## Coverage
- `pom.xml` generates two separate reports (`jacoco-unit` and `jacoco-it`).

## Files to inspect after runs
- `target/jacoco.exec` → unit test exec data
- `target/site/jacoco-unit/index.html` → unit report
- `target/jacoco-it.exec` → IT exec data
- `target/site/jacoco-it/index.html` → IT report
