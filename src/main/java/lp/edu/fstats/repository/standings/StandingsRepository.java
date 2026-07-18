package lp.edu.fstats.repository.standings;

import lp.edu.fstats.model.standings.Standings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Year;
import java.util.List;

@Repository
public interface StandingsRepository extends JpaRepository<Standings, Long> {

    List<Standings> findAllByCompetition_Id(Long id);

}
