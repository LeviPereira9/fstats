package lp.edu.fstats.repository.match;

import lp.edu.fstats.model.match.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findAllByExternalIdIn(List<Long> externalIds);

    @Query("""
    SELECT m FROM Match m WHERE m.competition.code = :code AND m.matchDay = :matchDay
""")
    List<Match> findAllByCompetitionAndMatchday(@Param("code") String code,@Param("matchDay") Integer matchDay);
}
