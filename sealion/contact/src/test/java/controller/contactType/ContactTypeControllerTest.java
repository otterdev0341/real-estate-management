package controller.contactType;


import io.quarkus.test.junit.QuarkusTest;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

@QuarkusTest
@Tag("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Getter
@Setter
public class ContactTypeControllerTest {

    // test create with correct
    // test create with empty
    // test create with duplicate
    // test update
    // test find by id
    // test delete
    // test find deleted

}
