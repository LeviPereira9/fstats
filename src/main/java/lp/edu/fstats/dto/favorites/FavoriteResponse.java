package lp.edu.fstats.dto.favorites;

import lp.edu.fstats.model.favorites.Favorite;

public record FavoriteResponse(
        Long favoriteId,
        String competitionCode,
        String competitionName
) {

    public FavoriteResponse (Favorite source){
        this(
                source.getId(),
                source.getCompetitionCode(),
                source.getCompetitionName());
    }

}
