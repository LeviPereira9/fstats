package lp.edu.fstats.service.favorites;

import lp.edu.fstats.dto.favorites.FavoriteResponse;
import lp.edu.fstats.dto.favorites.FavoriteAddRequest;
import lp.edu.fstats.dto.favorites.FavoriteRemoveRequest;
import lp.edu.fstats.dto.favorites.FavoritesResponse;

public interface FavoriteService {

    FavoritesResponse getAllFavorites(String username);

    void addFavorite(String username, FavoriteAddRequest request);

    void removeFavorite(String username, FavoriteRemoveRequest request);

}
