package lp.edu.fstats.controller.auth;

import lp.edu.fstats.base.IntegrationTestBase;
import lp.edu.fstats.model.user.Role;
import lp.edu.fstats.model.user.User;
import lp.edu.fstats.support.helper.AuthTestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AuthControllerIT extends IntegrationTestBase {

    @Autowired
    private AuthTestHelper authTestHelper;

    //Register
    @Test
    void registerUser_shouldReturnCreatedAndSetCookie_whenDataIsValid() {
        String body = """
              {
                "username": "novo_usuario",
                "email":"novo_usuario@test.com",
                "password":"senha12345",
                "confirmPassword":"senha12345",
                "dateOfBirth":"2000-01-01"
              }
              """;

        given()
                .contentType("application/json")
                .body(body)
        .when()
                .post("/auth/register")
        .then()
                .statusCode(201)
                .cookie("access_token", not(emptyString()))
                .body("operation", equalTo("Auth.Register"))
                .body("code", equalTo(201));
    }

    @Test
    void register_shouldReturnBadRequest_whenPasswordsDontMatch(){
        String body = """
                  {
                    "username":"senha_diff",
                    "email":"senha_diff@test.com",
                    "password":"senha_diff",
                    "confirmPassword":"outraSenha123",
                    "dateOfBirth":"2000-01-01"
                  }
                  """;

        given()
                .contentType("application/json")
                .body(body)
        .when()
                .post("/auth/register")
        .then()
                .statusCode(400)
                .body("operation", equalTo("Error.BadRequest"))
                .body("message", equalTo("As senhas informadas não são semelhantes."));
    }

    @Test
    void register_shouldReturnBadRequest_whenEmailAlreadyExists(){
        authTestHelper.createUser(
                "existing_user",
                    "duplicado@test.com",
                "senha12345",
                Role.USER
                );

        String body = """
                  {
                    "username": "outro_usuario",
                    "email": "duplicado@test.com",
                    "password": "senha12345",
                    "confirmPassword": "senha12345",
                    "dateOfBirth": "2000-01-01"
                  }
                  """;

        given()
                .contentType("application/json")
                .body(body)
        .when()
                .post("/auth/register")
        .then()
                .statusCode(400)
                .body("operation", equalTo("Error.DuplicateField"))
                .body("fieldErrors.email", equalTo("Este e-mail já está sendo utilizado por outra conta."));
    }

    @Test
    void register_shouldReturnBadRequest_whenUsernameAlreadyExists(){
        authTestHelper.createUser(
                "usuario_duplicado",
                "primeiro@test.com",
                "senha12345",
                Role.USER
        );

        String body = """
                  {
                    "username": "usuario_duplicado",
                    "email": "segundo@test.com",
                    "password": "senha12345",
                    "confirmPassword": "senha12345",
                    "dateOfBirth": "2000-01-01"
                  }
                  """;

        given()
                .contentType("application/json")
                .body(body)
        .when()
                .post("/auth/register")
        .then()
                .statusCode(400)
                .body("operation", equalTo("Error.DuplicateField"))
                .body("fieldErrors.username", equalTo("Este nome de usuário já está sendo utilizado por outra conta."));
    }

    @Test
    void register_shouldReturnBadRequest_whenUserIsUnderage(){
        String body = """
                  {
                    "username": "usuario_menor",
                    "email": "usuario_menor@test.com",
                    "password": "senha12345",
                    "confirmPassword": "senha12345",
                    "dateOfBirth": "%s"
                  }
                  """.formatted(LocalDate.now().minusYears(10));

        given()
                .contentType("application/json")
                .body(body)
        .when()
                .post("/auth/register")
        .then()
                .statusCode(400)
                .body("operation", equalTo("Error.Validation"))
                .body("fieldErrors.dateOfBirth", equalTo("O usuário deve ter pelo menos 18 anos de idade."));
    }

    @Test
    void register_shouldReturnBadRequest_whenUsernameIsBlank(){
        String body = """
                  {
                    "username": "",
                    "email": "sem_username@test.com",
                    "password": "senha12345",
                    "confirmPassword": "senha12345",
                    "dateOfBirth": "2000-01-01"
                  }
                  """;

        given()
                .contentType("application/json")
                .body(body)
        .when()
                .post("/auth/register")
        .then()
                .statusCode(400)
                .body("operation", equalTo("Error.Validation"))
                .body("fieldErrors.username", equalTo("Informe o nome de usuário."));
    }

    @Test
    void register_shouldReturnBadRequest_whenEmailIsInvalid(){
        String body = """
                  {
                    "username": "email_invalido",
                    "email": "nao-e-um-email",
                    "password": "senha12345",
                    "confirmPassword": "senha12345",
                    "dateOfBirth": "2000-01-01"
                  }
                  """;

        given()
                .contentType("application/json")
                .body(body)
        .when()
                .post("/auth/register")
        .then()
                .statusCode(400)
                .body("operation", equalTo("Error.Validation"))
                .body("fieldErrors.email", equalTo("Informe um e-mail válido."));
    }

    //loginUser

    @Test
    void loginUser_shouldReturnOkAndSetCookie_whenCredentialsAreValidUsingUsername() {
        authTestHelper.createUser(
                "login_user",
                "login_user@test.com",
                "senha12345", Role.USER
        );

        String body = """
              {
                "login": "login_user",
                "password": "senha12345"
              }
              """;

        given()
                .contentType("application/json")
                .body(body)
        .when()
                .post("/auth/login")
        .then()
                .statusCode(200)
                .cookie("access_token", not(emptyString()))
                .body("operation", equalTo("Auth.Login"))
                .body("code", equalTo(200));
    }

    @Test
    void loginUser_shouldReturnOkAndSetCookie_whenCredentialsAreValidUsingEmail(){

        authTestHelper.createUser(
                "login_user_email",
                "login_email@test.com",
                "senha12345",
                Role.USER
        );

        String body = """
                  {
                    "login": "login_email@test.com",
                    "password": "senha12345"
                  }
                  """;


        given()
                .contentType("application/json")
                .body(body)
        .when()
                .post("/auth/login")
        .then()
                .statusCode(200)
                .cookie("access_token", not(emptyString()));
    }

    @Test
    void loginUser_shouldReturnForbidden_whenPasswordIsIncorrect(){
        authTestHelper.createUser(
                "wrong_pass_user",
                "wrong_pass@test.com",
                "senhaCorreta1",
                Role.USER
        );

        String body = """
                  {
                    "login": "wrong_pass_user",
                    "password": "senhaErrada1",
                  }
                  """;

        given()
                .contentType("application/json")
                .body(body)
        .when()
                .post("/auth/login")
        .then()
                .statusCode(403)
                .body("operation", equalTo("Error.ForbiddenAction"));
    }

    @Test
    void login_shouldReturnForbidden_whenUserDoesNotExist(){
        String body = """
                  {
                    "login": "usuario_inexistente",
                    "password": "senha12345"
                  }
                  """;

        given()
                .contentType("application/json")
                .body(body)
        .when()
                .post("/auth/login")
        .then()
                .statusCode(401)
                .body("operation", equalTo("Error.Unauthorized"));
    }

}
