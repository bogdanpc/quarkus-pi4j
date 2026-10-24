# Quarkus Pi4J Extension - Improvement Plan

## Project Review and Improvement Suggestions

### 2. Configuration Improvements
- [ ] Add platform selection (Mock, RaspberryPi, custom)
- [ ] GPIO pin mappings configuration
- [ ] Default timeouts and retry policies
- [ ] Custom Pi4J context properties

### 3. Error Handling & Resilience
- [ ] Replace generic Exception catches with specific Pi4J exceptions
- [ ] Add proper error recovery mechanisms
- [ ] Implement circuit breaker pattern for GPIO operations
- [ ] Add retry logic for transient failures

### 4. Testing Enhancements
- [ ] Replace placeholder test in Pi4jTest.java with real tests
- [ ] Add mock Pi4J context for testing without hardware
- [ ] Create integration tests using Pi4J mock provider
- [ ] Add tests for health check functionality
- [ ] Test configuration validation

### 5. Developer Experience

#### Dev Services Integration
- [ ] Add Dev Services support for Pi4J mock mode during development
- [ ] Create `Pi4jDevServicesProcessor` for automatic mock platform setup
- [ ] Support hybrid development (mock dangerous components, real sensors)
- [ ] Enable zero-configuration development without Pi hardware
- [ ] Add configuration options:
  ```properties
  quarkus.pi4j.dev-services.mode=mock|real|hybrid
  quarkus.pi4j.dev-services.mock.pins=22,23,24  # Mock specific pins
  quarkus.pi4j.dev-services.auto-start=true
  ```

#### DevUI Integration
- [ ] Implement comprehensive DevUI extension with multiple cards:
  - **GPIO Pin Visualization**: Real-time pin state display (HIGH/LOW, INPUT/OUTPUT)
  - **Interactive Pin Control**: Click-to-toggle digital outputs, test inputs
  - **Pi4J System Dashboard**: Platform info, providers, board details, temperature
  - **Hardware Safety Controls**: Emergency stops, voltage monitoring, current limits
  - **Configuration Management**: Live pin configuration editing, platform switching
- [ ] Add virtual hardware simulator for testing without physical components
- [ ] Create pin mapping visualization for different Raspberry Pi models
- [ ] Include GPIO operation history and timing analysis
- [ ] Add I2C bus scanning and SPI transaction monitoring
- [ ] Support remote development (DevUI accessible from development machine)

#### Benefits on Real Pi Hardware
- **Safe Development**: Mock expensive/dangerous components while using real sensors
- **Remote Development**: Access DevUI from laptop while Pi runs the application
- **Hardware Debugging**: Pin conflict detection, timing analysis, voltage monitoring  
- **Interactive Testing**: Test GPIO changes without rebuild/redeploy cycles
- **Emergency Safety**: Web-accessible stop buttons and safety controls

#### Additional Developer Experience Improvements
- [ ] Create more comprehensive examples (I2C, SPI, PWM)
- [ ] Add configuration profiles for different Raspberry Pi models
- [ ] Enable hot reload support for GPIO configuration changes

### 6. API Improvements
- [ ] Create higher-level abstractions for common patterns
- [ ] Add reactive/async support using Mutiny
- [ ] Support for batch GPIO operations
- [ ] Event-driven programming model with CDI events

### 7. Documentation
- [ ] Add Javadoc to public APIs
- [ ] Create configuration reference documentation
- [ ] Add troubleshooting guide
- [ ] Include wiring diagrams for examples

### 8. Build & Release
- [ ] Add GitHub Actions for CI/CD
- [ ] Configure automatic dependency updates (Dependabot)
- [ ] Add release automation
- [ ] Include native image configuration

### 9. Specific Code Improvements

#### ContextConfiguration.java
- [ ] Add configuration for custom Pi4J platforms
- [ ] Support lazy initialization
- [ ] Add metrics/monitoring support

#### Pi4jHealthCheck.java
- [ ] Cache board info to avoid repeated reads
- [ ] Make health check data configurable
- [ ] Add custom health indicators

#### Example Application
- [ ] Add OpenAPI annotations
- [ ] Implement proper REST response codes
- [ ] Add input validation
- [ ] Include more GPIO examples (button input, PWM, etc.)

### 10. Security
- [ ] Add configuration validation to prevent unsafe pin access
- [ ] Implement permission checks for GPIO operations
- [ ] Add rate limiting for GPIO operations
- [ ] Document security best practices

## Priority Order

### High Priority (Core Functionality)
1. Testing Enhancements - Essential for reliability
2. Error Handling & Resilience - Critical for production use
3. Code Modernization - Improves maintainability

### Medium Priority (Developer Experience)
4. Configuration Improvements - Better flexibility
5. Developer Experience - Easier adoption
6. Documentation - Better onboarding

### Low Priority (Nice to Have)
7. API Improvements - Advanced features
8. Build & Release - Automation
9. Security - Advanced security features

## Learnings from quarkiverse/quarkus-pi4j (reviewed 2026-06-25)

Concrete, actionable gaps found by comparing against `quarkiverse/quarkus-pi4j`
(both on Pi4J 4.0.2, Quarkus ~3.36). Ordered by impact. Each maintains backward
compatibility unless noted.

### L1. Mock provider support (highest impact — unlocks hardware-free testing)
- [ ] Add `quarkus.pi4j.mock` (default `false`) config property.
- [ ] When `mock=true`, build `Context` from `MockPlatform` +
      `MockDigitalInputProvider`/`MockDigitalOutputProvider` via
      `Pi4J.newContextBuilder()`, instead of `Pi4J.newAutoContext()`.
- [ ] Register the three mock classes with `ReflectiveClassBuildItem`
      (`.methods().constructors()`) so mock mode works in native image.
- [ ] Switch integration tests to `quarkus.pi4j.mock=true` so they run in CI
      without a Raspberry Pi (replaces "needs hardware" placeholder tests).

### L2. Broaden runtime configuration (move off BUILD_AND_RUN_TIME_FIXED)
- [ ] Introduce a `@ConfigRoot(phase = RUN_TIME)` `Pi4jConfig` (`quarkus.pi4j`):
  - [ ] `enabled` (default `true`) — disable the whole extension.
  - [ ] `mock` (default `false`) — see L1.
  - [ ] `shutdown` (default `true`) — gate context shutdown on app stop.
  - [ ] `failOnStartupError` (default `true`) — boot even without Pi hardware;
        only fail when the `Context` is actually injected.
  - [ ] `pins` — `Map<String,Integer>` named BCM pin mappings (e.g.
        `quarkus.pi4j.pins.led=13`).
  - [ ] `digitalInputProvider` / `digitalOutputProvider` — `Optional<String>`
        provider-ID overrides.
- [ ] Keep existing `quarkus.pi4j.health.enabled` working (migrate or retain).
- [ ] Register via `ConfigMappingBuildItem` in the processor.

### L3. Lifecycle via Startup/Shutdown events
- [ ] Refactor `ContextConfiguration` to build the `Context` on
      `@Observes StartupEvent`, capturing any failure instead of throwing when
      `failOnStartupError=false`.
- [ ] Gate disposal on `@Observes ShutdownEvent` + `shutdown` config flag.
- [ ] Expose `ready()` / `startupFailure()` for health check and Dev UI.
- [ ] Preserve `@DefaultBean` so users can still override the `Context` (an
      advantage over quarkiverse — do not lose this).

### L4. GPIO injection ergonomics (headline developer feature)
- [ ] Promote the example app's `Pi4JService` logic into a real `GpioService`
      bean in the runtime module: `output(address)`, `input(address)`,
      `namedOutput(name)`, `namedInput(name)`, with caching via `pi4j.hasIO(id)`
      to return the same IO instance on repeated access.
- [ ] Add `@GpioPin(int value, String id, String name)` qualifier + producer for
      `@Inject @GpioPin(13) DigitalOutput led;`.
- [ ] Add `@NamedGpio(String value)` qualifier resolving against `pins` config
      for `@Inject @NamedGpio("led") DigitalOutput led;`.
- [ ] Register `GpioService` + producer as unremovable `AdditionalBeanBuildItem`s.

### L5. Dev UI card (cheap polish)
- [X] Add `Pi4jDevUiProcessor` with `@BuildStep(onlyIf = IsDevelopment.class)`
      registering a `CardPageBuildItem` (web component + external Pi4J docs link).
- [X] Add a JSON-RPC service exposing context/platforms/providers/registry in dev.

### L6. Native image support
- [ ] Add reflection registration for reflectively-loaded Pi4J providers/mocks
      (see L1); validate a native build of the integration tests.

### L7. Docs / housekeeping
- [ ] Document the new config properties and injection annotations.

### Kept as-is (we are ahead of quarkiverse here)
- Richer health check (board model, SoC, CPU, temp, voltage, uptime, platforms,
  providers, registry) — do not regress to a leaner probe.
- `@DefaultBean` overridability of the `Context`.

## Implementation Notes


- Start with high-priority items that have the most impact on reliability and maintainability
- Each improvement should maintain backward compatibility
- Consider creating feature branches for major changes
- Add tests for each new feature or improvement
- Update documentation as features are implemented