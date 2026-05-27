package lp.edu.fstats.service.favorites;

import lombok.RequiredArgsConstructor;
import lp.edu.fstats.dto.favorites.FavoriteAddRequest;
import lp.edu.fstats.dto.favorites.FavoriteRemoveRequest;
import lp.edu.fstats.dto.favorites.FavoritesResponse;
import lp.edu.fstats.exception.custom.CustomDuplicateFieldException;
import lp.edu.fstats.exception.custom.CustomForbiddenActionException;
import lp.edu.fstats.exception.custom.CustomNotFoundException;
import lp.edu.fstats.model.code.Code;
import lp.edu.fstats.model.favorites.Favorite;
import lp.edu.fstats.model.user.User;
import lp.edu.fstats.repository.code.CodeRepository;
import lp.edu.fstats.repository.competition.CompetitionRepository;
import lp.edu.fstats.repository.favorites.FavoriteRepository;
import lp.edu.fstats.repository.user.UserRepository;
import lp.edu.fstats.util.AuthUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final CodeRepository codeRepository;

    @Override
    public FavoritesResponse getAllFavorites(String username) {
        if(!AuthUtil.isSelfRequest(username)) throw CustomForbiddenActionException.notAuthorized();

        List<Favorite> favorites = favoriteRepository.findAllByUser_Username(username);

        return FavoritesResponse.getResponse(favorites);
    }

    @Override
    public void addFavorite(String username, FavoriteAddRequest request) {

        if(!AuthUtil.isSelfRequest(username)) throw CustomForbiddenActionException.notAuthorized();

        User user = userRepository.findByUsername(username)
                .orElseThrow(CustomNotFoundException::user);

        Code code = codeRepository.findById(request.competitionId())
                .orElseThrow(CustomNotFoundException::competition);

        boolean alreadyInFavorites = favoriteRepository.existsByCompetition_IdAndUser_Username(
                request.competitionId(),
                username);

        if(alreadyInFavorites) throw CustomDuplicateFieldException.favorite();

        Favorite favorite = request.toModel(code, user);

        favoriteRepository.save(favorite);
    }

    @Override
    public void removeFavorite(String username, FavoriteRemoveRequest request) {
        if(!AuthUtil.isSelfRequest(username)) throw CustomForbiddenActionException.notAuthorized();

        boolean favoriteExists = favoriteRepository.existsById(request.favoriteId());

        if(!favoriteExists) throw CustomNotFoundException.favorite();

        favoriteRepository.deleteById(request.favoriteId());
    }
}
