package lp.edu.fstats.repository;

import lp.edu.fstats.base.RepositoryTestBase;
import lp.edu.fstats.model.code.Code;
import lp.edu.fstats.repository.code.CodeRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.repository.competition.CompetitionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

public class CompetitionRepositoryIT extends RepositoryTestBase {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private CodeRepository codeRepository;

    @BeforeEach
    void setUp(){
        competitionRepository.deleteAllInBatch();
        codeRepository.deleteAllInBatch();
    }

    private Competition buildCompetition(Code code, LocalDate startDate, String status) {
        Competition competition = new Competition();
        competition.setCode(code);
        competition.setName("Premier League");
        competition.setType("LEAGUE");
        competition.setEmblem("emblem.png");
        competition.setExternalId(100L);
        competition.setStartDate(startDate);
        competition.setEndDate(startDate.plusMonths(9));
        competition.setStatus(status);
        competition.setActive(true);

        return competition;
    }

    private Code buildAndSaveCode(String codeName) {
        Code code = new Code();
        code.setCode(codeName);
        code.setName(codeName);
        return codeRepository.save(code);
    }

    //findByCode
    @Test
    void findByCode_shouldReturnMostRecentCompetition_whenMultipleExist(){

        Code codePL = this.buildAndSaveCode("PL");

        Competition comp1 = this.buildCompetition(
                codePL,
                LocalDate.of(2022, 8, 1),
                "Finalizada"
        );

        Competition comp2 = this.buildCompetition(
                codePL,
                LocalDate.of(2023, 8, 1),
                "Finalizada"
        );
        comp2.setExternalId(101L);

        Competition comp3 = this.buildCompetition(
                codePL,
                LocalDate.of(2024, 8, 1),
                "Em andamento"
        );
        comp3.setExternalId(102L);

        competitionRepository.save(comp1);

        competitionRepository.save(comp2);

        competitionRepository.save(comp3);

        Optional<Competition> result = competitionRepository.findByCodeAndStatus("PL");

        assertTrue(result.isPresent());
        assertEquals(LocalDate.of(2024, 8, 1), result.get().getStartDate());

    }
    
    @Test
    void findByCode_shouldReturnEmpty_whenCodeDoesNotExist(){
        Optional<Competition> result = competitionRepository.findByCode("XX");
        
        assertFalse(result.isPresent());
    }
    
    // findByCodeAndStatus

    @Test
    void findByCodeAndStatus_shouldReturnCompetition_whenStatusIsInProgress(){

        Code codePL = this.buildAndSaveCode("PL");

        Competition comp1 = this.buildCompetition(
                codePL,
                LocalDate.of(2023, 8, 1),
                "Finalizada"
        );

        Competition comp2 = this.buildCompetition(
                codePL,
                LocalDate.of(2024, 8, 1),
                "Em andamento"
        );
        comp2.setExternalId(101L);

        competitionRepository.save(comp1);
        competitionRepository.save(comp2);

        Optional<Competition> result = competitionRepository.findByCodeAndStatus("PL");

        assertTrue(result.isPresent());
        assertEquals("Em andamento", result.get().getStatus());
    }

    @Test
    void findByCodeAndStatus_shouldReturnEmpty_whenNoActiveCompetitionExists(){

        Code codePL = this.buildAndSaveCode("PL");

        competitionRepository.save(
                this.buildCompetition(
                        codePL,
                        LocalDate.of(2023, 8, 1),
                        "Finalizada"
                )
        );

        Optional<Competition> result = competitionRepository.findByCodeAndStatus("PL");

        assertFalse(result.isPresent());
    }

    @Test
    void findByCodeAndStatus_shouldReturnEmpty_whenCodeDoesNotExist(){

        Optional<Competition> result = competitionRepository.findByCodeAndStatus("XX");

        assertFalse(result.isPresent());
    }

    // existsByExternalId
    @Test
    void existsByExternalId_shouldReturnTrue_whenExternalIdExists(){
        Code codePL = this.buildAndSaveCode("PL");

        competitionRepository.save(
                this.buildCompetition(
                        codePL,
                        LocalDate.of(2024, 8, 1),
                        "Em andamento"
                )
        );

        assertTrue(competitionRepository.existsByExternalId(100L));
    }

    @Test
    void existsByExternalId_shouldReturnFalse_whenExternalIdDoesNotExist(){

        assertFalse(competitionRepository.existsByExternalId(999L));

    }
}
