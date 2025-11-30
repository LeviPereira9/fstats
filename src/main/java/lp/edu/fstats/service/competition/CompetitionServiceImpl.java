package lp.edu.fstats.service.competition;

import lombok.RequiredArgsConstructor;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.repository.competition.CompetitionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompetitionServiceImpl implements CompetitionService {

    private final CompetitionRepository competitionRepository;

    @Override
    public Competition findByExternalId(Long externalId) {
        return null;
    }
}
