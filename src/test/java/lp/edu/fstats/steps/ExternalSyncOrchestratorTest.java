package lp.edu.fstats.steps;

import lp.edu.fstats.factory.context.SyncContextTestFactory;
import lp.edu.fstats.factory.entity.CompetitionTestFactory;
import lp.edu.fstats.integration.service.football.sync.ExternalSyncOrchestrator;
import lp.edu.fstats.integration.service.football.sync.context.CompetitionSyncContext;
import lp.edu.fstats.integration.service.football.sync.context.StandingsSyncContext;
import lp.edu.fstats.integration.service.football.sync.context.TeamSyncContext;
import lp.edu.fstats.integration.service.football.sync.step.*;
import lp.edu.fstats.model.code.Code;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.repository.code.CodeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.management.RuntimeErrorException;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExternalSyncOrchestratorTest {

    @Mock
    private CodeRepository codeRepository;

    @Mock
    private CompetitionSyncStep competitionSyncStep;

    @Mock
    private TeamSyncStep teamSyncStep;

    @Mock
    private MatchSyncStep matchSyncStep;

    @Mock
    private StandingsSyncStep standingsSyncStep;

    @Mock
    private AveragesStep averagesStep;

    @Mock
    private ProbabilityStep probabilityStep;

    @InjectMocks
    private ExternalSyncOrchestrator externalSyncOrchestrator;

    //Helpers
    private CompetitionSyncContext buildInactiveCsc(){
        return new CompetitionSyncContext();
    }

    private Code buildCode(String code){
        Code c = new Code();
        c.setId(1);
        c.setCode(code);
        c.setName("Premier League");

        return c;
    }


    //syncAll
    @Test
    void syncAll_shouldSyncAllCodes_whenCodesExist(){
        Code pl = this.buildCode("PL");
        Code bl1 = this.buildCode("BL1");

        Competition cPl = CompetitionTestFactory.buildCompetition("PL");

        Competition cBl1 = CompetitionTestFactory.buildCompetition("BL1");

        CompetitionSyncContext plCsc = SyncContextTestFactory.buildCsc(cPl);

        CompetitionSyncContext bl1Csc = SyncContextTestFactory.buildCsc(cBl1);

        TeamSyncContext tsc = SyncContextTestFactory.buildTsc(List.of());
        StandingsSyncContext ssc = SyncContextTestFactory.buildSsc(List.of(), List.of());

        when(codeRepository.findAll())
                .thenReturn(List.of(pl, bl1));

        when(competitionSyncStep.sync(eq("PL"), any(Year.class)))
                .thenReturn(plCsc);

        when(competitionSyncStep.sync(eq("BL1"), any(Year.class)))
                .thenReturn(bl1Csc);

        when(teamSyncStep.sync(any(CompetitionSyncContext.class)))
                .thenReturn(tsc);

        when(standingsSyncStep.sync(any(CompetitionSyncContext.class), any(TeamSyncContext.class)))
                .thenReturn(ssc);

        externalSyncOrchestrator.syncAll();

        verify(competitionSyncStep, times(2)).sync(any(String.class), any(Year.class));

        verify(teamSyncStep, times(2)).sync(any(CompetitionSyncContext.class));

        verify(matchSyncStep, times(2)).sync(any(CompetitionSyncContext.class), any(TeamSyncContext.class));

        verify(standingsSyncStep, times(2)).sync(any(CompetitionSyncContext.class), any(TeamSyncContext.class));

        verify(averagesStep, times(2)).sync(any(), any(), any());

        verify(probabilityStep, times(2)).sync(any(CompetitionSyncContext.class));

    }

    @Test
    void syncAll_shouldDoNothing_whenNoCodesExist(){

        when(codeRepository.findAll()).thenReturn(List.of());

        externalSyncOrchestrator.syncAll();

        verifyNoInteractions(competitionSyncStep);
        verifyNoInteractions(teamSyncStep);
        verifyNoInteractions(matchSyncStep);
        verifyNoInteractions(standingsSyncStep);
        verifyNoInteractions(averagesStep);
        verifyNoInteractions(probabilityStep);

    }

    @Test
    void syncAll_shouldContinueWithOtherCodes_whenOneCodeThrowsException(){

        Code pl = this.buildCode("PL");
        Code bl1 = this.buildCode("BL1");

        Competition cBl1 = CompetitionTestFactory.buildCompetition("BL1");

        CompetitionSyncContext bl1Csc = SyncContextTestFactory.buildCsc(cBl1);

        TeamSyncContext tsc = SyncContextTestFactory.buildTsc(List.of());
        StandingsSyncContext ssc = SyncContextTestFactory.buildSsc(List.of(), List.of());

        when(codeRepository.findAll())
                .thenReturn(List.of(pl, bl1));

        when(competitionSyncStep.sync(eq("PL"), any(Year.class)))
                .thenThrow(new RuntimeException("API indisponível"));

        when(competitionSyncStep.sync(eq("BL1"), any(Year.class)))
                .thenReturn(bl1Csc);

        when(teamSyncStep.sync(any(CompetitionSyncContext.class)))
                .thenReturn(tsc);

        when(standingsSyncStep.sync(any(CompetitionSyncContext.class), any(TeamSyncContext.class)))
                .thenReturn(ssc);

        externalSyncOrchestrator.syncAll();

        //bl1 foi normal, pl1 deu throw
        verify(competitionSyncStep, times(2)).sync(any(String.class), any(Year.class));
        verify(teamSyncStep, times(1)).sync(any(CompetitionSyncContext.class));

    }

    // sync (via syncCompetition)
    @Test
    void syncCompetition_shouldExecuteAllSteps_whenCompetitionIsActive(){

        Competition competition = CompetitionTestFactory.buildCompetition("PL");

        CompetitionSyncContext csc = SyncContextTestFactory.buildCsc(competition);

        TeamSyncContext tsc = SyncContextTestFactory.buildTsc(List.of());

        StandingsSyncContext ssc = SyncContextTestFactory.buildSsc(List.of(), List.of());

        when(competitionSyncStep.sync(eq("PL"), any(Year.class)))
                .thenReturn(csc);

        when(teamSyncStep.sync(csc))
                .thenReturn(tsc);

        when(standingsSyncStep.sync(csc, tsc))
                .thenReturn(ssc);

        externalSyncOrchestrator.syncCompetition("PL");

        InOrder inOrder = inOrder(
                competitionSyncStep,
                teamSyncStep,
                matchSyncStep,
                standingsSyncStep,
                averagesStep,
                probabilityStep
        );

        inOrder.verify(competitionSyncStep).sync(eq("PL"), any(Year.class));
        inOrder.verify(teamSyncStep).sync(csc);
        inOrder.verify(matchSyncStep).sync(csc, tsc);
        inOrder.verify(standingsSyncStep).sync(csc, tsc);
        inOrder.verify(averagesStep).sync(csc, tsc, ssc);
        inOrder.verify(probabilityStep).sync(csc);
    }

    @Test
    void syncCompetition_shouldStopAfterCompetitionStep_whenNoActiveCompetition(){
        CompetitionSyncContext inactiveCsc = this.buildInactiveCsc();

        when(competitionSyncStep.sync(eq("PL"), any(Year.class)))
                .thenReturn(inactiveCsc);

        externalSyncOrchestrator.syncCompetition("PL");

        verify(competitionSyncStep).sync(eq("PL"), any(Year.class));

        verifyNoInteractions(teamSyncStep);
        verifyNoInteractions(matchSyncStep);
        verifyNoInteractions(standingsSyncStep);
        verifyNoInteractions(averagesStep);
        verifyNoInteractions(probabilityStep);
    }

    //Locks
    @Test
    void syncCompetition_shouldThrowException_whenLockAlreadyHeldForSameCode() throws InterruptedException{
        Competition competition = CompetitionTestFactory.buildCompetition("PL");

        CompetitionSyncContext csc = SyncContextTestFactory.buildCsc(competition);

        TeamSyncContext tsc = SyncContextTestFactory.buildTsc(List.of());

        StandingsSyncContext ssc = SyncContextTestFactory.buildSsc(List.of(), List.of());

        CountDownLatch firstCallHoldingLock = new CountDownLatch(1);
        CountDownLatch releaseFirstCall = new CountDownLatch(1);

        when(competitionSyncStep.sync(eq("PL"), any(Year.class)))
                .thenReturn(csc);

        when(teamSyncStep.sync(csc))
                .thenAnswer( invocation -> {
                    // sinaliza que já entrou no sync e está segurando o lock
                    firstCallHoldingLock.countDown();
                    releaseFirstCall.await();
                    return tsc;
                });

        when(standingsSyncStep.sync(csc, tsc))
                .thenReturn(ssc);

        Thread firstThread = new Thread(() -> {
            externalSyncOrchestrator.syncCompetition("PL");
        });

        firstThread.start();

        boolean reachedLockedSection = firstCallHoldingLock.await(2, TimeUnit.SECONDS);

        assertTrue(reachedLockedSection);

        // segunda chamada, mesma competição, lock já está preso na thread acima
        assertThrows(RuntimeException.class,
                ()-> externalSyncOrchestrator.syncCompetition("PL"));

        releaseFirstCall.countDown();
        firstThread.join(2000);

        //só a primeira chamada realmente completou o fluxo
        verify(competitionSyncStep, times(1))
                .sync(eq("PL"), any(Year.class));

        verify(matchSyncStep, times(1)).sync(csc, tsc);
    }

    @Test
    void syncCompetition_shouldNotBlock_whenCodesAreDifferente(){

        Competition cPL = CompetitionTestFactory.buildCompetition("PL");
        Competition cBL1 = CompetitionTestFactory.buildCompetition("BL1");

        CompetitionSyncContext plCsc = SyncContextTestFactory.buildCsc(cPL);
        CompetitionSyncContext bl1Csc = SyncContextTestFactory.buildCsc(cBL1);

        TeamSyncContext tsc = SyncContextTestFactory.buildTsc(List.of());
        StandingsSyncContext ssc = SyncContextTestFactory.buildSsc(List.of(), List.of());

        when(competitionSyncStep.sync(eq("PL"), any(Year.class)))
                .thenReturn(plCsc);
        when(competitionSyncStep.sync(eq("BL1"), any(Year.class)))
                .thenReturn(bl1Csc);

        when(teamSyncStep.sync(any(CompetitionSyncContext.class)))
                .thenReturn(tsc);

        when(standingsSyncStep.sync(any(CompetitionSyncContext.class), any(TeamSyncContext.class)))
                .thenReturn(ssc);

        assertDoesNotThrow(()->{
            externalSyncOrchestrator.syncCompetition("PL");
            externalSyncOrchestrator.syncCompetition("BL1");
        });

        verify(competitionSyncStep).sync(eq("PL"), any(Year.class));
        verify(competitionSyncStep).sync(eq("BL1"), any(Year.class));

    }

    @Test
    void syncCompetition_shouldAllowNewSync_afterPreviousLockWasReleased(){
        Competition competition = CompetitionTestFactory.buildCompetition("PL");
        CompetitionSyncContext csc = SyncContextTestFactory.buildCsc(competition);
        TeamSyncContext tsc = SyncContextTestFactory.buildTsc(List.of());
        StandingsSyncContext ssc = SyncContextTestFactory.buildSsc(List.of(), List.of());

        when(competitionSyncStep.sync(eq("PL"), any(Year.class)))
                .thenReturn(csc);
        when(teamSyncStep.sync(csc))
                .thenReturn(tsc);
        when(standingsSyncStep.sync(csc, tsc))
                .thenReturn(ssc);

        // primeira chamada completa normalmente e libera o lock ao final
        externalSyncOrchestrator.syncCompetition("PL");

        //segunda chamada, mesmo código, lock ja foi liberado e não deve lançar
        assertDoesNotThrow(() -> externalSyncOrchestrator.syncCompetition("PL"));

        verify(competitionSyncStep, times(2))
                .sync(eq("PL"), any(Year.class));
    }

    @Test
    void syncCompetition_shouldReleaseLock_evenWhenSyncFails(){
        Competition competition = CompetitionTestFactory.buildCompetition("PL");
        CompetitionSyncContext csc = SyncContextTestFactory.buildCsc(competition);

        when(competitionSyncStep.sync(eq("PL"), any(Year.class)))
                .thenReturn(csc);
        when(teamSyncStep.sync(csc)).thenThrow(new RuntimeException("API indisponível"));

        //primeira falhou
        assertThrows(RuntimeException.class,()-> externalSyncOrchestrator.syncCompetition("PL"));

        // reset para simular uma nova tentativa
        reset(teamSyncStep);
        TeamSyncContext tsc = SyncContextTestFactory.buildTsc(List.of());

        when(teamSyncStep.sync(csc))
                .thenReturn(tsc);
        when(standingsSyncStep.sync(csc, tsc))
                .thenReturn(SyncContextTestFactory.buildSsc(List.of(), List.of()));

        //se o lock nao foi liberado, essa chamada lança lock acquired
        assertDoesNotThrow(() -> externalSyncOrchestrator.syncCompetition("PL"));
    }
}
