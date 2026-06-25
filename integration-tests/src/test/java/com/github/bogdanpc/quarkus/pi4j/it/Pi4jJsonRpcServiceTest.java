package com.github.bogdanpc.quarkus.pi4j.it;

import com.github.bogdanpc.quarkus.pi4j.devui.Pi4jJsonRpcService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;


@QuarkusTest
class Pi4jJsonRpcServiceTest {
    @Inject
    Pi4jJsonRpcService service;

    @Test
    void getInfoReturnsContextData() {
        var info = service.getInfo();

        assertNotNull(info.get("platform"), "current platform name should be present");

        assertTrue(info.containsKey("platforms"));
        var platforms = (List<?>) info.get("platforms");
        assertFalse(platforms.isEmpty(), "at least one platform should be registered");
    }

}
