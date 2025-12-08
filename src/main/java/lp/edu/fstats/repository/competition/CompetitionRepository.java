package lp.edu.fstats.repository.competition;

import lp.edu.fstats.model.competition.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {


    @Query("""
    SELECT c
        FROM Competition c
        WHERE c.code = :code
          AND c.startDate = (
              SELECT MAX(c2.startDate)
              FROM Competition c2
              WHERE c2.code = :code
          )
""")
    Optional<Competition> findByCode(String code);

    @Query("""
    SELECT c FROM Competition c WHERE c.code = :code AND c.status = 'Em Andamento'
""")
    Optional<Competition> findByCodeAndStatus(@Param("code") String code);

    boolean existsByExternalId(Long externalId);
}
