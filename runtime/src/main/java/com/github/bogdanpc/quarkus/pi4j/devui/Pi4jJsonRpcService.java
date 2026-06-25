package com.github.bogdanpc.quarkus.pi4j.devui;

import com.pi4j.context.Context;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class Pi4jJsonRpcService {
    @Inject
    Context pi4jContext;

    public Map<String, Object> getInfo() {
        return Map.of(
                "platform", getActivePlatform(),
                "platforms", getPlatforms(),
                "providers", getProviders());
    }

    public List<Map<String, String>> getPlatforms() {
        return pi4jContext.platforms().all().values().stream()
                .map(p -> Map.of("id", p.id(), "name", p.name()))
                .toList();
    }

    public List<Map<String, String>> getProviders() {
        return pi4jContext.providers().all().values().stream()
                .map(p -> Map.of("id", p.id(), "name", p.name()))
                .toList();
    }

    public String getActivePlatform() {
        try {
            return pi4jContext.platform().name();
        } catch (Exception e) {
            return "none";
        }
    }

    /**
     * Number of registered platforms, rendered as the dynamic label on the "Platforms" link.
     * */
    public int getPlatformCount() {
        return pi4jContext.platforms().all().size();
    }

    /**
     * Number of registered providers, rendered as the dynamic label on the "Providers" link.
     * */
    public int getProviderCount() {
        return pi4jContext.providers().all().size();
    }
}
