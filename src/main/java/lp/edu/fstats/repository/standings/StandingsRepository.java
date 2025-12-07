package lp.edu.fstats.repository.standings;

import lp.edu.fstats.model.standings.Standings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Year;
import java.util.List;

public interface StandingsRepository extends JpaRepository<Standings, Long> {

    List<Standings> findAllByCompetition_Id(Long id);

}
