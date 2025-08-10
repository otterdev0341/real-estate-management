package auth.controller;

import auth.repository.implementation.GenderRepositoryImpl;
import auth.repository.internal.InternalGenderRepository;
import auth.repository.internal.InternalRoleRepository;
import auth.repository.internal.InternalUserRepository;
import com.spencerwi.either.Either;
import common.domain.entity.Gender;
import common.domain.entity.Role;
import common.domain.entity.User;
import common.errorStructure.RepositoryError;
import gender.controller.GenderInitializer;
import io.qameta.allure.*;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.MediaType;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;
import java.util.UUID;

import static io.restassured.RestAssured.given;

@QuarkusTest
@Tag("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Getter
@Setter
public class AuthControllerTest {

    @Inject
    InternalGenderRepository genderRepository;
    @Inject
    InternalRoleRepository roleRepository;
    @Inject
    InternalUserRepository userRepository;


    private UUID userId;
    private String test_email;
    private String test_password;

    @BeforeEach
    @Transactional
    void setUp() {
        System.out.println("Running @BeforeEach setup: Creating a new test user...");

        // Ensure foundational data exists. This is still necessary as our
        // migration only creates the tables, not the initial data.
        Gender gender = createGenderIfNotExist("male");
        Role role = createRoleIfNotExist("user");
        UUID random = UUID.randomUUID();
        String new_email = "test2" + random + "@gmail.com";
        this.setTest_email(new_email);
        User user = new User();
        String password = "password";
        this.setTest_password(password);
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        user.setEmail("test2" + random + "@gmail.com");
        user.setUsername("test_username" + random);
        user.setPassword(hashedPassword);
        user.setFirstName("test_first_name");
        user.setLastName("test_last_name");
        user.setGender(gender);
        user.setRole(role);

        Either<RepositoryError, User> user1 = userRepository.createUser(user);
        if (user1.isLeft()) {
            System.out.println("Error creating user: " + user1.getLeft().message());
            Assertions.fail("Failed to create user for test setup. Check repository logic.");
        } else {
            userId = user1.getRight().getId();
            System.out.println("User 'test2@gmail.com' successfully created.");
        }
    }

    @AfterAll
    @Transactional
    void tearDown() {
        Either<RepositoryError, Boolean> deleteResult = userRepository.deleteUserById(userId);
        if (deleteResult.isLeft() || deleteResult.getRight() == false) {
            System.out.println("Error deleting user: " + deleteResult.getLeft().message());
        }
    }


   
    @Test
    @Order(1)
    @Step("Sign In Test")
    @Epic("Auth Test")
    @Feature("Sign In")
    @Story("Sign In Test")
    @Description("Login with username and password")
    void signInTest() {
        System.out.println("Running signInTest...");
        JsonObject payload = Json.createObjectBuilder()
                .add("email", this.getTest_email())
                .add("password", this.getTest_password())
                .build();
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload.toString())
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200);
        System.out.println("✅ signInTest passed!");
    }

    @Test
    @Step("Sign In Test")
    @Epic("Auth Test")
    @Feature("Sign In")
    @Story("Sign In Test")
    @Description("Login with username and password")
    @Order(2)
    void signInTestKo() {
        System.out.println("Running signInTest...");
        JsonObject payload = Json.createObjectBuilder()
                .add("email", "")
                .add("password", "wrong_password")
                .build();
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload.toString())
                .when()
                .post("/auth/login")
                .then()
                .statusCode(400);
        System.out.println("✅ signInTest passed!");
    }



    // Helper methods to create data if it doesn't exist
    private Gender createGenderIfNotExist(String detail) {
        Either<RepositoryError, Optional<Gender>> targetGender = genderRepository.findByDetail(detail);
        if (targetGender.isLeft() || targetGender.getRight().isEmpty()) {
            Gender newGender = new Gender();
            newGender.setDetail(detail);
            return genderRepository.createGender(newGender).getRight();
        }
        return targetGender.getRight().get();
    }

    private Role createRoleIfNotExist(String detail) {
        Either<RepositoryError, Optional<Role>> targetRole = roleRepository.findUserRole();
        if (targetRole.isLeft() || targetRole.getRight().isEmpty()) {
            Role newRole = new Role();
            newRole.setDetail(detail);
            return roleRepository.createRole(newRole).getRight();
        }
        return targetRole.getRight().get();
    }

}
