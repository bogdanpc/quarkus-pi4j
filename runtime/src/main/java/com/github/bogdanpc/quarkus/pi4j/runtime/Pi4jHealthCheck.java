package com.github.bogdanpc.quarkus.pi4j.runtime;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.jboss.logging.Logger;

import com.pi4j.boardinfo.model.BoardInfo;
import com.pi4j.boardinfo.util.BoardInfoHelper;
import com.pi4j.context.Context;

/**
 * Quarkus Health Check
 * Similar with <a href=
 * "https://github.com/Pi4J/pi4j-springboot/blob/main/pi4j-spring-boot/src/main/java/com/pi4j/spring/boot/Pi4jActuatorConfiguration.java">...</a>
 */
@Readiness
@ApplicationScoped
public class Pi4jHealthCheck implements HealthCheck {
    private final BoardInfo boardInfo;
    private final Context context;
    private final static Logger logger = Logger.getLogger(HealthCheck.class);

    public Pi4jHealthCheck(Context context) {
        this.context = context;
        boardInfo = BoardInfoHelper.current();
    }

    @Override
    public HealthCheckResponse call() {
        var os = boardInfo.getOperatingSystem();
        var boardModel = boardInfo.getBoardModel();
        var java = boardInfo.getJavaInfo();
        var boardReading = BoardInfoHelper.getBoardReading();
        var builder = HealthCheckResponse.named("Pi4J health check");

        try {
            builder.withData("os.name", os.getName())
                    .withData("os.architecture", os.getArchitecture())
                    .withData("os.version", os.getVersion())
                    .withData("os.architecture", os.getArchitecture())
                    .withData("board.name", boardModel.getName())
                    .withData("board.description", boardModel.getLabel())
                    .withData("board.model.label", boardModel.getModel().getLabel())
                    .withData("board.cpu.label", boardModel.getCpu().getLabel())
                    .withData("board.soc", boardModel.getSoc().name())
                    .withData("java.version", java.getVersion())
                    .withData("java.runtime", java.getRuntime())
                    .withData("java.vendor", java.getVendor())
                    .withData("java.vendor.version", java.getVendorVersion())
                    .withData("reading.volt.value", String.valueOf(boardReading.getVoltValue()))
                    .withData("reading.temperature.celsius", String.valueOf(boardReading.getTemperatureInCelsius()))
                    .withData("reading.temperature.fahrenheit", String.valueOf(boardReading.getTemperatureInFahrenheit()))
                    .withData("reading.uptime", boardReading.getUptimeInfo().trim());

            try {
                builder.withData("platform.current", context.platform().name());
            } catch (Exception ex) {
                logger.errorf("Could not return the Pi4J Default Platform: %s", ex.getMessage());
            }

            try {
                for (var entry : context.platforms().all().entrySet()) {
                    builder.withData("platform." + getAsKeyName(entry.getKey()) + ".name", entry.getValue().name());
                    builder.withData("platform." + getAsKeyName(entry.getKey()) + ".description",
                            entry.getValue().description());
                }
            } catch (Exception ex) {
                logger.errorf("Could not return the Pi4J Platforms: %s", ex.getMessage());
            }

            try {
                for (var entry : context.providers().all().entrySet()) {
                    builder.withData("provider." + getAsKeyName(entry.getKey()) + ".name", entry.getValue().name());
                    builder.withData("provider." + getAsKeyName(entry.getKey()) + ".description",
                            entry.getValue().description());
                    builder.withData("provider." + getAsKeyName(entry.getKey()) + ".type.name", entry.getValue().type().name());
                }
            } catch (Exception ex) {
                logger.errorf("Could not return the Pi4J Providers: %s", ex.getMessage());
            }

            try {
                for (var entry : context.registry().all().entrySet()) {
                    builder.withData("registry." + getAsKeyName(entry.getKey()) + ".name", entry.getValue().name());
                    builder.withData("registry." + getAsKeyName(entry.getKey()) + ".type.name", entry.getValue().type().name());
                    builder.withData("registry." + getAsKeyName(entry.getKey()) + ".description",
                            entry.getValue().description());
                }
            } catch (Exception ex) {
                logger.errorf("Could not return the Pi4J Registry: %s", ex.getMessage());
            }

            return builder.up().build();
        } catch (Exception ex) {
            return builder.down().withData("reason", ex.getMessage()).build();
        }
    }

    private String getAsKeyName(String key) {
        return key.toLowerCase().trim().replace(" ", "-");
    }
}
