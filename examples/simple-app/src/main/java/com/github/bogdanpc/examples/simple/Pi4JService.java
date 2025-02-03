package com.github.bogdanpc.examples.simple;

import com.pi4j.context.Context;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * <a href="https://github.com/Pi4J/pi4j-springboot/blob/main/pi4j-spring-boot-starter-sample-app">...</a>
 */
@ApplicationScoped
public class Pi4JService {

    private final Context pi4j;
    private static final int PIN_LED = 22; // PIN 15 = BCM 22
    private DigitalOutput led = null;

    public Pi4JService(Context pi4j) {
        this.pi4j = pi4j;
        try {
            // LED example is based on https://www.pi4j.com/getting-started/minimal-example-application/
            led = this.pi4j.digitalOutput().create(PIN_LED);
            Log.infof("LED initialized on pin %s", PIN_LED);
        } catch (IOException e) {
            Log.errorf("Error while initializing the LED: %s", e.getMessage());
        }
    }

    public boolean ledToggle(boolean state) {
        if (led == null) {
            Log.error("LED is not initialized");
            return false;
        }
        try {
            led.state(state ? DigitalState.HIGH : DigitalState.LOW);
            Log.infof("LED state is set to %s", state);
            return true;
        } catch (IOException e) {
            Log.errorf("Error while changing the LED status: %s", e.getMessage());
        }

        return false;
    }
}
