# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is the Quarkus Pi4J extension - a Quarkus extension that enables GPIO programming on Raspberry Pi devices using the Pi4J library within Java Quarkus applications.

## Build Commands

```bash
# Build the entire project
./mvnw clean package

# Build and install to local Maven repository
./mvnw clean install

# Run tests
./mvnw test

# Run integration tests
./mvnw verify -Pit

# Build documentation
./mvnw package -Pdocs

# Run Quarkus in dev mode (from examples/simple-app directory)
./mvnw quarkus:dev

# Run a specific test
./mvnw test -Dtest=ClassName#methodName
```

## Architecture

### Module Structure

The project follows a multi-module Maven structure with three main components:

1. **runtime/** - Core runtime functionality
   - `ContextConfiguration.java` - Manages Pi4J Context lifecycle as a CDI singleton bean
   - `Pi4jHealthCheck.java` - Provides health check integration with Quarkus SmallRye Health
   - `Pi4jBuildTimeConfig.java` - Build-time configuration for the extension

2. **deployment/** - Build-time augmentation
   - `Pi4jProcessor.java` - Quarkus build processor that registers the feature, beans, and health checks during build

3. **examples/simple-app/** - Example application demonstrating extension usage
   - Shows how to inject and use the Pi4J Context
   - Includes REST endpoints for LED control

### Key Design Patterns

- **CDI Integration**: The Pi4J Context is automatically configured as a CDI bean and can be injected using `@Inject`
- **Lifecycle Management**: The extension handles proper initialization and shutdown of the Pi4J system
- **Health Check**: Optional health check integration that reports board info, temperature, voltage, and Pi4J platform/provider status
- **Build-Time Processing**: Uses Quarkus build steps to register components at build time for optimal runtime performance

### Configuration

Health check can be enabled/disabled via application properties:
```properties
quarkus.pi4j.health.enabled=true  # Default is true
```

### Testing Approach

- Unit tests in `deployment/src/test/` for build-time processing
- Integration tests in `integration-tests/` for runtime behavior
- Dev mode tests for hot reload functionality

## Development Notes

- Requires JDK 25 or later
- Uses Quarkus and Pi4J
- Deployment requires a Raspberry Pi device
- The Pi4J Context is thread-safe and managed as a singleton

## Code Style Guidelines

When writing or modifying code in this repository, use modern Java features available in JDK 21:

- **Use `var` for local variables** where type inference improves readability
  ```java
  var context = Pi4J.newAutoContext();
  var led = pi4j.digitalOutput().create(PIN_LED);
  ```

- **Prefer Records** for immutable data classes instead of traditional POJOs
  ```java
  public record GpioConfig(int pin, String name, DigitalState initialState) {}
  ```

- **Use Stream API** for collection operations instead of loops where appropriate
  ```java
  providers.stream()
      .filter(Provider::isEnabled)
      .map(Provider::name)
      .collect(Collectors.toList());
  ```

- **Use modern List/Set/Map factory methods** instead of manual initialization
  ```java
  List.of("item1", "item2");
  Map.of("key1", "value1", "key2", "value2");
  ```

- **Pattern matching** and **switch expressions** where they improve clarity
- **Text blocks** for multi-line strings
- **Optional** for nullable return values instead of returning null