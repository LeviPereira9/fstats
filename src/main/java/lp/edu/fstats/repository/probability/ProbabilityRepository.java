package lp.edu.fstats.repository.probability;

import lp.edu.fstats.model.probability.Probability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProbabilityRepository extends JpaRepository<Probability, Long> {
    @Query("""
    SELECT MAX(p.matchDay) FROM Probability p
        WHERE p.competition.id = :competitionId
""")
    Integer findMaxMatchday(@Param("competitionId") Long competitionId);
}
