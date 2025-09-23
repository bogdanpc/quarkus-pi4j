# Quarkus Pi4J extension

Pi4J Quarkus extension, enabling GPIO programming on Raspberry Pi devices within your Java Quarkus applications.

The [Pi4J Context](https://www.pi4j.com/documentation/create-context/) automatically configured by the Quarkus
extension, so you can inject it into your components.

The extension handles the lifecycle management and ensures proper initialization and shutdown of the Pi4J system.

## Related Projects

[Pi4J](https://www.pi4j.com/) - The core Pi4J library

[Pi4J Spring Boot Starter](https://github.com/Pi4J/pi4j-springboot) - Spring Boot integration for Pi4J

## Prerequisites

- JDK 21 or later
- Apache Maven 3.8.1+
- Raspberry Pi device (for deployment)

## Features

- Integration of Pi4J with Quarkus applications

## Build from source

```shell
git clone https://github.com/bogdanpc/quarkus-pi4j
cd quarkus-pi4j
./mvnw package
```

## Usage


If Java is not installed yet, use [SDKMAN](https://sdkman.io/install/)

Add the following dependency to your `pom.xml`

```xml

<dependency>
    <groupId>io.github.bogdanpc</groupId>
    <artifactId>quarkus-pi4j</artifactId>
    <version>0.2.1</version>
</dependency>
```

The Pi4J [Context](https://www.pi4j.com/documentation/create-context/) is the central component that manages the
lifecycle of all Pi4J instances and provides access to the various I/O interfaces. Here's a detailed example:

```java

import com.pi4j.context.Context;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.gpio.digital.DigitalState;
import io.quarkus.logging.Log;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LEDResource {
    @Inject
    Context pi4j; // Automatically configured and injected by the Quarkus extension

    private static final int PIN_LED = 22;

    public void turnOnLED() {
        try {
            var led = pi4j.digitalOutput().create(PIN_LED);

            led.state(DigitalState.HIGH);
        } catch (IOException e) {
            Log.errorf("Error: %s", e.getMessage());
        }
    }
}
```

## Monitoring Health Status

Pi4J Quarkus extension uses Quarkus [SmallRye Health check](https://quarkus.io/guides/smallrye-health) extension.

If the the Quarkus is imported, you can turn on or off by setting  `quarkus.pi4j.health.enabled` application property to
`application.properties` file.

By default is turned on.

```
quarkus.pi4j.health.enabled=true
```

## Running the sample application on a Raspberry Pi

If Java is not installed yet, use [SDKMAN](https://sdkman.io/install/)

First you need to clone and install to local Maven repository
```shell
git clone https://github.com/bogdanpc/quarkus-pi4j
cd quarkus-pi4j
./mvnw package install
```

Then go to `examples/simpe-app` directory

Start application in Quarkus dev mode using
```shell
./mvnw quarkus:dev
```

In the output of the application you should see the `pi4j` extension in the Installed features list

```shell
__  ____  __  _____   ___  __ ____  ______
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/
2025-02-03 21:42:58,376 INFO  [io.quarkus] (Quarkus Main Thread) quarkus-pi4j-examples-simple-app 1.0.0 on JVM (powered by Quarkus 3.18.1) started in 2.975s. Listening on: http://localhost:8080

2025-02-03 21:42:58,380 INFO  [io.quarkus] (Quarkus Main Thread) Profile dev activated. Live Coding activated.
2025-02-03 21:42:58,382 INFO  [io.quarkus] (Quarkus Main Thread) Installed features: [cdi, pi4j, rest, smallrye-context-propagation, smallrye-health, vertx]
```

In the terminal, run `curl http://0.0.0.0:8080/q/health`.

Output on an Raspberry Pi 5:

```json
{
  "name": "Pi4J health check",
  "status": "UP",
  "data": {
    "os.name": "Linux",
    "os.architecture": "aarch64",
    "os.version": "6.6.69-v8-16k+",
    "board.name": "MODEL_5_B",
    "board.description": "Raspberry Pi 5 Model B",
    "board.model.label": "Model B",
    "board.cpu.label": "Cortex-A76",
    "board.soc": "BCM2712",
    "java.version": "23.0.2",
    "java.runtime": "23.0.2+7",
    "java.vendor": "Azul Systems, Inc.",
    "java.vendor.version": "Zulu23.32+11-CA",
    "reading.volt.value": "0.8764",
    "reading.temperature.celsius": "46.6",
    "reading.temperature.fahrenheit": "115.88000000000001",
    "reading.uptime": "21:39:01 up 23 days,  8:36,  2 users,  load average: 0.35, 0.18, 0.06",
    "platform.current": "RaspberryPi Platform",
    "platform.raspberrypi.name": "RaspberryPi Platform",
    "platform.raspberrypi.description": "Pi4J Platform for the RaspberryPi series of products.",
    "provider.linuxfs-spi.name": "LinuxFS SPI Provider",
    "provider.linuxfs-spi.description": "com.pi4j.plugin.linuxfs.provider.spi.LinuxFsSpiProviderImpl",
    "provider.linuxfs-spi.type.name": "SPI",
    "provider.gpiod-digital-output.name": "GpioD Digital Output (GPIO) Provider",
    "provider.gpiod-digital-output.description": "com.pi4j.plugin.gpiod.provider.gpio.digital.GpioDDigitalOutputProviderImpl",
    "provider.gpiod-digital-output.type.name": "DIGITAL_OUTPUT",
    "provider.pigpio-serial.name": "PiGpio Serial Provider",
    "provider.pigpio-serial.description": "com.pi4j.plugin.pigpio.provider.serial.PiGpioSerialProviderImpl",
    "provider.pigpio-serial.type.name": "SERIAL",
    "provider.linuxfs-pwm.name": "LinuxFS PWM Provider",
    "provider.linuxfs-pwm.description": "com.pi4j.plugin.linuxfs.provider.pwm.LinuxFsPwmProviderImpl",
    "provider.linuxfs-pwm.type.name": "PWM",
    "provider.gpiod-digital-input.name": "GpioD Digital Input (GPIO) Provider",
    "provider.gpiod-digital-input.description": "com.pi4j.plugin.gpiod.provider.gpio.digital.GpioDDigitalInputProviderImpl",
    "provider.gpiod-digital-input.type.name": "DIGITAL_INPUT",
    "provider.linuxfs-i2c.name": "LinuxFS I2C Provider",
    "provider.linuxfs-i2c.description": "com.pi4j.plugin.linuxfs.provider.i2c.LinuxFsI2CProviderImpl",
    "provider.linuxfs-i2c.type.name": "I2C"
  }
}
```

You can toggle on/off the LED using REST endpoints

ON `curl -v -X PUT http://0.0.0.0:8080/api/pi4j/led/true`

OFF `curl -v -X PUT http://0.0.0.0:8080/api/pi4j/led/false`
