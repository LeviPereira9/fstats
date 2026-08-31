package lp.edu.fstats.controller.auto;


import lp.edu.fstats.base.IntegrationTestBase;
import lp.edu.fstats.integration.service.football.sync.ExternalSyncOrchestrator;
import lp.edu.fstats.model.user.Role;
import lp.edu.fstats.model.user.User;
import lp.edu.fstats.support.helper.AuthTestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.verify;
import static org.hamcrest.Matchers.*;

public class AutoControllerIT extends IntegrationTestBase {

    @Autowired
    private AuthTestHelper authTestHelper;

    @MockitoBean
    private ExternalSyncOrchestrator externalSyncOrchestrator;

    // startSync
    @Test
    void startSync_shouldReturnOkAndDelegateToOrchestrator_whenUserIsSuperAdmin(){

        User superAdmin = authTestHelper.createUser(
                "super_admin",
                "super@test.com",
                "senha12345", Role.SUPER_ADMIN
        );

        given()
                .cookie(authTestHelper.authCookie(superAdmin))
        .when()
                .post("/auto/1")
        .then()
                .statusCode(200);

        verify(externalSyncOrchestrator).syncCompetition(1);
    }

    @Test
    void startSync_shouldReturnForbiddenAction_whenUserIsAdminButNotSuperAdmin(){
        User admin = authTestHelper.createUser(
                "admin_user",
                "admin@test.com",
                "senha12345",
                Role.ADMIN
        );

        given()
                .cookie(authTestHelper.authCookie(admin))
        .when()
                .post("/auto/PL")
        .then()
                .statusCode(403)
                .body("operation", equalTo("Error.ForbiddenAction"))
                .body("message", equalTo("Você não têm permissão para acessar este recurso."));
    }

    @Test
    void startSync_shouldReturnUnauthorized_whenNoCookieIsProvided(){
        given()
        .when()
                .post("/auto/PL")
        .then()
                .statusCode(401);
    }

    //startSyncs
    @Test
    void startSyncs_shouldReturnOkAndDelegateToOrchestrator_whenUserIsSuperAdmin(){
        User superAdmin = authTestHelper.createUser(
                "super_admin_2",
                "super2@test.com",
                "senha12345",
                Role.SUPER_ADMIN
        );

        given()
                .cookie(authTestHelper.authCookie(superAdmin))
        .when()
                .post("/auto")
        .then()
                .statusCode(200);

        verify(externalSyncOrchestrator).syncAll();
    }

}
