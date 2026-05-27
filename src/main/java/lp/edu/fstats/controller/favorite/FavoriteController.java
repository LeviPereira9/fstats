package lp.edu.fstats.controller.favorite;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lp.edu.fstats.config.redis.ratelimit.snippets.RateLimit;
import lp.edu.fstats.dto.code.CodesResponse;
import lp.edu.fstats.dto.favorites.FavoriteAddRequest;
import lp.edu.fstats.dto.favorites.FavoriteRemoveRequest;
import lp.edu.fstats.dto.favorites.FavoritesResponse;
import lp.edu.fstats.response.normal.Response;
import lp.edu.fstats.service.favorites.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Favoritos",
        description = "Endpoints para gerenciamento da coleção de campeonatos favoritos."
)
@RestController
@RequestMapping("/${api.prefix}/user/{username}/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    @RateLimit
    @GetMapping
    public ResponseEntity<Response<FavoritesResponse>> getAllUserFavorites(@PathVariable("username") String username) {

        FavoritesResponse data = favoriteService.getAllFavorites(username);

        Response<FavoritesResponse> response = Response.<FavoritesResponse>builder()
                .operation("a")
                .code(HttpStatus.OK.value())
                .data(data)
                .message("Competições favoritas encontradas com sucesso.")
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<Response<Void>> addFavorite(
            @PathVariable("username") String username,
            @RequestBody FavoriteAddRequest request
            ){

        favoriteService.addFavorite(username, request);
        int code = HttpStatus.CREATED.value();

        Response<Void> response = Response.<Void>builder()
                .operation("a")
                .code(code)
                .message("Competição adicionada aos favoritos com sucesso.")
                .build();

        return ResponseEntity.status(code).body(response);
    }

    @DeleteMapping
    public ResponseEntity<Response<Void>> deleteFavorite(
            @PathVariable("username") String username,
            @RequestBody FavoriteRemoveRequest request
    ){

        favoriteService.removeFavorite(username, request);

        Response<Void> response = Response.<Void>builder()
                .operation("a")
                .code(HttpStatus.OK.value())
                .message("Competição removida dos favoritos com sucesso.")
                .build();

        return ResponseEntity.ok(response);
    }
}
