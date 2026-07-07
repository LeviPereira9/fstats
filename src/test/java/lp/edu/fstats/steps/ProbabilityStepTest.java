package lp.edu.fstats.steps;

import lp.edu.fstats.factory.entity.TeamTestFactory;
import lp.edu.fstats.model.probability.Probability;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import lp.edu.fstats.dto.probability.PoissonProbabilityData;
import lp.edu.fstats.factory.context.SyncContextTestFactory;
import lp.edu.fstats.factory.entity.CompetitionTestFactory;
import lp.edu.fstats.factory.entity.MatchTestFactory;
import lp.edu.fstats.integration.service.football.sync.context.CompetitionSyncContext;
import lp.edu.fstats.integration.service.football.sync.step.ProbabilityStep;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.model.match.Match;
import lp.edu.fstats.model.team.Team;
import lp.edu.fstats.repository.match.MatchRepository;
import lp.edu.fstats.repository.probability.ProbabilityRepository;
import lp.edu.fstats.service.poisson.PoissonService;
import lp.edu.fstats.service.probability.ProbabilityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ProbabilityStepTest {

    @Mock
    private ProbabilityService probabilityService;

    @Mock
    private ProbabilityRepository probabilityRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private PoissonService poissonService;

    @InjectMocks
    private ProbabilityStep probabilityStep;

    //helpers
    private Competition buildCompetition(int apiCurrentMatchDay){
        Competition competition = CompetitionTestFactory.buildCompetition("PL");

        competition.setApiCurrentMatchDay(apiCurrentMatchDay);

        return competition;
    }

    private Match buildMatch(
            Long id,
            Team home,
            Team away,
            Integer matchDay,
            String status,
            Integer homeGoals,
            Integer awayGoals){

        Match match = MatchTestFactory.buildMatch(
                id,
                id * 100,
                home,
                away,
                matchDay,
                this.buildCompetition(matchDay)
        );

        match.setStatus(status);
        match.setHomeGoals(homeGoals);
        match.setAwayGoals(awayGoals);

        return match;
    }

    private PoissonProbabilityData buildPoissonData(){
        return new PoissonProbabilityData(
                BigDecimal.valueOf(0.80),
                BigDecimal.valueOf(0.60),
                BigDecimal.valueOf(0.30)
        );
    }

    // cenário: menos de 3 rodadas - retorna imediatamente

    @Test
    void sync_shouldDoNothing_whenApiCurrentMatchDayIsLessThanThree(){

        Competition competition = this.buildCompetition(2);
        CompetitionSyncContext csc = SyncContextTestFactory.buildCsc(competition);

        probabilityStep.sync(csc);

        verifyNoInteractions(probabilityRepository);
        verifyNoInteractions(matchRepository);
        verifyNoInteractions(poissonService);
        verifyNoInteractions(probabilityService);

    }

    // cenário: já calculou até a rodada atual
    @Test
    void sync_shouldDoNothing_whenAllMatchDaysAlreadyCalculated(){

        Competition competition = this.buildCompetition(5);
        CompetitionSyncContext csc = SyncContextTestFactory.buildCsc(competition);

        //maxMatchday == apiCurrentMatchDay -> startCount = 6 > 5

        when(probabilityRepository.findMaxMatchday(1L))
                .thenReturn(5);

        probabilityStep.sync(csc);

        verifyNoInteractions(matchRepository);
        verifyNoInteractions(poissonService);
        verifyNoInteractions(probabilityService);

    }

    // cenário: calcula a primeira rodada, sem histórico
    @Test
    void sync_shouldCalculateAndSaveProbabilities_whenNoMaxMatchDayExists(){

        Competition competition = this.buildCompetition(3);
        CompetitionSyncContext csc = SyncContextTestFactory.buildCsc(competition);

        Team arsenal = TeamTestFactory.buildTeam(1L, 100L, "Arsenal");
        Team chelsea = TeamTestFactory.buildTeam(2L, 200L, "Chelsea");

        // rodadas 1 e 2 finalizada, rodada 3 é a alvo.

        Match r1 = this.buildMatch(1L, arsenal, chelsea, 1, "FINISHED", 2, 1);
        Match r2 = this.buildMatch(2L, chelsea, arsenal, 2, "FINISHED", 1, 1);
        Match r3 = this.buildMatch(3L, arsenal, chelsea, 3, "SCHEDULED", 0, 0);

        when(probabilityRepository.findMaxMatchday(1L)).thenReturn(null);

        when(matchRepository.findAllByCompetition_Id(1L))
                .thenReturn(List.of(r1, r2, r3));

        when(poissonService.calculate(any(BigDecimal.class)))
                .thenReturn(this.buildPoissonData());

        probabilityStep.sync(csc);

        // startCount = 3 == apiCurrentMatchDay = 3 -> n entra na recursão
        verify(probabilityService, times(1)).saveAll(anyList());

        ArgumentCaptor<List<Probability>> captor = ArgumentCaptor.forClass(List.class);

        verify(probabilityService).saveAll(captor.capture());

        List<Probability> saved = captor.getValue();

        assertEquals(1, saved.size());
        assertEquals(r3, saved.get(0).getMatch());
        assertEquals(3, saved.get(0).getMatchDay());

        assertEquals(BigDecimal.valueOf(0.80), saved.get(0).getProbabilityOver05());
        assertEquals(BigDecimal.valueOf(0.60), saved.get(0).getProbabilityOver15());
        assertEquals(BigDecimal.valueOf(0.30), saved.get(0).getProbabilityOver25());

    }

    // cenário: calcula duas rodadas (recursão)

    @Test
    void sync_shouldCalculateTwoMatchDays_whenTwoMatchDaysNeedProcessing(){

        Competition competition = this.buildCompetition(4);
        CompetitionSyncContext csc = SyncContextTestFactory.buildCsc(competition);

        Team arsenal = TeamTestFactory.buildTeam(1L, 100L, "Arsenal");
        Team chelsea = TeamTestFactory.buildTeam(2L, 200L, "Chelsea");


        Match r1 = this.buildMatch(1L, arsenal, chelsea, 1, "FINISHED", 2, 1);
        Match r2 = this.buildMatch(2L, chelsea, arsenal, 2, "FINISHED", 1, 1);
        Match r3 = this.buildMatch(3L, arsenal, chelsea, 3, "SCHEDULED", 0, 0);
        Match r4 = this.buildMatch(4L, arsenal, chelsea, 4, "SCHEDULED", 0, 0);

        // nenhuma probabilidade calculada ainda - começa da r3

        when(probabilityRepository.findMaxMatchday(1L))
                .thenReturn(null);

        when(matchRepository.findAllByCompetition_Id(1L))
                .thenReturn(List.of(r1, r2, r3, r4));

        when(poissonService.calculate(any(BigDecimal.class)))
                .thenReturn(this.buildPoissonData());

        probabilityStep.sync(csc);

        // duas rodadas calculadas -> salveAll chamado duas vezes (uma por nível de recursão)
        verify(probabilityService, times(2)).saveAll(anyList());

        //poissonService chamado uma vez por partida (r3 e r4)

        verify(poissonService, times(2)).calculate(any(BigDecimal.class));
    }

    // cenário: partida não finalizada é ignorada no cálculo do lambda

    @Test
    void sync_shouldIgnoreUnfinishedMatches_whenCalculatingLambda(){
        Competition competition = this.buildCompetition(3);
        CompetitionSyncContext csc = SyncContextTestFactory.buildCsc(competition);

        Team arsenal = TeamTestFactory.buildTeam(1L, 100L, "Arsenal");
        Team chelsea = TeamTestFactory.buildTeam(2L, 200L, "Chelsea");

        // rodada 1 não finalizada - não contribui pro cálculo do lambda.

        Match r1Unfinished = this.buildMatch(1L, arsenal, chelsea, 1, "SCHEDULED", 0, 0);
        Match r2 = this.buildMatch(2L, chelsea, arsenal, 2, "FINISHED", 1, 1);
        Match r3 = this.buildMatch(3L, arsenal, chelsea, 3, "SCHEDULED", 0, 0);

        when(probabilityRepository.findMaxMatchday(1L))
                .thenReturn(null);

        when(matchRepository.findAllByCompetition_Id(1L))
                .thenReturn(List.of(r1Unfinished, r2, r3));

        when(poissonService.calculate(any(BigDecimal.class)))
                .thenReturn(this.buildPoissonData());

        probabilityStep.sync(csc);

        //probabilidade ainda é calculada para r3, mas o 0 lambda usa só o r2 (único finalizado)
        verify(poissonService, times(1)).calculate(any(BigDecimal.class));

        verify(probabilityService, times(1)).saveAll(anyList());
    }
}
