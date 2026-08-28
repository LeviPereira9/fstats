package lp.edu.fstats.repository.competition;

import lp.edu.fstats.model.competition.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompetitionRepository extends JpaRepository<Competition, Long> {


    @Query("""
    SELECT c
        FROM Competition c
        WHERE c.code.code = :code
          AND c.active = true
""")
    Optional<Competition> findByCode(String code);

    @Query("""
    SELECT c FROM Competition c WHERE c.code.code = :code AND c.status = 'Em andamento' AND c.active = true
""")
    Optional<Competition> findByCodeAndStatus(@Param("code") String code);

    boolean existsByExternalId(Long externalId);
}
