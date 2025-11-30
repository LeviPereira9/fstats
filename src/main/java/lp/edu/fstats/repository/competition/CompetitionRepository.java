package lp.edu.fstats.repository.competition;

import lp.edu.fstats.model.competition.Competition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {

    Optional<Competition> findByExternalId(Long externalId);
}
