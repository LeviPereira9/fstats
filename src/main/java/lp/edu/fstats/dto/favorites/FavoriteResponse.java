package lp.edu.fstats.dto.favorites;

import lp.edu.fstats.model.favorites.Favorite;

public record FavoriteResponse(
        Long favoriteId,
        Integer codeId,
        String code,
        String codeName
) {

    public FavoriteResponse (Favorite source){
        this(
                source.getId(),
                source.getCompetitionId(),
                source.getCompetitionCode(),
                source.getCompetitionName());
    }

}
