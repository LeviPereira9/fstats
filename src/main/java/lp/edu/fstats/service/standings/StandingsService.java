package lp.edu.fstats.service.standings;

import lp.edu.fstats.dto.standings.StandingsResponse;
import lp.edu.fstats.model.standings.Standings;

import java.util.List;
import java.util.Map;

public interface StandingsService {
    Map<Long, Standings> findAllByCompetitionId(Long id);

    StandingsResponse getStandings(Long competitionId);

    void saveAll(List<Standings> standingsToSave);
}
