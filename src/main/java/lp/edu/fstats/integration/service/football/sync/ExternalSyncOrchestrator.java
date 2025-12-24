package lp.edu.fstats.integration.service.football.sync;

import lp.edu.fstats.integration.service.football.sync.context.CompetitionSyncContext;
import lp.edu.fstats.integration.service.football.sync.context.StandingsSyncContext;
import lp.edu.fstats.integration.service.football.sync.context.TeamSyncContext;
import lp.edu.fstats.integration.service.football.sync.step.*;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.edu.fstats.model.code.Code;
import lp.edu.fstats.repository.code.CodeRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class ExternalSyncOrchestrator {

    private final CodeRepository codeRepository;

    private final CompetitionSyncStep competitionStep;
    private final TeamSyncStep teamSyncStep;
    private final MatchSyncStep matchSyncStep;
    private final StandingsSyncStep standingsSyncStep;
    private final AveragesStep averagesStep;
    private final ProbabilityStep probabilityStep;


    @Scheduled(cron = "0 0 2,10,18 * * *", zone = "America/Sao_Paulo")
    @Transactional
    public void syncAll(){
        List<Code> codes = codeRepository.findAll();

        for(Code code : codes){
            try{
                this.sync(code.getCode());
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void sync(String code){

        Year season = Year.now();

        CompetitionSyncContext csc = competitionStep.sync(code, season);

        if(!csc.hasActiveCompetition()){
            return;
        }

        TeamSyncContext tsc = teamSyncStep.sync(csc);
        matchSyncStep.sync(csc, tsc);

        StandingsSyncContext ssc = standingsSyncStep.sync(csc, tsc);
        averagesStep.sync(csc, tsc, ssc);
        probabilityStep.sync(csc);
    }

}
