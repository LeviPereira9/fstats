package lp.edu.fstats.service.competition;

import lombok.RequiredArgsConstructor;
import lp.edu.fstats.dto.competition.CompetitionResponse;
import lp.edu.fstats.exception.custom.CustomNotFoundException;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.repository.competition.CompetitionRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompetitionServiceImpl implements CompetitionService {

    private final CompetitionRepository competitionRepository;

    @Cacheable(value = "competition", key = "'code:'+#code")
    @Override
    public CompetitionResponse getCompetition(String code) {
        Competition competition = competitionRepository.findByCode(code)
                .orElseThrow(CustomNotFoundException::competition);

        return new CompetitionResponse(competition);
    }

    @Override
    @CacheEvict(value = "competition", allEntries = true)
    public Competition saveCompetition(Competition competition) {

        competition = competitionRepository.save(competition);

        return competition;
    }
}
