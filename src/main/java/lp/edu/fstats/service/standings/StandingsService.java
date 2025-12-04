package lp.edu.fstats.service.standings;

import lp.edu.fstats.model.standings.Standings;

import java.util.Map;

public interface StandingsService {
    Map<Long, Standings> findAllByCompetitionId(Long id);
}
