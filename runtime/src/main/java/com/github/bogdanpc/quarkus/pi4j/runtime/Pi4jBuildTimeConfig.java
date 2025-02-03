package com.github.bogdanpc.quarkus.pi4j.runtime;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
@ConfigMapping(prefix = "quarkus.pi4j")
public interface Pi4jBuildTimeConfig {

    /**
     * Health check configuration
     */
    @WithName("health")
    HealthCheckConfig health();

    /**
     * Health check configuration.
     */
    interface HealthCheckConfig {
        /**
         * Whether a health check is published in case the smallrye-health extension is present.
         */
        @WithName("enabled")
        @WithDefault("true")
        boolean enabled();
    }
}
