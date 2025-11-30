package lp.edu.fstats.service.match;

import lp.edu.fstats.model.match.Match;

import java.util.List;
import java.util.Map;

public interface MatchService {
    public Map<Integer, Match> findAllByExternalId(List<Integer> externalIds);
}
