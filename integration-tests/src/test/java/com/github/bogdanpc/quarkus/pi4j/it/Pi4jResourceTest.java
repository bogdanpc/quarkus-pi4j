package com.github.bogdanpc.quarkus.pi4j.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class Pi4jResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/pi4j")
                .then()
                .statusCode(200)
                .body(is("Hello Pi4J Platform for the RaspberryPi series of products."));
    }

    @Test
    public void healthCheckHasPi4JInfo() {
        given()
                .when().get("/q/health/ready")
                .then()
                .statusCode(200)
                .body("status", is("UP"))
                .body("checks[0].name", is("Pi4J health check"))
                .body("checks[0].status", is("UP"))
                .body("checks[0].data.'board.name'", notNullValue())
                .body("checks[0].data.'java.version'", notNullValue());
    }
}
