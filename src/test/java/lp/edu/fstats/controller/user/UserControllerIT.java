package lp.edu.fstats.controller.user;

import lp.edu.fstats.base.IntegrationTestBase;
import lp.edu.fstats.support.helper.AuthTestHelper;
import lp.edu.fstats.model.user.Role;
import lp.edu.fstats.model.user.User;
import lp.edu.fstats.service.verification.VerificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserControllerIT extends IntegrationTestBase {

    @Autowired
    private AuthTestHelper authTestHelper;

    @MockitoBean
    private VerificationService verificationService;

    //=========================== getUser ===========================
    @Test
    void getUser_shouldReturnOk_whenRequesterIsSelf(){
        User user = authTestHelper.createDefaultUser("senha12345");

        given()
                .cookie(authTestHelper.authCookie(user))
        .when()
                .get("/user/{username}/details", user.getUsername())
        .then()
                .statusCode(200)
                .body("operation", equalTo("User.GetByUsername"))
                .body("message", equalTo("Usuário encontrado com sucesso."))
                .body("data.username", equalTo(user.getUsername()));
    }

    @Test
    void getUser_shouldReturnOk_whenRequesterIsAdmin(){
        User target = authTestHelper.createDefaultUser("senha12345");
        User admin = authTestHelper.createUser(
                "admin_get",
                "admin_get@test.com",
                "senha12345",
                Role.ADMIN
        );

        given()
                .cookie(authTestHelper.authCookie(admin))
        .when()
                .get("/user/{username}/details", target.getUsername())
        .then()
                .statusCode(200)
                .body("operation", equalTo("User.GetByUsername"))
                .body("message", equalTo("Usuário encontrado com sucesso."))
                .body("data.username", equalTo(target.getUsername()));
    }

    @Test
    void getUser_shouldReturnForbidden_whenRequesterIsNotSelfOrAdmin(){
        User target = authTestHelper.createDefaultUser("senha12345");
        User other = authTestHelper.createUser(
                "other_user",
                "other_user@test.com",
                "senha12345",
                Role.USER
        );

        given()
                .cookie(authTestHelper.authCookie(other))
        .when()
                .get("/user/{username}/details", target.getUsername())
        .then()
                .statusCode(403)
                .body("operation", equalTo("Error.ForbiddenAction"))
                .body("message", equalTo("Ação não permitida. Apenas o próprio usuário ou um moderador pode realizar esta operação."));
    }

    @Test
    void getUser_shouldReturnNotFound_whenUserDoesNotExist(){
        User requester = authTestHelper.createUser(
                "admin",
                "admin@test.com",
                "senha12345",
                Role.ADMIN
        );

        given()
                .cookie(authTestHelper.authCookie(requester))
        .when()
                .get("/user/{username}/details", "usuario_inexistente")
        .then()
                .statusCode(404)
                .body("operation", equalTo("Error.NotFound"))
                .body("message", equalTo("Usuário não encontrado. Verifique as informações e tente novamente."));
    }

    //=========================== getShortUser ===========================
    @Test
    void getShortUser_shouldReturnOk_forAnyAuthenticateUser(){
        User target = authTestHelper.createDefaultUser("senha12345");
        User requester = authTestHelper.createUser(
                "requester_short",
                "requester_short@test.com",
                "senha12345",
                Role.USER
        );

        given()
                .cookie(authTestHelper.authCookie(requester))
        .when()
                .get("/user/{username}", target.getUsername())
        .then()
                .statusCode(200)
                .body("operation", equalTo("User.GetShortInfoByUsername"))
                .body("message", equalTo("Usuário encontrado com sucesso."))
                .body("data.username", equalTo(target.getUsername()));
    }

    //=========================== getUsersBySearch ===========================
    @Test
    void getUsersBySearch_shouldReturnMatchingUsers_whenSearchMatchesUsername(){
        User requester = authTestHelper.createDefaultUser("senha12345");
        authTestHelper.createUser("joao_silva", "joao@test.com", "senha12345", Role.USER);
        authTestHelper.createUser("joao_pedro", "pedro@test.com", "senha12345", Role.USER);
        authTestHelper.createUser("maria_costa", "maria@test.com", "senha12345", Role.USER);

        given()
                .cookie(authTestHelper.authCookie(requester))
                .queryParam("search", "joao")
                .queryParam("page", 0)
        .when()
                .get("/user/search")
        .then()
                .statusCode(200)
                .body("content.size()", equalTo(2));
    }

    @Test
    void getUsersBySearch_shouldReturnEmptyContent_whenNoUserMatches(){
        User requester = authTestHelper.createDefaultUser("senha12345");

        given()
                .cookie(authTestHelper.authCookie(requester))
                .queryParam("search", "inexistente")
                .queryParam("page", 0)
        .when()
                .get("/user/search")
        .then()
                .statusCode(200)
                .body("content.size()", equalTo(0));
    }

    //=========================== updateUser ===========================
    @Test
    void updateUser_shouldReturnOk_whenRequesterIsSelf(){
        User user = authTestHelper.createDefaultUser("senha12345");

        String body = """
                      {
                        "profilePicture": "https://example.com/pic.png",
                        "bio": "Nova bio",
                        "dateOfBirth": "2000-01-01"
                      }
                      """;

        given()
                .cookie(authTestHelper.authCookie(user))
                .contentType("application/json")
                .body(body)
        .when()
                .put("/user/{username}", user.getUsername())
        .then()
                .statusCode(200)
                .body("message", equalTo("Usuário atualizado com sucesso."))
                .body("data.bio", equalTo("Nova bio"));
    }

    @Test
    void updateUser_shouldReturnForbidden_whenRequesterIsNotSelfOrAdmin(){
        User target = authTestHelper.createDefaultUser("senha12345");
        User other = authTestHelper.createUser(
                "other_update",
                "other_update@test.com",
                "senha12345",
                Role.USER
        );

        String body = """
                      {
                        "profilePicture": "https://example.com/pic.png",
                        "bio": "Tentando alterar",
                        "dateOfBirth": "2000-01-01"
                      }
                      """;

        given()
                .cookie(authTestHelper.authCookie(other))
                .contentType("application/json")
                .body(body)
        .when()
                .put("/user/{username}", target.getUsername())
        .then()
                .statusCode(403)
                .body("operation", equalTo("Error.ForbiddenAction"))
                .body("message", equalTo("Ação não permitida. Apenas o próprio usuário ou um moderador pode realizar esta operação."));
    }

    @Test
    void updateUser_shouldReturnBadRequest_whenUserIsUnderage(){
        User user = authTestHelper.createDefaultUser("senha12345");

        String body = """
                      {
                        "profilePicture": "https://example.com/pic.png",
                        "bio": "Bio",
                        "dateOfBirth": "%s"
                      }
                      """.formatted(LocalDate.now().minusYears(5));

        given()
                .cookie(authTestHelper.authCookie(user))
                .contentType("application/json")
                .body(body)
        .when()
                .put("/user/{username}", user.getUsername())
        .then()
                .statusCode(400)
                .body("operation", equalTo("Error.Validation"))
                .body("fieldErrors.dateOfBirth", equalTo("O usuário deve ter pelo menos 18 anos de idade."));
    }

    //=========================== updateUserPassword ===========================
    @Test
    void updateUserPassword_shouldReturnOk_whenRequesterIsSelfAndDataIsValid(){
        User user = authTestHelper.createDefaultUser("senhaAtual1");

        String body = """
                      {
                        "currentPassword": "senhaAtual1",
                        "newPassword": "senhaNova1",
                        "confirmNewPassword": "senhaNova1"
                      }
                      """;

        given()
                .cookie(authTestHelper.authCookie(user))
                .contentType("application/json")
                .body(body)
        .when()
                .patch("/user/{username}/password", user.getUsername())
        .then()
                .statusCode(200)
                .body("operation", equalTo("User.UpdatePassword"))
                .body("message", equalTo("Senha atualizada com sucesso."));
    }

    @Test
    void updateUserPassword_shouldReturnForbidden_whenRequesterIsNotSelf_evenIfAdmin(){
        User target = authTestHelper.createDefaultUser("senhaAtual1");
        User admin = authTestHelper.createUser(
                "admin_pass",
                "admin_pass@test.com",
                "senha12345",
                Role.ADMIN
        );

        String body = """
                      {
                        "currentPassword": "senhaAtual1",
                        "newPassword": "senhaNova1",
                        "confirmNewPassword": "senhaNova1"
                      }
                      """;

        given()
                .cookie(authTestHelper.authCookie(admin))
                .contentType("application/json")
                .body(body)
        .when()
                .patch("/user/{username}/password", target.getUsername())
        .then()
                .statusCode(403)
                .body("operation", equalTo("Error.ForbiddenAction"))
                .body("message", equalTo("Ação não permitida. Apenas o próprio usuário ou um moderador pode realizar esta operação."));
    }

    @Test
    void updateUserPassword_shouldReturnBadRequest_whenCurrentPasswordIsInvalid(){
        User user = authTestHelper.createDefaultUser("senhaAtual1");

        String body = """
                      {
                        "currentPassword": "senhaErrada1",
                        "newPassword": "senhaNova1",
                        "confirmNewPassword": "senhaNova1"
                      }
                      """;

        given()
                .cookie(authTestHelper.authCookie(user))
                .contentType("application/json")
                .body(body)
        .when()
                .patch("/user/{username}/password", user.getUsername())
        .then()
                .statusCode(400)
                .body("operation", equalTo("Error.BadRequest"))
                .body("message", equalTo("A senha atual informada está incorreta."));

    }

    @Test
    void updateUserPassword_shouldReturnBadRequest_whenNewPasswordsDontMatch(){
        User user = authTestHelper.createDefaultUser("senhaAtual1");

        String body = """
                      {
                        "currentPassword": "senhaAtual1",
                        "newPassword": "senhaNova1",
                        "confirmNewPassword": "senhaDiferente1"
                      }
                      """;

        given()
                .cookie(authTestHelper.authCookie(user))
                .contentType("application/json")
                .body(body)
        .when()
                .patch("/user/{username}/password", user.getUsername())
        .then()
                .statusCode(400)
                .body("operation", equalTo("Error.BadRequest"))
                .body("message", equalTo("As senhas informadas não são semelhantes."));
    }

    @Test
    void updateUserPassword_shouldReturnBadRequest_whenNewPasswordEqualsCurrent(){
        User user = authTestHelper.createDefaultUser("senhaAtual1");

        String body = """
                      {
                        "currentPassword": "senhaAtual1",
                        "newPassword": "senhaAtual1",
                        "confirmNewPassword": "senhaAtual1"
                      }
                      """;

        given()
                .cookie(authTestHelper.authCookie(user))
                .contentType("application/json")
                .body(body)
        .when()
                .patch("/user/{username}/password", user.getUsername())
        .then()
                .statusCode(400)
                .body("operation", equalTo("Error.BadRequest"))
                .body("message", equalTo("A nova senha não pode ser igual à senha atual."));
    }
    // =========================== softDeleteUser ===========================
    @Test
    void softDeleteUser_shouldReturnOk_whenRequesterIsSelf(){
        User user = authTestHelper.createDefaultUser("senha12345");

        given()
                .cookie(authTestHelper.authCookie(user))
        .when()
                .delete("/user/{username}", user.getUsername())
        .then()
                .statusCode(200)
                .body("operation", equalTo("User.SoftDeleteUser"))
                .body("message", equalTo("Usuário deletado com sucesso."));
    }

    @Test
    void softDeleteUser_shouldReturnForbidden_whenRequesterIsNotSelfOrAdmin(){
        User target = authTestHelper.createDefaultUser("senha12345");
        User other = authTestHelper.createUser(
                "other_delete",
                "other_delete@test.com",
                "senha12345",
                Role.USER
        );

        given()
                .cookie(authTestHelper.authCookie(other))
        .when()
                .delete("/user/{username}", target.getUsername())
        .then()
                .statusCode(403)
                .body("operation", equalTo("Error.ForbiddenAction"))
                .body("message", equalTo("Ação não permitida. Apenas o próprio usuário ou um moderador pode realizar esta operação."));
    }

    //=========================== emailChange ===========================
    @Test
    void emailChange_shouldReturnOk_whenRequesterIsSelfAndVerified(){
        User user = authTestHelper.createDefaultUser("senha12345");

        String body = """
                      {
                        "newEmail": "novo_email@test.com"
                      }
                      """;

        given()
                .cookie(authTestHelper.authCookie(user))
                .contentType("application/json")
                .body(body)
        .when()
                .post("/user/{username}/email", user.getUsername())
        .then()
                .statusCode(200)
                .body("operation", equalTo("User.RequestEmailChange"))
                .body("message", equalTo("Solicitação de alteração de e-mail realizada com sucesso."));
    }

    @Test
    void emailChange_shouldReturnForbidden_whenUserIsNotVerified(){
        User user = authTestHelper.notVerifiedUser("not_verified", "senha12345");

        String body = """
                      {
                        "newEmail": "novo_email@test.com"
                      }
                      """;

        given()
                .cookie(authTestHelper.authCookie(user))
                .contentType("application/json")
                .body(body)
        .when()
                .post("/user/{username}/email", user.getUsername())
        .then()
                .statusCode(403)
                .body("operation", equalTo("Error.ForbiddenAction"))
                .body("message", equalTo("Ação não permitida. Apenas usuários com e-mails verificados podem realizar esta operação."));
    }

    @Test
    void emailChange_shouldReturnForbidden_whenRequesterIsNotSelf(){
        User target = authTestHelper.createDefaultUser("senha12345");
        User other = authTestHelper.createUser(
                "other_email",
                "other_email@test.com",
                "senha12345",
                Role.USER
        );

        String body = """
                      {
                        "newEmail": "novo_email@test.com"
                      }
                      """;

        given()
                .cookie(authTestHelper.authCookie(other))
                .contentType("application/json")
                .body(body)
        .when()
                .post("/user/{username}/email", target.getUsername())
        .then()
                .statusCode(403)
                .body("operation", equalTo("Error.ForbiddenAction"))
                .body("message", equalTo("Ação não permitida. Apenas o próprio usuário ou um moderador pode realizar esta operação."));
    }

    //=========================== modifyRole ===========================
    @Test
    void modifyRole_shouldReturnOk_whenSuperAdminPromotesUserToAdmin(){
        User target = authTestHelper.createDefaultUser("senha12345");

        User superAdmin = authTestHelper.createUser(
                "super_role",
                "super_role@test.com",
                "senha12345",
                Role.SUPER_ADMIN
        );

        given()
                .cookie(authTestHelper.authCookie(superAdmin))
                .queryParam("role", "ADMIN")
        .when()
                .put("/user/{username}/role", target.getUsername())
        .then()
                .statusCode(200)
                .body("operation", equalTo("User.ModifyRole"))
                .body("message", equalTo("Cargo do usuário alterado com sucesso."));
    }

    @Test
    void modifyRole_shouldReturnForbidden_whenAdminTriesToPromoteToAdmin(){
        User target = authTestHelper.createDefaultUser("senha12345");

        User admin = authTestHelper.createUser(
                "admin_role",
                "admin_role@test.com",
                "senha12345",
                Role.ADMIN
        );

        given()
                .cookie(authTestHelper.authCookie(admin))
                .queryParam("role", "ADMIN")
        .when()
                .put("/user/{username}/role", target.getUsername())
        .then()
                .statusCode(403)
                .body("operation", equalTo("Error.ForbiddenAction"))
                .body("message", equalTo("Ação não permitida. Apenas o próprio usuário ou um moderador pode realizar esta operação."));
    }

    @Test
    void modifyRole_shouldReturnForbidden_whenSuperAdminTriesToPromoteToSuperAdmin(){
        User target = authTestHelper.createDefaultUser("senha12345");
        User superAdmin = authTestHelper.createUser(
                "super_role_2",
                "super_role_2@test.com",
                "senha12345",
                Role.SUPER_ADMIN
        );

        given()
                .cookie(authTestHelper.authCookie(superAdmin))
                .queryParam("role", "SUPER_ADMIN")
        .when()
                .put("/user/{username}/role", target.getUsername())
        .then()
                .statusCode(403)
                .body("operation", equalTo("Error.ForbiddenAction"))
                .body("message", equalTo("Ação não permitida. Apenas o próprio usuário ou um moderador pode realizar esta operação."));
    }

    @Test
    void modifyRole_shouldReturnNotFound_whenRoleNameIsInvalid(){
        User target = authTestHelper.createDefaultUser("senha12345");
        User superAdmin = authTestHelper.createUser(
                "super_role_3",
                "super_role_3@test.com",
                "senha12345",
                Role.SUPER_ADMIN
        );

        given()
                .cookie(authTestHelper.authCookie(superAdmin))
                .queryParam("role", "NAO_EXISTE")
        .when()
                .put("/user/{username}/role", target.getUsername())
        .then()
                .statusCode(404)
                .body("operation", equalTo("Error.NotFound"))
                .body("message", equalTo("Cargo de usuário não encontrada. Verifique as configurações da conta."));
    }

}
