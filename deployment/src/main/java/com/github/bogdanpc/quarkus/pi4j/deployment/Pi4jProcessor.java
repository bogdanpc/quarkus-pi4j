package com.github.bogdanpc.quarkus.pi4j.deployment;

import com.github.bogdanpc.quarkus.pi4j.runtime.ContextConfiguration;
import com.github.bogdanpc.quarkus.pi4j.runtime.Pi4jBuildTimeConfig;
import com.github.bogdanpc.quarkus.pi4j.runtime.Pi4jHealthCheck;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.smallrye.health.deployment.spi.HealthBuildItem;

class Pi4jProcessor {

    private static final String FEATURE = "pi4j";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    AdditionalBeanBuildItem additionalBeans() {
        return new AdditionalBeanBuildItem(ContextConfiguration.class);
    }

    @BuildStep
    void addHealthCheck(Pi4jBuildTimeConfig config, BuildProducer<HealthBuildItem> healthChecks) {
        healthChecks.produce(new HealthBuildItem(Pi4jHealthCheck.class.getName(), config.health().enabled()));
    }
}
