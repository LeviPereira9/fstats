package lp.edu.fstats.integration.service.football.sync.step;

import lombok.RequiredArgsConstructor;
import lp.edu.fstats.integration.client.FootballApiClient;
import lp.edu.fstats.integration.dto.competition.CompetitionExternalResponse;
import lp.edu.fstats.model.code.Code;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.repository.competition.CompetitionRepository;
import lp.edu.fstats.service.competition.CompetitionService;
import lp.edu.fstats.integration.service.football.sync.context.CompetitionSyncContext;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CompetitionSyncStep {

    private final CompetitionRepository competitionRepository;
    private final CompetitionService competitionService;

    private final FootballApiClient footballApiClient;

    public CompetitionSyncContext sync(Code Icode, Year season){

        String code = Icode.getCode();

        CompetitionSyncContext context = new CompetitionSyncContext();

        Competition competition;

        Competition savedCompetition = competitionRepository.findByCodeAndStatus(code)
                .orElse(null);

        CompetitionExternalResponse externalCompetition = footballApiClient.getCurrentCompetition(code);

        if(savedCompetition == null){

            if(competitionRepository.existsByExternalId(externalCompetition.currentSeason().id())){
                return context;
            }

            competition = externalCompetition.toModel(Icode);

        } else {
            //mudou a season, novas caras.
            System.out.println(externalCompetition.currentSeason().id());
            System.out.println(savedCompetition.getExternalId());

            if(!Objects.equals(savedCompetition.getExternalId(), externalCompetition.currentSeason().id())){
                savedCompetition.setActive(false);
                savedCompetition.setStatus("Finalizada");
                competitionRepository.save(savedCompetition);

                competition = externalCompetition.toModel(Icode);
            } else {
                competition = externalCompetition.update(savedCompetition);
            }

        }

        competitionService.saveCompetition(competition);

        context.setCompetition(competition);

        return context;
    }

}
