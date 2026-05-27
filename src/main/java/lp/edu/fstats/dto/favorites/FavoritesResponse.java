package lp.edu.fstats.dto.favorites;

import lp.edu.fstats.model.favorites.Favorite;

import java.util.List;

public record FavoritesResponse(
        List<FavoriteResponse> favorites
) {
    public static FavoritesResponse getResponse(List<Favorite> source) {
        return new FavoritesResponse(
                source.stream()
                        .map(FavoriteResponse::new)
                        .toList()
        );
    }
}

