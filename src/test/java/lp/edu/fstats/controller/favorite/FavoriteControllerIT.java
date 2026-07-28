package lp.edu.fstats.controller.favorite;

import lp.edu.fstats.base.IntegrationTestBase;
import lp.edu.fstats.dto.favorites.FavoriteAddRequest;
import lp.edu.fstats.dto.favorites.FavoriteRemoveRequest;
import lp.edu.fstats.model.code.Code;
import lp.edu.fstats.model.favorites.Favorite;
import lp.edu.fstats.model.user.Role;
import lp.edu.fstats.model.user.User;
import lp.edu.fstats.repository.code.CodeRepository;
import lp.edu.fstats.repository.favorites.FavoriteRepository;
import lp.edu.fstats.support.helper.AuthTestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class FavoriteControllerIT extends IntegrationTestBase {

    @Autowired
    private AuthTestHelper authTestHelper;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private CodeRepository codeRepository;

    //===================== helpers =====================

    public Favorite createFavorite(User user, Code code){
        Favorite favorite = new Favorite();

        favorite.setUser(user);
        favorite.setCompetition(code);

        return favoriteRepository.save(favorite);
    }

    public Code createCode(String code, String name){
        Code entity = new Code();
        entity.setCode(code);
        entity.setName(name);

        return codeRepository.save(entity);
    }

    //===================== getAllUserFavorites =====================
    @Test
    void getAllUserFavorites_shouldReturnForbiddenAction_whenNoCookieIsProvided(){
        given()
        .when()
                .get("/user/{username}/favorites","user_test" )
        .then()
                .statusCode(401)
                .body("operation", equalTo("Error.ForbiddenAction"));
    }

    @Test
    void getAllUserFavorites_shouldReturnForbidden_whenUserIsNotSelf(){
        User requester = authTestHelper.createDefaultUser("senha12345");

        User target = authTestHelper.createUser(
                "target_user",
                "target_user@test.com",
                "senha12345",
                Role.USER
        );

        given()
                .cookie(authTestHelper.authCookie(requester))
        .when()
                .get("/user/{username}/favorites", target.getUsername())
        .then()
                .statusCode(403)
                .body("operation", equalTo("Error.ForbiddenAction"));
    }

    @Test
    void getAllUserFavorites_shouldReturnForbidden_whenRequesterIsAdminButNotSelf(){
        User admin = authTestHelper.createUser(
                "admin_user",
                "admin_user@test.com",
                "senha12345",
                Role.ADMIN
        );

        User target = authTestHelper.createDefaultUser("senha12345");

        given()
                .cookie(authTestHelper.authCookie(admin))
        .when()
                .get("/user/{username}/favorites", target.getUsername())
        .then()
                .statusCode(403)
                .body("operation", equalTo("Error.ForbiddenAction"));
    }

    @Test
    void getAllUserFavorites_shouldReturnEmptyList_whenUserHasNoFavorites(){
        User user = authTestHelper.createDefaultUser("senha12345");

        given()
                .cookie(authTestHelper.authCookie(user))
        .when()
                .get("/user/{username}/favorites", user.getUsername())
        .then()
                .statusCode(200)
                .body("operation", equalTo("Favorites.GetAll"))
                .body("data.favorites.size()", equalTo(0));
    }

    @Test
    void getAllUserFavorites_shouldReturnFavoritesData_whenFavoritesExist(){
        User user = authTestHelper.createDefaultUser("senha12345");

        Code code = this.createCode("PL", "Premier League");
        this.createFavorite(user, code);

        given()
                .cookie(authTestHelper.authCookie(user))
        .when()
                .get("/user/{username}/favorites", user.getUsername())
        .then()
                .statusCode(200)
                .body("operation", equalTo("Favorites.GetAll"))
                .body("data.favorites.size()", equalTo(1));
    }

    @Test
    void getAllUserFavorites_shouldReturnTooManyRequests_whenRateLimitIsExceeded(){
        User user = authTestHelper.createDefaultUser("senha12345");

        for(int i = 0; i < 10; i++){
            given()
                    .cookie(authTestHelper.authCookie(user))
            .when()
                    .get("/user/{username}/favorites", user.getUsername())
            .then()
                    .statusCode(200);
        }

        given()
                .cookie(authTestHelper.authCookie(user))
        .when()
                .get("/user/{username}/favorites", user.getUsername())
        .then()
                .statusCode(429)
                .body("operation", equalTo("Error.RateLimitExceeded"));
    }

    //====================== addFavorite ===============
    @Test
    void addFavorite_shouldReturnForbiddenAction_whenNoCookieIsProvided(){
        given()
                .contentType("application/json")
                .body(new FavoriteAddRequest(1))
        .when()
                .put("/user/{username}/favorites", "user_test")
        .then()
                .statusCode(401)
                .body("operation", equalTo("Error.ForbiddenAction"));
    }

    @Test
    void addFavorite_shouldReturnForbidden_whenUserIsNotSelf(){
        User requester = authTestHelper.createDefaultUser("senha12345");

        User target = authTestHelper.createUser(
                "target_user",
                "target_suer@email.com",
                "senha12345",
                Role.USER
        );

        Code code = this.createCode("PL", "Premier League");

        given()
                .cookie(authTestHelper.authCookie(requester))
        .when()
                .put("/user/{username}/favorites", target.getUsername())
        .then()
                .statusCode(403)
                .body("operation", equalTo("Error.ForbiddenAction"));
    }

    @Test
    void addFavorite_shouldReturnNotFound_whenCompetitionCodeDoesNotExist(){
        User user = authTestHelper.createDefaultUser("senha12345");

        given()
                .cookie(authTestHelper.authCookie(user))
                .contentType("application/json")
                .body(new FavoriteAddRequest(999999))
        .when()
                .put("/user/{username}/favorites", user.getUsername())
        .then()
                .statusCode(404)
                .body("operation", equalTo("Error.NotFound"))
                .body("message", equalTo("Competição não encontrada. Solicite um novo envio."));
    }

    @Test
    void addFavorite_shouldReturnBadRequest_whenFavoriteAlreadyExists(){
        User user = authTestHelper.createDefaultUser("senha12345");

        Code code = this.createCode("PL", "Premier League");

        this.createFavorite(user, code);

        given()
                .cookie(authTestHelper.authCookie(user))
                .contentType("application/json")
                .body(new FavoriteAddRequest(code.getId()))
        .when()
                .put("/user/{username}/favorites", user.getUsername())
        .then()
                .statusCode(400)
                .body("operation", equalTo("Error.DuplicateField"))
                .body("fieldErrors.favorite", equalTo("Você já adicionou esta competição aos favoritos"));
    }

    @Test
    void addFavorite_shouldReturnCreated_whenDataIsValid(){
        User user = authTestHelper.createDefaultUser("senha12345");

        Code code = this.createCode("PL", "Premier League");

        given()
                .cookie(authTestHelper.authCookie(user))
                .contentType("application/json")
                .body(new FavoriteAddRequest(code.getId()))
        .when()
                .put("/user/{username}/favorites", user.getUsername())
        .then()
                .statusCode(201)
                .body("operation", equalTo("Favorites.Add"))
                .body("message", equalTo("Competição adicionada aos favoritos com sucesso."));
    }

    //================== DeleteFavorite =====================
    @Test
    void deleteFavorite_shouldReturnForbiddenAction_whenNoCookieIsProvided(){
        given()
                .contentType("application/json")
                .body(new FavoriteRemoveRequest(1L))
        .when()
                .delete("/user/{username}/favorites", "user_test")
        .then()
                .statusCode(401)
                .body("operation", equalTo("Error.ForbiddenAction"));
    }

    @Test
    void deleteFavorite_shouldReturnForbiddenAction_whenUserIsNotSelf(){
        User requester = authTestHelper.createDefaultUser("senha12345");

        User target = authTestHelper.createUser(
                "target_user",
                "target_user@test.com",
                "senha12345",
                Role.USER
        );

        Code code = this.createCode("PL", "Premier League");

        Favorite favorite = this.createFavorite(target, code);

        given()
                .cookie(authTestHelper.authCookie(requester))
                .contentType("application/json")
                .body(new FavoriteRemoveRequest(favorite.getId()))
        .when()
                .delete("/user/{username}/favorites", target.getUsername())
        .then()
                .statusCode(403)
                .body("operation", equalTo("Error.ForbiddenAction"));
    }

    @Test
    void deleteFavorite_shouldReturnNotFound_whenFavoriteDoesNotExist(){
        User user = authTestHelper.createDefaultUser("senha12345");

        given()
                .cookie(authTestHelper.authCookie(user))
                .contentType("application/json")
                .body(new FavoriteRemoveRequest(9999L))
        .when()
                .delete("/user/{username}/favorites", user.getUsername())
        .then()
                .statusCode(404)
                .body("operation", equalTo("Error.NotFound"))
                .body("message", equalTo("Favorito não encontrado."));
    }

    @Test
    void deleteFavorite_shouldReturnOk_whenFavoriteIsRemoved(){
        User user = authTestHelper.createDefaultUser("senha12345");

        Code code = this.createCode("PL", "Premier League");
        Favorite favorite = this.createFavorite(user, code);

        given()
                .cookie(authTestHelper.authCookie(user))
                .contentType("application/json")
                .body(new FavoriteRemoveRequest(favorite.getId()))
        .when()
                .delete("/user/{username}/favorites", user.getUsername())
        .then()
                .statusCode(200)
                .body("operation", equalTo("Favorites.Remove"))
                .body("message", equalTo("Competição removida dos favoritos com sucesso."));
    }

}
