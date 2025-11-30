package lp.edu.fstats.service.competition;

import lp.edu.fstats.model.competition.Competition;

public interface CompetitionService {
    Competition findByExternalId(Long externalId);
}
