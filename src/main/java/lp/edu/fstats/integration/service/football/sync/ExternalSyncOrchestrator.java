package lp.edu.fstats.integration.service.football.sync;

import lp.edu.fstats.exception.custom.CustomNotFoundException;
import lp.edu.fstats.integration.service.football.sync.context.CompetitionSyncContext;
import lp.edu.fstats.integration.service.football.sync.context.StandingsSyncContext;
import lp.edu.fstats.integration.service.football.sync.context.TeamSyncContext;
import lp.edu.fstats.integration.service.football.sync.step.*;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.edu.fstats.model.code.Code;
import lp.edu.fstats.repository.code.CodeRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

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

    private final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();


    @Scheduled(cron = "0 0 2,10,18 * * *", zone = "America/Sao_Paulo")
    @Transactional
    @Async("competitionsThread")
    public void syncAll(){
        List<Code> codes = codeRepository.findAll();

        for(Code code : codes){

            if(!code.isActive()){
                continue;
            }

            try{
                this.sync(code);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Async("competitionThread")
    public void syncCompetition(Integer codeId){
        locks.putIfAbsent(codeId.toString(), new ReentrantLock());
        ReentrantLock lock = locks.get(codeId.toString());

        Code code = codeRepository.findById(codeId)
                .orElseThrow(CustomNotFoundException::competition);

        if(!lock.tryLock()){
            throw new RuntimeException("Lock acquired");
        }

        try{
            this.sync(code);
        } finally {
            lock.unlock();
        }


    }

    private void sync(Code code){

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
