package lp.edu.fstats.service.match;

import lombok.RequiredArgsConstructor;
import lp.edu.fstats.model.match.Match;
import lp.edu.fstats.repository.match.MatchRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;

    @Override
    public Map<Long, Match> findAllByExternalId(List<Long> externalIds) {
        List<Match> matches = matchRepository.findAllByExternalIdIn(externalIds);

        return matches.stream().collect(
                Collectors.toMap(Match::getExternalId, Function.identity()));
    }
}
