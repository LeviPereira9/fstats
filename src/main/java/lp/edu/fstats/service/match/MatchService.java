package lp.edu.fstats.service.match;

import lp.edu.fstats.dto.match.MatchesResponse;
import lp.edu.fstats.model.match.Match;

import java.util.List;
import java.util.Map;

public interface MatchService {
    Map<Long, Match> findAllByExternalId(List<Long> externalIds);

    MatchesResponse getMatches(Long competitionId, Integer matchday);
}
