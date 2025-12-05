package lp.edu.fstats.service.standings;

import lp.edu.fstats.dto.standings.StandingsResponse;
import lp.edu.fstats.model.standings.Standings;

import java.time.Year;
import java.util.Map;

public interface StandingsService {
    Map<Long, Standings> findAllByCompetitionId(Long id);

    StandingsResponse getStandings(String code, Long competitionId);
}
