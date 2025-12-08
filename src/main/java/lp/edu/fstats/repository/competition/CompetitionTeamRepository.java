package lp.edu.fstats.repository.competition;

import lp.edu.fstats.model.competition.CompetitionTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompetitionTeamRepository extends JpaRepository<CompetitionTeam, Long> {

    List<CompetitionTeam> findAllByCompetitionId(Long competitionId);

}
