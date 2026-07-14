package lp.edu.fstats.repository;

import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.model.match.Match;
import lp.edu.fstats.model.probability.Probability;
import lp.edu.fstats.model.team.Team;
import lp.edu.fstats.repository.competition.CompetitionRepository;
import lp.edu.fstats.repository.match.MatchRepository;
import lp.edu.fstats.repository.probability.ProbabilityRepository;
import lp.edu.fstats.repository.team.TeamRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;

public class ProbabilityRepositoryTest extends RepositoryTestBase {

    @Autowired
    private ProbabilityRepository probabilityRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private TeamRepository teamRepository;

    @BeforeEach
    void setUp(){
        probabilityRepository.deleteAll();
        matchRepository.deleteAll();
        competitionRepository.deleteAll();
        teamRepository.deleteAll();
    }

    private Competition buildCompetition(){

        Competition competition = new Competition();

        competition.setCode("PL");
        competition.setName("Premier League");
        competition.setType("LEAGUE");
        competition.setEmblem("emblem.png");
        competition.setExternalId(100L);
        competition.setStartDate(LocalDate.of(2024, 8, 1));
        competition.setEndDate(LocalDate.of(2025, 5, 1));
        competition.setStatus("Em andamento");

        return competition;
    }

    private Team buildTeam(Long externalId, String name){
        Team team = new Team();

        team.setExternalId(externalId);
        team.setName(name);
        team.setShortName(name);
        team.setTla(name.substring(0, 3).toUpperCase());
        team.setCrest("crest.png");

        return team;
    }

    private Match buildMatch(Competition competition, Team home, Team away, Integer matchDay) {
        Match match = new Match();
        match.setExternalId((long) matchDay * 100);
        match.setCompetition(competition);
        match.setHomeTeam(home);
        match.setAwayTeam(away);
        match.setMatchDay(matchDay);
        match.setStatus("FINISHED");
        match.setUtcDate(LocalDateTime.now());
        match.setHomeGoals(2);
        match.setAwayGoals(1);
        return match;
    }

    private Probability buildProbability(Match match, Competition competition, Integer matchDay) {
        Probability probability = new Probability();
        probability.setMatch(match);
        probability.setCompetition(competition);
        probability.setMatchDay(matchDay);
        probability.setProbabilityOver05(BigDecimal.valueOf(0.80));
        probability.setProbabilityOver15(BigDecimal.valueOf(0.60));
        probability.setProbabilityOver25(BigDecimal.valueOf(0.30));
        return probability;
    }

    // findMaxMatchday
    @Test
    void findMaxMatchday_shouldReturnHighestMatchDay_whenMultipleProbabilitiesExist(){

        Competition competition = competitionRepository.save(this.buildCompetition());

        Team home = teamRepository.save(
                this.buildTeam(100L, "Arsenal")
        );

        Team away = teamRepository.save(
                this.buildTeam(200L, "Chelsea")
        );

        Match match3 = matchRepository.save(
                this.buildMatch(competition, home, away, 3)
        );

        Match match5 = matchRepository.save(
                this.buildMatch(competition, home, away, 5)
        );

        Match match7 = matchRepository.save(
                this.buildMatch(competition, home, away, 7)
        );


        probabilityRepository.save(
                this.buildProbability(match3, competition, 3));

        probabilityRepository.save(
                this.buildProbability(match5, competition, 5)
        );

        probabilityRepository.save(
                this.buildProbability(match7, competition, 7)
        );


        Integer result = probabilityRepository.findMaxMatchday(competition.getId());


        assertEquals(7, result);
    }

    @Test
    void findMaxMatchday_shouldReturnNull_whenNoProbabilitiesExist(){
        Competition competition = competitionRepository.save(this.buildCompetition());

        Integer result = probabilityRepository.findMaxMatchday(competition.getId());

        assertNull(result);
    }

}
