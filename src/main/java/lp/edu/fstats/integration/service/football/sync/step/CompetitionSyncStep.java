package lp.edu.fstats.integration.service.football.sync.step;

import lombok.RequiredArgsConstructor;
import lp.edu.fstats.integration.client.FootballApiClient;
import lp.edu.fstats.integration.dto.competition.CompetitionExternalResponse;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.repository.competition.CompetitionRepository;
import lp.edu.fstats.service.competition.CompetitionService;
import lp.edu.fstats.integration.service.football.sync.context.CompetitionSyncContext;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class CompetitionSyncStep {

    private final CompetitionRepository competitionRepository;
    private final CompetitionService competitionService;

    private final FootballApiClient footballApiClient;

    public CompetitionSyncContext sync(String code, Year season){

        CompetitionSyncContext context = new CompetitionSyncContext(season);

        Competition competition;

        Competition savedCompetition = competitionRepository.findByCodeAndStatus(code)
                .orElse(null);

        CompetitionExternalResponse externalCompetition = footballApiClient.getCurrentCompetition(code);

        if(savedCompetition == null){
            boolean isFinished = competitionRepository.existsByExternalId(externalCompetition.id());

            if(isFinished){
                competition = null;
            } else {
                competition = externalCompetition.toModel();
            }

        } else {
            competition = externalCompetition.update(savedCompetition);
        }

        if(competition != null){
            competitionService.saveCompetition(competition);
        }

        context.setCompetition(competition);

        return context;
    }

}
