package lp.edu.fstats.dto.favorites;

import lp.edu.fstats.model.code.Code;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.model.favorites.Favorite;
import lp.edu.fstats.model.user.User;

public record FavoriteAddRequest(
        Integer competitionId
) {

    public Favorite toModel(Code competition, User user){
        Favorite favorite = new Favorite();

        favorite.setCompetition(competition);
        favorite.setUser(user);

        return favorite;
    }

}
