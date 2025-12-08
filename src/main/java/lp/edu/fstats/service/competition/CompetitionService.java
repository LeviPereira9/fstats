package lp.edu.fstats.service.competition;

import lp.edu.fstats.dto.competition.CompetitionResponse;
import lp.edu.fstats.model.competition.Competition;

public interface CompetitionService {

    CompetitionResponse getCompetition(String code);

    void saveCompetition(Competition competition);
}
