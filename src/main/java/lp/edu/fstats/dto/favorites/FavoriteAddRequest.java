package lp.edu.fstats.dto.favorites;

import jakarta.validation.constraints.NotNull;
import lp.edu.fstats.model.code.Code;
import lp.edu.fstats.model.favorites.Favorite;
import lp.edu.fstats.model.user.User;

public record FavoriteAddRequest(
        @NotNull
        Integer codeId
) {

    public Favorite toModel(Code competition, User user){
        Favorite favorite = new Favorite();

        favorite.setCompetition(competition);
        favorite.setUser(user);

        return favorite;
    }

}
