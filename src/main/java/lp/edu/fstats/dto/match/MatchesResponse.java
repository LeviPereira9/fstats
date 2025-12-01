package lp.edu.fstats.dto.match;

import lp.edu.fstats.model.match.Match;

import java.util.List;

public record MatchesResponse(
        List<MatchResponse> matches
) {
}
