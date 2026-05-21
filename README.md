# Flamingo QA — Automation Test Suite

End-to-end QA framework covering:
- **API tests** against [Restful Booker](https://restful-booker.herokuapp.com) (REST CRUD)
  and a public **Movie GraphQL** mirror (positive + negative).
- **UI tests** against [DemoQA](https://demoqa.com) — Practice Form (Student Form) + Web Tables(Employee) —
  built on the **Page Object Model** with **Playwright for Java**.

## Stack

| Concern        | Library / version |
| -------------- | ----------------- |
| Language       | Java 21           |
| Build          | Maven (no wrapper — install Maven 3.9+) |
| Test runner    | JUnit 5 (`junit-jupiter` 5.10) |
| API client     | REST Assured 5.4  |
| UI driver      | Playwright for Java 1.45 |
| Assertions     | AssertJ 3.25      |
| JSON           | Jackson 2.17      |
| Reporting      | Allure 2.27 (`allure-junit5`, `allure-rest-assured`) |
| Boilerplate    | Lombok 1.18       |

## Prerequisites

- JDK **21+**
- Maven **3.9+**
- For UI tests: Playwright will auto-download Chromium on first run. On CI/Linux you may
  need OS dependencies — see *Run UI tests* below.

## How to run

```powershell
# Run all tests
mvn clean test

# Run only API tests
mvn test -Dgroups=api

# Run only UI tests
mvn test -Dgroups=ui

# Run UI tests with visible browser
mvn test -Dgroups=ui -Dui.headless=false

# Run a single test class
mvn test -Dtest=BookingTests

# Configure parallel thread count (default: 2)
mvn test -Dgroups=api -Dthreads=4
```

### Install Playwright browsers manually (one-off)

```powershell
mvn compile exec:java "-Dexec.mainClass=com.microsoft.playwright.CLI" "-Dexec.args=install --with-deps chromium"
```

### Reports

- **Surefire HTML** — open `target/surefire-reports/` after `mvn test`.
- **Allure** — `mvn allure:serve` (downloads CLI, opens a live report).
- **Screenshots** — every UI test captures a full-page PNG into `target/screenshots/`
  and attaches it to Allure (named after the test).


## Test strategy

**What we prioritised, and why.**

1. **Cover the contract, not the UI of the API.** REST Assured tests assert on
   status codes, response schemas (POJO deserialization with Jackson), and meaningful
   field values — not the wire format. CRUD is exercised end-to-end so a single test
   class proves the full lifecycle of a booking.
2. **GraphQL: positive AND negative shape checks.** Negative tests explicitly assert
   *where* GraphQL surfaces problems (HTTP 200 + `data: null` vs `errors[]` vs HTTP 400),
   which is the most common source of false positives in GraphQL test suites.
3. **POM keeps tests readable.** Page objects expose intent-revealing methods
   (`fillName`, `setDateOfBirth`, `addRecord`) and return `this` for fluent chaining.
   Tests only contain *what* is verified; *how* lives in the page object.
4. **Dynamic waits, never `sleep`.** All waits are Playwright's auto-waiting plus
   explicit `waitForSelector`/`waitFor` with a configurable timeout. No `Thread.sleep`
   anywhere.
5. **DRY via base classes.** `UiTestBase` owns browser lifecycle + screenshot capture;
   `RestAssuredSpecs` owns base URI, content type and Allure attachment.
6. **Unique test data.** `WebTablesTest` generates a UUID-based email so the suite can
   run repeatedly on a shared demo site without flakiness.

## Challenges & solutions

| Problem | Solution |
| ------- | -------- |
| DemoQA shows a sticky ad/footer that intercepts clicks on the submit button | Each page object removes `iframe`, `#fixedban`, and `footer` via `page.evaluate` on open, then scrolls the submit button into view before clicking. |
| Restful Booker's `DELETE` returns **201** (not 204) and `PUT` requires the token as a `Cookie` header (not Bearer) | Encoded as a documented assertion (`isEqualTo(201)`) and a header on the `BookingClient.delete`/`update` calls. |
| Public SpaceX GraphQL mirrors drift; hard-coded ids break | `singleByIdWithVariables` paginates to fetch a real id first, then queries by it. |
| Negative GraphQL behaviours differ by server (200 vs 400 for syntax errors) | Assertions accept *either* status (`isIn(200, 400)`) but strictly assert the body shape (`errors[].message`, `data == null`). |
| Playwright screenshots in `@AfterEach` lose JUnit's failure signal | We capture unconditionally — cheap on a demo site, and they make Allure reports useful regardless of outcome. |

## What I would add with more time

- Replace ordered CRUD with independent tests + a JUnit 5 extension that creates/
  cleans up a booking per test.
- Schema validation (`io.rest-assured:json-schema-validator`) for `/booking` responses.
- A JUnit 5 `TestWatcher` extension so screenshots only fire on failure, with a
  separate `success/` and `failure/` folder.
- Contract tests (Pact / WireMock) so the suite is runnable offline when the public
  services are down.
- Trace-on-failure (`context.tracing().start(...)`) to ship `.zip` traces to Allure.

## CI

A GitHub Actions workflow lives at [`.github/workflows/ci.yml`](.github/workflows/ci.yml).
It runs API and UI tests in parallel jobs on every push / PR, uploads Surefire reports,
Allure results, and UI screenshots as artifacts.

Latest run can be found here: https://github.com/Alexandr220v/flamingo-test/actions/workflows/ci.yml 
Allure report: https://alexandr220v.github.io/flamingo-test/ 
