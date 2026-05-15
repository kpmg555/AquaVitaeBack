package com.aquavitae;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class GreetingResourceTest {
    @Test
    public void testDashboardRiesgo() {
        given()
                .when().get("/api/dashboard")
                .then()
                .statusCode(200);
    }

}