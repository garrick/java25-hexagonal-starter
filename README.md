# java21-hexagonal-starter

A Java 21 project template for hexagonal (ports & adapters) architecture with a batteries-included quality gate wired into every build.

## Stack

| Concern | Tool |
|---|---|
| Language | Java 21 (Eclipse Temurin toolchain) |
| Build | Gradle 9.5.1 with version catalog (`gradle/libs.versions.toml`) |
| CLI I/O | JLine 3 |
| Unit tests | JUnit Jupiter 5 |
| Property-based tests | jqwik |
| Coverage | JaCoCo (HTML/XML/CSV) |
| Mutation testing | PIT (pitest) |
| Static analysis | Checkstyle 10, PMD 7, CPD |
| CI | GitHub Actions |

## Getting started

```bash
# Rename the project
# Edit settings.gradle: rootProject.name = 'your-project-name'
# Edit app/build.gradle: mainClass = 'your.package.Main'

./gradlew run        # run the application
./gradlew check      # full quality gate: tests + coverage + mutation + static analysis
./gradlew test       # tests + JaCoCo report
./gradlew pitest     # mutation testing only
```

## Quality gate

`./gradlew check` runs everything in one shot:

- **JUnit 5** — unit and integration tests
- **jqwik** — property-based tests (registered automatically via JUnit Platform)
- **JaCoCo** — line/branch coverage report at `app/build/reports/jacoco/`
- **PIT** — mutation testing report at `app/build/reports/pitest/`
- **Checkstyle** — style rules in `app/code_quality_tools/checkstyle.xml`
- **PMD** — bug pattern rules in `app/code_quality_tools/pmd.xml`
- **CPD** — copy-paste detection (threshold: 100 tokens)

## GitHub Actions CI

Workflow: `.github/workflows/gradle.yml`

### Triggers

| Event | Branch filter |
|---|---|
| `push` | `main` |
| `pull_request` | targeting `main` |

### Job: `build`

Runs on `ubuntu-latest` with `contents: read` permissions (no write access to the repo).

| Step | Action / Command | Purpose |
|---|---|---|
| Checkout | `actions/checkout@v4` | Full source checkout |
| JDK setup | `actions/setup-java@v4` — Temurin 21 | Matches the local toolchain declaration |
| Gradle setup | `gradle/actions/setup-gradle@v4.0.0` | Enables Gradle build caching and wrapper validation |
| Quality gate | `./gradlew check` | Runs the full gate: tests, JaCoCo, PIT, Checkstyle, PMD, CPD |

### What `./gradlew check` gates in CI

The `check` task is the single CI entrypoint — the same command you run locally. A red build means at least one of:

- JUnit 5 / jqwik test failure
- JaCoCo coverage threshold missed
- PIT mutation score threshold missed
- Checkstyle rule violation
- PMD rule violation
- CPD copy-paste threshold (100 tokens) exceeded

### Caching

`gradle/actions/setup-gradle` automatically caches the Gradle wrapper, dependency jars, and build-cache entries between runs, keeping incremental builds fast on repeated pushes.

### Pinned action SHA

`setup-gradle` is pinned to a full commit SHA (`af1da67...`) rather than a mutable tag — standard supply-chain hygiene for public repos. Update it by bumping the SHA in the workflow file when a new release ships.

## Project layout

```
app/
  src/main/java/org/commandline/   # production code
  src/test/java/org/commandline/   # tests
  code_quality_tools/              # checkstyle.xml, pmd.xml
gradle/
  libs.versions.toml               # dependency version catalog
.github/workflows/gradle.yml       # CI: runs ./gradlew check on push/PR
```

## Hexagonal architecture conventions

Organize production code under `org.commandline` using this layout:

```
domain/          # pure business logic, no framework dependencies
  model/         # value objects and entities
  ports/
    in/          # use-case interfaces (driving side)
    out/         # repository/gateway interfaces (driven side)
application/     # use-case implementations
adapter/
  in/            # console, HTTP, etc. (driving adapters)
  out/           # persistence, external APIs (driven adapters)
Main.java        # wiring root
```

Mutation testing excludes `adapter.in.console` and `Main` by default (acceptance-test boundary — uncomment the `excludedClasses` line in `app/build.gradle` once acceptance tests are in place).

## First steps after cloning

1. Rename `rootProject.name` in `settings.gradle`.
2. Set your base package in `app/build.gradle` (`mainClass`) and rename `org/commandline` accordingly.
3. Update `pitest.targetClasses` to match your new package.
4. Add your domain model in `domain/model/`, define ports, implement use cases, wire adapters.
