import com.google.gson.Gson;
import entities.Category;
import entities.Pet;
import entities.User;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

import static io.restassured.RestAssured.given;

public class ApiTests {
    String BASE_URL = "https://petstore.swagger.io/v2";

    @Test
    public void addNewPetToTheStore() {
        Category dogCategory = new Category(235, "Dogs");
        System.out.println("I preparing test data...");

        Pet addingNewPet = Pet.builder()
                .id(235)
                .category(dogCategory)
                .name("Ajka")
                .photoUrls(Collections.singletonList("urls"))
                .tags(null)
                .status("for free")
                .build();
        System.out.println("Body to send: " + new Gson().toJson(addingNewPet));

        Response addNewPet = given()
                .baseUri(BASE_URL)
                .basePath("/pet")
                .contentType(ContentType.JSON)
                .body(addingNewPet)
                .when()
                .post();
        System.out.println("response " + addNewPet.asString());


        long id = addingNewPet.getId();

        Response gettingInfoAboutPet = given()
                .baseUri(BASE_URL)
                .pathParam("petId", id)
                .when()
                .get("/pet/{petId}");
        System.out.println("Get " + gettingInfoAboutPet.asString());
        Pet gotPetResponse = gettingInfoAboutPet.as(Pet.class);
        Assert.assertEquals("Name isn`t compare", addingNewPet.getName(), gotPetResponse.getName());
    }

    @Test
    public void registrationNewUser() {
        User regNewUser = User.builder()
                .id(123)
                .username("Pedrio")
                .firstName("Pedro")
                .lastName("Sluth")
                .email("test@mail.com")
                .password("qaz1234567890")
                .phone("380999644418")
                .userStatus(5)
                .build();

        Response addNewUser = given()
                .baseUri(BASE_URL)
                .basePath("/user")
                .contentType(ContentType.JSON)
                .body(regNewUser)
                .when()
                .post();
        System.out.println("response " + regNewUser);

        Assert.assertEquals("Wrong status code", 200, addNewUser.getStatusCode());

        String mail = regNewUser.getEmail();

        Response gettingMail = given()
                .baseUri(BASE_URL)
                .pathParam("username", "Pedrio")
                .when()
                .get("/user/{username}");
        User getMailByName = gettingMail.as(User.class);
        Assert.assertEquals("wrong mail",getMailByName.getEmail(), regNewUser.getEmail());

    }
}
