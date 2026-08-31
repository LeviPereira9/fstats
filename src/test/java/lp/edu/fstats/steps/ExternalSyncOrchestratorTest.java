package lp.edu.fstats.steps;

import lp.edu.fstats.exception.custom.CustomNotFoundException;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Year;
import java.util.List;
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

    private Code buildCode(String code, Integer id){
        Code c = new Code();
        c.setId(id);
        c.setCode(code);
        c.setName("Premier League");
        c.setActive(true);

        return c;
    }

    private Code buildCode(String code){
        return this.buildCode(code, 1);
    }

    //syncAll
    @Test
    void syncAll_shouldSyncAllCodes_whenCodesExist(){
        Code pl = this.buildCode("PL", 1);
        Code bl1 = this.buildCode("BL1", 2);

        Competition cPl = CompetitionTestFactory.buildCompetition("PL");
        Competition cBl1 = CompetitionTestFactory.buildCompetition("BL1");

        CompetitionSyncContext plCsc = SyncContextTestFactory.buildCsc(cPl);
        CompetitionSyncContext bl1Csc = SyncContextTestFactory.buildCsc(cBl1);

        TeamSyncContext tsc = SyncContextTestFactory.buildTsc(List.of());
        StandingsSyncContext ssc = SyncContextTestFactory.buildSsc(List.of(), List.of());

        when(codeRepository.findAll())
                .thenReturn(List.of(pl, bl1));

        when(competitionSyncStep.sync(eq(pl), any(Year.class)))
                .thenReturn(plCsc);

        when(competitionSyncStep.sync(eq(bl1), any(Year.class)))
                .thenReturn(bl1Csc);

        when(teamSyncStep.sync(any(CompetitionSyncContext.class)))
                .thenReturn(tsc);

        when(standingsSyncStep.sync(any(CompetitionSyncContext.class), any(TeamSyncContext.class)))
                .thenReturn(ssc);

        externalSyncOrchestrator.syncAll();

        verify(competitionSyncStep, times(2)).sync(any(Code.class), any(Year.class));
        verify(teamSyncStep, times(2)).sync(any(CompetitionSyncContext.class));
        verify(matchSyncStep, times(2)).sync(any(CompetitionSyncContext.class), any(TeamSyncContext.class));
        verify(standingsSyncStep, times(2)).sync(any(CompetitionSyncContext.class), any(TeamSyncContext.class));
        verify(averagesStep, times(2)).sync(any(), any(), any());
        verify(probabilityStep, times(2)).sync(any(CompetitionSyncContext.class));
    }

    @Test
    void syncAll_shouldSkipInactiveCodes(){
        Code pl = this.buildCode("PL", 1);
        pl.setActive(false);

        when(codeRepository.findAll()).thenReturn(List.of(pl));

        externalSyncOrchestrator.syncAll();

        verifyNoInteractions(competitionSyncStep);
        verifyNoInteractions(teamSyncStep);
        verifyNoInteractions(matchSyncStep);
        verifyNoInteractions(standingsSyncStep);
        verifyNoInteractions(averagesStep);
        verifyNoInteractions(probabilityStep);
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

        Code pl = this.buildCode("PL", 1);
        Code bl1 = this.buildCode("BL1", 2);

        Competition cBl1 = CompetitionTestFactory.buildCompetition("BL1");
        CompetitionSyncContext bl1Csc = SyncContextTestFactory.buildCsc(cBl1);

        TeamSyncContext tsc = SyncContextTestFactory.buildTsc(List.of());
        StandingsSyncContext ssc = SyncContextTestFactory.buildSsc(List.of(), List.of());

        when(codeRepository.findAll())
                .thenReturn(List.of(pl, bl1));

        when(competitionSyncStep.sync(eq(pl), any(Year.class)))
                .thenThrow(new RuntimeException("API indisponível"));

        when(competitionSyncStep.sync(eq(bl1), any(Year.class)))
                .thenReturn(bl1Csc);

        when(teamSyncStep.sync(any(CompetitionSyncContext.class)))
                .thenReturn(tsc);

        when(standingsSyncStep.sync(any(CompetitionSyncContext.class), any(TeamSyncContext.class)))
                .thenReturn(ssc);

        externalSyncOrchestrator.syncAll();

        //bl1 foi normal, pl deu throw
        verify(competitionSyncStep, times(2)).sync(any(Code.class), any(Year.class));
        verify(teamSyncStep, times(1)).sync(any(CompetitionSyncContext.class));
    }

    // sync (via syncCompetition)
    @Test
    void syncCompetition_shouldExecuteAllSteps_whenCompetitionIsActive(){

        Code code = this.buildCode("PL", 1);
        Competition competition = CompetitionTestFactory.buildCompetition("PL");

        CompetitionSyncContext csc = SyncContextTestFactory.buildCsc(competition);
        TeamSyncContext tsc = SyncContextTestFactory.buildTsc(List.of());
        StandingsSyncContext ssc = SyncContextTestFactory.buildSsc(List.of(), List.of());

        when(codeRepository.findById(1))
                .thenReturn(Optional.of(code));

        when(competitionSyncStep.sync(eq(code), any(Year.class)))
                .thenReturn(csc);

        when(teamSyncStep.sync(csc))
                .thenReturn(tsc);

        when(standingsSyncStep.sync(csc, tsc))
                .thenReturn(ssc);

        externalSyncOrchestrator.syncCompetition(1);

        InOrder inOrder = inOrder(
                competitionSyncStep,
                teamSyncStep,
                matchSyncStep,
                standingsSyncStep,
                averagesStep,
                probabilityStep
        );

        inOrder.verify(competitionSyncStep).sync(eq(code), any(Year.class));
        inOrder.verify(teamSyncStep).sync(csc);
        inOrder.verify(matchSyncStep).sync(csc, tsc);
        inOrder.verify(standingsSyncStep).sync(csc, tsc);
        inOrder.verify(averagesStep).sync(csc, tsc, ssc);
        inOrder.verify(probabilityStep).sync(csc);
    }

    @Test
    void syncCompetition_shouldStopAfterCompetitionStep_whenNoActiveCompetition(){
        Code code = this.buildCode("PL", 1);
        CompetitionSyncContext inactiveCsc = this.buildInactiveCsc();

        when(codeRepository.findById(1))
                .thenReturn(Optional.of(code));

        when(competitionSyncStep.sync(eq(code), any(Year.class)))
                .thenReturn(inactiveCsc);

        externalSyncOrchestrator.syncCompetition(1);

        verify(competitionSyncStep).sync(eq(code), any(Year.class));

        verifyNoInteractions(teamSyncStep);
        verifyNoInteractions(matchSyncStep);
        verifyNoInteractions(standingsSyncStep);
        verifyNoInteractions(averagesStep);
        verifyNoInteractions(probabilityStep);
    }

    @Test
    void syncCompetition_shouldThrowNotFound_whenCodeDoesNotExist(){
        when(codeRepository.findById(99))
                .thenReturn(Optional.empty());

        assertThrows(CustomNotFoundException.class,
                () -> externalSyncOrchestrator.syncCompetition(99));

        verifyNoInteractions(competitionSyncStep);
    }

    //Locks
    @Test
    void syncCompetition_shouldThrowException_whenLockAlreadyHeldForSameCode() throws InterruptedException{
        Code code = this.buildCode("PL", 1);
        Competition competition = CompetitionTestFactory.buildCompetition("PL");

        CompetitionSyncContext csc = SyncContextTestFactory.buildCsc(competition);
        TeamSyncContext tsc = SyncContextTestFactory.buildTsc(List.of());
        StandingsSyncContext ssc = SyncContextTestFactory.buildSsc(List.of(), List.of());

        CountDownLatch firstCallHoldingLock = new CountDownLatch(1);
        CountDownLatch releaseFirstCall = new CountDownLatch(1);

        when(codeRepository.findById(1))
                .thenReturn(Optional.of(code));

        when(competitionSyncStep.sync(eq(code), any(Year.class)))
                .thenReturn(csc);

        when(teamSyncStep.sync(csc))
                .thenAnswer(invocation -> {
                    // sinaliza que já entrou no sync e está segurando o lock
                    firstCallHoldingLock.countDown();
                    releaseFirstCall.await();
                    return tsc;
                });

        when(standingsSyncStep.sync(csc, tsc))
                .thenReturn(ssc);

        Thread firstThread = new Thread(() -> {
            externalSyncOrchestrator.syncCompetition(1);
        });

        firstThread.start();

        boolean reachedLockedSection = firstCallHoldingLock.await(2, TimeUnit.SECONDS);
        assertTrue(reachedLockedSection);

        // segunda chamada, mesmo codeId, lock já está preso na thread acima
        assertThrows(RuntimeException.class,
                () -> externalSyncOrchestrator.syncCompetition(1));

        releaseFirstCall.countDown();
        firstThread.join(2000);

        //só a primeira chamada realmente completou o fluxo
        verify(competitionSyncStep, times(1))
                .sync(eq(code), any(Year.class));

        verify(matchSyncStep, times(1)).sync(csc, tsc);
    }

    @Test
    void syncCompetition_shouldNotBlock_whenCodesAreDifferent(){

        Code codePL = this.buildCode("PL", 1);
        Code codeBL1 = this.buildCode("BL1", 2);

        Competition cPL = CompetitionTestFactory.buildCompetition("PL");
        Competition cBL1 = CompetitionTestFactory.buildCompetition("BL1");

        CompetitionSyncContext plCsc = SyncContextTestFactory.buildCsc(cPL);
        CompetitionSyncContext bl1Csc = SyncContextTestFactory.buildCsc(cBL1);

        TeamSyncContext tsc = SyncContextTestFactory.buildTsc(List.of());
        StandingsSyncContext ssc = SyncContextTestFactory.buildSsc(List.of(), List.of());

        when(codeRepository.findById(1)).thenReturn(Optional.of(codePL));
        when(codeRepository.findById(2)).thenReturn(Optional.of(codeBL1));

        when(competitionSyncStep.sync(eq(codePL), any(Year.class)))
                .thenReturn(plCsc);
        when(competitionSyncStep.sync(eq(codeBL1), any(Year.class)))
                .thenReturn(bl1Csc);

        when(teamSyncStep.sync(any(CompetitionSyncContext.class)))
                .thenReturn(tsc);

        when(standingsSyncStep.sync(any(CompetitionSyncContext.class), any(TeamSyncContext.class)))
                .thenReturn(ssc);

        assertDoesNotThrow(() -> {
            externalSyncOrchestrator.syncCompetition(1);
            externalSyncOrchestrator.syncCompetition(2);
        });

        verify(competitionSyncStep).sync(eq(codePL), any(Year.class));
        verify(competitionSyncStep).sync(eq(codeBL1), any(Year.class));
    }

    @Test
    void syncCompetition_shouldAllowNewSync_afterPreviousLockWasReleased(){
        Code code = this.buildCode("PL", 1);
        Competition competition = CompetitionTestFactory.buildCompetition("PL");

        CompetitionSyncContext csc = SyncContextTestFactory.buildCsc(competition);
        TeamSyncContext tsc = SyncContextTestFactory.buildTsc(List.of());
        StandingsSyncContext ssc = SyncContextTestFactory.buildSsc(List.of(), List.of());

        when(codeRepository.findById(1)).thenReturn(Optional.of(code));

        when(competitionSyncStep.sync(eq(code), any(Year.class)))
                .thenReturn(csc);
        when(teamSyncStep.sync(csc))
                .thenReturn(tsc);
        when(standingsSyncStep.sync(csc, tsc))
                .thenReturn(ssc);

        // primeira chamada completa normalmente e libera o lock ao final
        externalSyncOrchestrator.syncCompetition(1);

        //segunda chamada, mesmo codeId, lock já foi liberado e não deve lançar
        assertDoesNotThrow(() -> externalSyncOrchestrator.syncCompetition(1));

        verify(competitionSyncStep, times(2))
                .sync(eq(code), any(Year.class));
    }

    @Test
    void syncCompetition_shouldReleaseLock_evenWhenSyncFails(){
        Code code = this.buildCode("PL", 1);
        Competition competition = CompetitionTestFactory.buildCompetition("PL");
        CompetitionSyncContext csc = SyncContextTestFactory.buildCsc(competition);

        when(codeRepository.findById(1)).thenReturn(Optional.of(code));

        when(competitionSyncStep.sync(eq(code), any(Year.class)))
                .thenReturn(csc);
        when(teamSyncStep.sync(csc)).thenThrow(new RuntimeException("API indisponível"));

        //primeira falhou
        assertThrows(RuntimeException.class, () -> externalSyncOrchestrator.syncCompetition(1));

        // reset para simular uma nova tentativa
        reset(teamSyncStep);
        TeamSyncContext tsc = SyncContextTestFactory.buildTsc(List.of());

        when(teamSyncStep.sync(csc))
                .thenReturn(tsc);
        when(standingsSyncStep.sync(csc, tsc))
                .thenReturn(SyncContextTestFactory.buildSsc(List.of(), List.of()));

        //se o lock não foi liberado, essa chamada lança "Lock acquired"
        assertDoesNotThrow(() -> externalSyncOrchestrator.syncCompetition(1));
    }
}