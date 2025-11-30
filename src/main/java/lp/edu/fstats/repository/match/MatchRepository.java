package lp.edu.fstats.repository.match;

import lp.edu.fstats.model.match.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findAllByExternalIdIn(List<Integer> externalIds);

}
