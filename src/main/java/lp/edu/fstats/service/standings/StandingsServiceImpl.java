package lp.edu.fstats.service.standings;

import lombok.RequiredArgsConstructor;
import lp.edu.fstats.dto.standings.StandingsResponse;
import lp.edu.fstats.exception.custom.CustomNotFoundException;
import lp.edu.fstats.model.standings.Standings;
import lp.edu.fstats.repository.standings.StandingsRepository;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StandingsServiceImpl implements StandingsService {
    private final StandingsRepository standingsRepository;

    @Override
    public Map<Long, Standings> findAllByCompetitionId(Long competitionId) {
        List<Standings> standings = standingsRepository.findAllByCompetition_Id(competitionId);

        return standings.stream().collect(Collectors.toMap(Standings::getTeamExternalId, Function.identity()));
    }

    @Override
    public StandingsResponse getStandings(Long competitionId) {
        List<Standings> standings = standingsRepository.findAllByCompetition_Id(competitionId);

        if(standings.isEmpty()){
            throw CustomNotFoundException.standings();
        }

        return StandingsResponse.toResponse(standings);
    }
}
