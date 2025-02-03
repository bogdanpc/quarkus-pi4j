package com.github.bogdanpc.quarkus.pi4j.runtime;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import org.jboss.logging.Logger;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;

import io.quarkus.arc.DefaultBean;

@Singleton
public class ContextConfiguration {
    public static Logger logger = Logger.getLogger(ContextConfiguration.class);
    private Context pi4j;

    @Produces
    @DefaultBean
    public Context context() {
        try {
            this.pi4j = Pi4J.newAutoContext();
            return this.pi4j;
        } catch (Exception e) {
            logger.errorf("Pi4J library failed to initialize: %s", e.getMessage());
            return null;
        }
    }

    @PreDestroy
    void shutdown() {
        this.pi4j.shutdown();
    }
}
