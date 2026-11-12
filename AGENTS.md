# PROJECT KNOWLEDGE BASE

**Generated:** 2026-09-11
**Commit:** f39c9fe
**Branch:** master

## OVERVIEW

Micronaut 5.2.5 (Java 25, Maven) microservice storing streamer configs (payment page, widgets) in PostgreSQL. Exposes a REST API, consumes RabbitMQ config commands/events, and publishes an OpenAPI→TypeScript client. Part of the OpenDonationAssistant platform.

## STRUCTURE

```
oda-config-service/
├── src/main/java/io/github/opendonationassistant/
│   ├── Application.java          # main(), @Factory, RabbitMQ wiring, forces `standalone` env
│   └── config/
│       ├── ConfigController.java # REST /config
│       ├── ConfigRepository.java # Micronaut Data JDBC repo
│       ├── ConfigValueAbstractFactory.java
│       ├── commands/             # REST command endpoints (create-config, change-agreement, change-socials)
│       ├── listeners/            # RabbitMQ command + event listeners
│       └── values/               # ConfigValue entity + typed subclasses
├── src/main/resources/
│   ├── application*.yml          # base + standalone + allinone profiles
│   ├── logback.xml               # JSON console logging
│   └── db/migration/             # Flyway V1..V5 (schema `config`)
├── src/test/java/                # JUnit5 + Mockito + Instancio (no @MicronautTest)
├── pom.xml                       # Micronaut Platform parent, ErrorProne/NullAway gates
├── aot-jar.properties / aot-native-image.properties  # AOT config per packaging
├── openapi-config.json           # TS/axios client generator options
├── Dockerfile                    # fedora:41, copies pre-built native binary
├── local.sh                      # native build → podman → k3s deploy
└── .github/workflows/maven.yml   # delegates to oda-libraries release workflow
```

## WHERE TO LOOK

| Task | Location | Notes |
|------|----------|-------|
| Add a config type | `config/values/` | Extend `SaveableConfigValue`; register in `ConfigValueAbstractFactory` switch |
| Add a REST endpoint | `config/ConfigController.java` or `config/commands/` | Commands send `PutKeyValue` via `ConfigCommandSender` |
| Handle a RabbitMQ command | `config/listeners/ConfigCommandListener.java` | Dispatch by `type` message header |
| Change DB schema | `src/main/resources/db/migration/` | New `V{n}__desc.sql`, schema `config` |
| Change runtime config | `src/main/resources/application*.yml` | Env vars with backtick defaults |
| Add a test | `src/test/java/...` | Mirror package, `*Test.java`, Mockito + Instancio |
| OpenAPI spec | `target/classes/META-INF/swagger/` | Generated at build; client published to GitHub npm |

## CODE MAP

| Symbol | Type | Location | Role |
|--------|------|----------|------|
| `Application` | class | `Application.java` | Entry point; `@Factory`; declares RabbitMQ `commands` exchange → `commands.config` queue |
| `DefaultEnvironmentConfigurer` | class | `Application.java` | `@ContextConfigurer` forcing `standalone` default env |
| `ConfigController` | controller | `config/ConfigController.java` | GET/PUT/POST `/config`; ownerId from auth or query; widgets/paymentpage special-cased |
| `ConfigRepository` | interface | `config/ConfigRepository.java` | `@JdbcRepository(POSTGRES)`; `find(ownerId,name)`, `findByNameAndUrl` |
| `ConfigValueAbstractFactory` | class | `config/ConfigValueAbstractFactory.java` | Creates/constructs typed values by name; `Objects.requireNonNull` guard |
| `ConfigValue` | entity | `config/values/ConfigValue.java` | `@MappedEntity("config")`; JSONB `value`/`actions`; nested `Action` record |
| `SaveableConfigValue` | class | `config/values/SaveableConfigValue.java` | `save()` = find→update else insert |
| `WidgetsConfigValue` | class | `config/values/WidgetsConfigValue.java` | Fills default `/topic/{ownerId}...` topics + `loglevel` |
| `PaymentPageConfigValue` | class | `config/values/PaymentPageConfigValue.java` | Default payment-page values; `upsertAction`/`removeAction` |
| `CreateConfig` | controller | `config/commands/CreateConfig.java` | POST `/config/commands/create-config` (anonymous) |
| `ChangeAgreement` | controller | `config/commands/ChangeAgreement.java` | POST `/config/commands/change-agreement` → `PutKeyValue` |
| `ChangeSocials` | controller | `config/commands/ChangeSocials.java` | POST `/config/commands/change-socials` → `PutKeyValue` |
| `ConfigCommandListener` | listener | `config/listeners/ConfigCommandListener.java` | `commands.config`; dispatch by `type` header (UpsertAction/DeleteAction/PutKeyValue) |
| `ConfigEventsListener` | listener | `config/listeners/ConfigEventsListener.java` | Events queue; `VotingState` branch is an empty stub |

## CONVENTIONS

- **google-java-format**: 2-space indent, alphabetical imports (static first), ≤100 cols.
- **NullAway in ERROR mode** (JSpecify) on `io.github.opendonationassistant` — passing null to `@Nonnull` params is a compile error. Generated sources excluded.
- **Error Prone** is a hard build gate (`--should-stop=ifError=FLOW`).
- Java 25: records, switch expressions, text blocks, `var`.
- Env-based config with backtick defaults: `${RABBITMQ_HOST:`localhost`}`.
- Flyway migrations `V{n}__desc.sql` into schema `config`; JSONB columns for `value`/`actions`.
- Tests: JUnit5, Mockito mocks (no runner), Instancio `@Given` data, text-block JSON fixtures, verbose `testXxx` names. No `@MicronautTest` anywhere.
- Commits: lowercase terse messages (e.g. "up micronaut version").

## ANTI-PATTERNS (THIS PROJECT)

- **Never pass null to `@Nonnull` parameters** — NullAway fails the build.
- **Don't touch `target/generated-sources/`** — excluded from NullAway; regenerated at build.
- **Don't commit Eclipse metadata** (`.classpath`, `.project`, `.factorypath`, `.settings/`) — gitignored local artifacts.
- **Don't break the Maven wrapper** — `.mvn/wrapper/` is gitignored; use system `mvn` on fresh clones.
- **Don't add hardcoded test data to Flyway migrations** — V2 inserts a real-looking testuser into every migrate (known smell).
- **Don't leave empty stubs** — `ConfigEventsListener.handleVotingState` and `WidgetsConfigValueTest` are empty placeholders.

## UNIQUE STYLES

- **Structured logging** via `ODALogger`: `log.info/debug/error(msg, Map.of(...))`.
- **Self-saving entities**: config values carry their `ConfigRepository` and call `save()`.
- **Event-driven writes**: command endpoints send `PutKeyValue` through RabbitMQ (`ConfigCommandSender`) rather than writing the DB directly.
- **Programmatic RabbitMQ wiring** in `Application.rabbitConfiguration()` via `AMQPConfiguration` (from `oda-rabbit-conf`).
- **`BaseController.getOwnerId(auth)`** from `oda-commons` for auth→owner resolution.
- **Dual packaging**: `-Dpackaging=native-image` switches Maven packaging AND selects the AOT config file.

## COMMANDS

```bash
mvn clean package                          # runnable fat jar
mvn clean package -Dpackaging=native-image # GraalVM native binary → target/oda-config-service
mvn test                                   # unit tests (surefire)
mvn -B verify                              # tests + jacoco coverage
mvn -B -q verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar  # CI: tests + SonarCloud
./local.sh                                 # native build → podman → k3s deploy
```

## NOTES

- **Default env is `standalone`**, forced in code (`DefaultEnvironmentConfigurer`), not by config.
- **Name mismatch**: Micronaut app name `oda-config-server` vs Maven artifact `oda-config-service`.
- **Stale config**: `application.yml` sets `jpa.default.entity-scan.packages: io.github.stcarolas.oda.config` — that package doesn't exist; real code is `io.github.opendonationassistant.config`.
- **Dockerfile has no build stage** — expects `target/oda-config-service` to exist before `docker build`.
- **`.mvn/jvm.config`** carries `--add-exports/--add-opens` for `jdk.compiler` internals (required for ErrorProne/NullAway on JDK 25).
- **`local.sh` uses GraalVM JDK 21** while the project targets JDK 25.
- **CI is externalized**: this repo's workflow only re-invokes `oda-libraries` reusable workflows (build/test/sonar/package/publish); deploy steps are commented out.
- **OpenAPI→TypeScript client** (`@opendonationassistant/oda-config-service-client`) is generated at `package` phase into `target/` and published to GitHub npm.
- **Image/git tags = GitHub run number**, not semver.