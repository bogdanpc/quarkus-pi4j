package com.github.bogdanpc.quarkus.pi4j.runtime;

import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import org.jboss.logging.Logger;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;

import io.quarkus.arc.DefaultBean;

@Singleton
public class ContextConfiguration {

    private static final Logger logger = Logger.getLogger(ContextConfiguration.class);

    @Produces
    @Singleton
    @DefaultBean
    public Context context() {
        try {
            return Pi4J.newAutoContext();
        } catch (Exception e) {
            logger.errorf("Pi4J library failed to initialize: %s", e.getMessage());
            throw new IllegalStateException("Failed to initialize Pi4J context", e);
        }
    }

    void shutdown(@Disposes Context context) {
        context.shutdown();
    }
}
