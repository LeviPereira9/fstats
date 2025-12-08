package lp.edu.fstats.service.competition;

import lombok.RequiredArgsConstructor;
import lp.edu.fstats.dto.competition.CompetitionResponse;
import lp.edu.fstats.exception.custom.CustomNotFoundException;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.repository.competition.CompetitionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompetitionServiceImpl implements CompetitionService {

    private final CompetitionRepository competitionRepository;

    @Override
    public CompetitionResponse getCompetition(String code) {
        Competition competition = competitionRepository.findByCode(code)
                .orElseThrow(CustomNotFoundException::competition);

        return new CompetitionResponse(competition);
    }

    @Override
    public void saveCompetition(Competition competition) {
        competition = competitionRepository.save(competition);
    }
}
