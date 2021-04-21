import com.google.gson.Gson;
import entities.Category;
import entities.Pet;
import entities.User;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class Practice {
    String BASE_URL = "https://petstore.swagger.io/v2";

    @Test
    //1. Добавить питомца с невалидным ID (длиной 20 цифр). Проверить, что возвращается код 500 и сообщение "something bad happened".
    public void practice() throws InterruptedException {
        BigInteger myID = new BigInteger("12345678912345678912");
        Category dogCategory = new Category(123, "Ajka");
        Pet addingNewPet = Pet.builder()
                .id(myID)
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
        Assert.assertEquals("something bad happened", 500, addNewPet.getStatusCode());
        MyResponce mess = addNewPet.as(MyResponce.class);
        Assert.assertEquals("Message dosn`t match", mess.getMessage(), "something bad happened");
    }

        @Test
        public void practice2() throws InterruptedException {
            String BASE_URL = "https://petstore.swagger.io/v2";
            BigInteger myID = new BigInteger("123");
            Category dogCategory = new Category(5, "Ajka");

        //2. Создать питомца,
        Pet addingNewPet1 = Pet.builder()
                .id(myID)
                .category(dogCategory)
                .name("Ajka")
                .photoUrls(Collections.singletonList("urls"))
                .tags(null)
                .status("priceless")
                .build();
        System.out.println("Body to send: " + new Gson().toJson(addingNewPet1));
        Response addNewPet1 = given()
                .baseUri(BASE_URL)
                .basePath("/pet")
                .contentType(ContentType.JSON)
                .body(addingNewPet1)
                .when()
                .post();
        System.out.println("response " + addNewPet1.asString());

        // получить код 200.

        BigInteger id = addingNewPet1.getId();
        Response gettingInfoAboutPet1 = given()
                .baseUri(BASE_URL)
                .pathParam("petId", id)
                .when()
                .get("/pet/{petId}");
        TimeUnit.SECONDS.sleep(5);
        Assert.assertEquals("pet added", 200, gettingInfoAboutPet1.getStatusCode());

        // Удалить питомца соответствующим запросом,
        Response deletePet = given()
                .baseUri(BASE_URL)
                .pathParam("petId", id)
                .when()
                .delete("/pet/{petId}");
        Assert.assertEquals("pet deleted", 200, deletePet.getStatusCode());

        // Проверить что он удалён(отправить GET запрос с его ID и получить ответ о том, что питомец не найден).
        Response gettingInfoAboutDeleting = given()
                .baseUri(BASE_URL)
                .pathParam("petId", 5)
                .when()
                .get("/pet/{petId}");
        TimeUnit.SECONDS.sleep(5);
        Assert.assertEquals("pet found", 404, gettingInfoAboutDeleting.getStatusCode());

        MyResponce mess = gettingInfoAboutDeleting.as(MyResponce.class);
            Assert.assertEquals("Message doesn`t match", "Pet not found", mess.getMessage());
    }
    @Test
    public void practice3() throws InterruptedException {
        String BASE_URL = "https://petstore.swagger.io/v2";
        //3. Добавить пользователя
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
        // получить код 200.
        Assert.assertEquals("Wrong status code", 200, addNewUser.getStatusCode());
        // Достать добавленного пользователя GET запросом
        Response getUser = given()
                .baseUri(BASE_URL)
                .pathParam("username", "Pedrio")
                .when()
                .get("/user/{username}")
                // сделать валидацию JSON SCHEMA полученного респонса.
                .then()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("UserSchema.json"))
                .extract()
                .response();
        System.out.println(getUser.asString());
    }
    @Test
    public void practice4() throws InterruptedException {
        //  4. Добавить питомца со статусом sold,
        BigInteger myID = new BigInteger("123");
        Category dogCategory = new Category(235, "Dogs");
        System.out.println("I preparing test data...");

        Pet addingNewPetBody = Pet.builder()
                .id(myID)
                .category(dogCategory)
                .name("Ajka")
                .photoUrls(Collections.singletonList("urls"))
                .tags(null)
                .status("sold")
                .build();
        System.out.println("Body to send: " + new Gson().toJson(addingNewPetBody));

        Response addNewPetPost = given()
                .baseUri(BASE_URL)
                .basePath("/pet")
                .contentType(ContentType.JSON)
                .body(addingNewPetBody)
                .when()
                .post();
        System.out.println("Response: " + addNewPetPost.asString());
        Assert.assertEquals("Status code in not 200", 200, addNewPetPost.getStatusCode());

        System.out.println("Test data for getting pets with status Sold is preparing...");

        TimeUnit.SECONDS.sleep(5);

        Response gettingPetsWithStatusSold = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .when()
                .get("/pet/findByStatus/?status=sold");

        System.out.println("Sold pets request done");

        List<Pet> petsSold = Arrays.stream(gettingPetsWithStatusSold.as(Pet[].class))
                .filter(pet -> pet.getId().equals(addingNewPetBody.getId()))
                .collect(Collectors.toList());

        Assert.assertEquals("Name is not needed", addingNewPetBody.getName(), petsSold.get(0).getName());
    }
    @Test
    public void practice5() throws InterruptedException {
           int idEmpty = 0;
            for (int i = 1; i <= 100; i++) {
                int gettingPetsResponse = given()
                        .baseUri(BASE_URL)
                        .contentType(ContentType.JSON)
                        .when()
                        .get("/pet/" + i)
                        .then()
                        .extract()
                        .statusCode();

                if (gettingPetsResponse != 200 && gettingPetsResponse ==404) {
                    idEmpty++;
                }
            }
            System.out.println(idEmpty);
        }
    }

















