package lp.edu.fstats.repository.favorites;

import lp.edu.fstats.model.favorites.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findAllByUser_Username(String username);

    boolean existsByCompetition_IdAndUser_Username(Integer id, String username);
}
