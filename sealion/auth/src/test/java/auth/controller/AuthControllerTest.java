package auth.controller;

import auth.controller.internal.InternalAuthController;
import common.domain.dto.auth.ReqLoginDto;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@Tag("integration")
@TestMethodOrder(MethodOrderer.class)
public class AuthControllerTest {

    @Inject
    InternalAuthController authController;

    @Test
    @Order(1)
    void testLoginSuccess() {

        JsonObject payload = Json.
                createObjectBuilder()
                .add("email", "test@gmail.com")
                .add("password", "password")
                .build();

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(payload.toString())
        .when()
            .post("/auth/login")
            .then()
            .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    void testLoginFailure() {
        ReqLoginDto loginDto = new ReqLoginDto("test@gmail.com", "wrongPassword");
        Response response = authController.login(loginDto);
        assertEquals(400, response.getStatus());
    }
}