package lp.edu.fstats.controller.code;

import lp.edu.fstats.base.IntegrationTestBase;
import lp.edu.fstats.support.helper.AuthTestHelper;
import lp.edu.fstats.model.code.Code;
import lp.edu.fstats.model.user.Role;
import lp.edu.fstats.model.user.User;
import lp.edu.fstats.repository.code.CodeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CodeControllerIT extends IntegrationTestBase {

    @Autowired
    private AuthTestHelper authTestHelper;

    @Autowired
    private CodeRepository codeRepository;

    @MockitoSpyBean
    private CodeRepository codeRepositorySpy;

    //getAll
    @Test
    void getAllCodes_shouldReturnOk_whenUserIsAuthenticated(){

        User user = authTestHelper.createDefaultUser("senha12345");

        given()
                .cookie(authTestHelper.authCookie(user))
        .when()
                .get("/competition/code")
        .then()
                .statusCode(200)
                .body("operation", equalTo("Code.GetAll"))
                .body("message", equalTo("Competições ativas encontradas com sucesso."));
    }

    @Test
    void getAllCodes_shouldReturnUnauthorized_whenNoCookiesIsProvided(){
        given()
        .when()
                .get("/competition/code")
        .then()
                .statusCode(401)
                .body("operation", equalTo("Error.Unauthorized"));
    }

    // getAll - RateLimit)
    @Test
    void getAllCodes_shouldReturnTooManyRequests_whenRateLimitIsExceeded(){
        User user = authTestHelper.createDefaultUser("senha12345");

        for(int i = 0; i < 10; i++){
            given()
                    .cookie(authTestHelper.authCookie(user))
            .when()
                    .get("/competition/code")
            .then()
                    .statusCode(200);
        }

        given()
                .cookie(authTestHelper.authCookie(user))
        .when()
                .get("/competition/code")
        .then()
                .statusCode(429)
                .body("operation", equalTo("Error.RateLimitExceeded"))
                .body("message", equalTo("Muitas requisições. Tente novamente em alguns minutos."));


    }

    // getAll - Cache
    @Test
    void getAllCodes_shouldHitRepositoryOnlyOnce_whenCalledTwiceInARow(){
        User user = authTestHelper.createDefaultUser("senha12345");

        Code code = new Code();
        code.setCode("BSA");
        code.setName("Brasileirão Série A");
        codeRepository.save(code);

        given()
                .cookie(authTestHelper.authCookie(user))
        .when()
                .get("/competition/code")
        .then()
                .statusCode(200)
                .body("message", equalTo("Competições ativas encontradas com sucesso."));

        given()
                .cookie(authTestHelper.authCookie(user))
        .when()
                .get("/competition/code")
        .then()
                .statusCode(200)
                .body("message", equalTo("Competições ativas encontradas com sucesso."));

        verify(codeRepositorySpy, times(1)).findAll();
    }

    // create
    @Test
    void createCode_shouldReturnCreatedWithBody_whenUserIsAdmin(){
        User admin = authTestHelper.createUser(
                "admin_code",
                "admin_code@test.com",
                "senha12345",
                Role.ADMIN);

        String body = """
                  {
                    "code":"PL",
                    "name":"Premier League"
                  }
                  """;

        given()
                .cookie(authTestHelper.authCookie(admin))
                .contentType("application/json")
                .body(body)
        .when()
                .post("/competition/code")
        .then()
                .statusCode(201)
                .body("operation", equalTo("Code.Create"))
                .body("data.code", equalTo("PL"))
                .body("data.name", equalTo("Premier League"));
    }

    @Test
    void createCode_shouldReturnAccessDenied_whenUserIsNotAdmin(){
        User user = authTestHelper.createDefaultUser("senha12345");

        String body = """
                  {
                    "code":"PL",
                    "name":"Premier League"
                  }
                  """;

        given()
                .cookie(authTestHelper.authCookie(user))
                .contentType("application/json")
                .body(body)
        .when()
                .post("/competition/code")
        .then()
                .statusCode(403)
                .body("operation", equalTo("Error.AccessDenied"));
    }

    @Test
    void createCode_shouldReturnBadRequest_whenCodeAlreadyExists(){
        User admin = authTestHelper.createUser(
                "admin_dup",
                "admin_dup@test.com",
                "senha12345",
                Role.ADMIN
        );


        String body = """
                  {
                    "code": "PL",
                    "name":"Premier League"
                  }
                  """;

        given()
                .cookie(authTestHelper.authCookie(admin))
                .contentType("application/json")
                .body(body)
        .when()
                .post("/competition/code")
        .then()
                .statusCode(201);

        given()
                .cookie(authTestHelper.authCookie(admin))
                .contentType("application/json")
                .body(body)
        .when()
                .post("/competition/code")
        .then()
                .statusCode(400)
                .body("operation", equalTo("Error.DuplicateField"))
                .body("fieldErrors.code", equalTo("Esse código já existe."));
    }

    // create - cache evict
    @Test
    void createCode_shouldEvictCache_whenNewCodeIsCreated(){
        User admin = authTestHelper.createUser(
                "admin_create",
                "admin_create@test.com",
                "Senha12345",
                Role.ADMIN
        );

        given()
                .cookie(authTestHelper.authCookie(admin))
        .when()
                .get("/competition/code")
        .then()
                .statusCode(200)
                .body("data.codes.size()", equalTo(0));

        String body = """
                  {
                    "code": "PL",
                    "name":"Premier League"
                  }
                  """;

        given()
                .cookie(authTestHelper.authCookie(admin))
                .contentType("application/json")
                .body(body)
        .when()
                .post("/competition/code")
        .then()
                .statusCode(201);

        given()
                .cookie(authTestHelper.authCookie(admin))
                .when()
                .get("/competition/code")
                .then()
                .statusCode(200)
                .body("data.codes.size()", equalTo(1));
    }

    //delete
    @Test
    void deleteCode_shouldReturnOk_whenUserIsAdmin(){
        User admin = authTestHelper.createUser(
                "admin_delete",
                "admin_delete@test.com",
                "senha12345",
                Role.ADMIN);

        String body = """
                  {
                    "code": "SA",
                    "name":"Serie A"
                  }
                  """;

        int createdId =
                given()
                        .cookie(authTestHelper.authCookie(admin))
                        .contentType("application/json")
                        .body(body)
                .when()
                        .post("/competition/code")
                        .jsonPath()
                        .getInt("data.id");

        given()
                .cookie(authTestHelper.authCookie(admin))
        .when()
                .delete("/competition/code/" + createdId)
        .then()
                .statusCode(200)
                .body("operation", equalTo("Code.Delete"));

    }

    @Test
    void deleteCod_shouldReturnAccessDenied_whenUserIsNotAdmin(){
        User user = authTestHelper.createDefaultUser("senha12345");

        given()
                .cookie(authTestHelper.authCookie(user))
        .when()
                .delete("/competition/code/1")
        .then()
                .statusCode(403)
                .body("operation", equalTo("Error.AccessDenied"));
    }

}
