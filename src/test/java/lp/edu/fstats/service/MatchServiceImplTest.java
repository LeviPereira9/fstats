package lp.edu.fstats.service;

import lp.edu.fstats.dto.match.MatchResponse;
import lp.edu.fstats.dto.match.MatchesResponse;
import lp.edu.fstats.exception.custom.CustomNotFoundException;
import lp.edu.fstats.factory.ProbabilityTestFactory;
import lp.edu.fstats.factory.TeamTestFactory;
import lp.edu.fstats.model.match.Match;
import lp.edu.fstats.model.probability.Probability;
import lp.edu.fstats.model.team.Team;
import lp.edu.fstats.repository.match.MatchRepository;
import lp.edu.fstats.service.match.MatchServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MatchServiceImplTest {

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private MatchServiceImpl matchService;

    //helpers
    private Match buildMatch(Long id, Long externalId, Team home, Team away, Integer matchDay){
        Match match = new Match();
        match.setId(id);
        match.setExternalId(externalId);
        match.setHomeTeam(home);
        match.setAwayTeam(away);
        match.setHomeGoals(2);
        match.setAwayGoals(1);
        match.setStatus("FINISHED");
        match.setMatchDay(matchDay);
        match.setUtcDate(LocalDateTime.of(2024, 5, 1, 16, 0));

        return match;
    }

    private Team buildTeam(Long id, String name){
        return TeamTestFactory.buildTeam(id,10L, name);
    }

    private Probability buildProbability(Match match){
        return ProbabilityTestFactory.buildProbability(
                BigDecimal.valueOf(0.80),
                BigDecimal.valueOf(0.60),
                BigDecimal.valueOf(0.30));
    }

    //findAllByExternalId
    @Test
    void findAllByExternalId_shouldReturnMapKeyedByExternalId_whenMatchesExist(){
        Team home = this.buildTeam(1L,"Arsenal");
        Team away = this.buildTeam(2L,"Chelsea");

        Match match = this.buildMatch(1L, 500L, home, away, 1);

        match.setProbability(this.buildProbability(match));

        List<Long> externalIds = List.of(500L);

        when(matchRepository.findAllByExternalIdIn(externalIds)).thenReturn(List.of(match));

        Map<Long, Match> result = matchService.findAllByExternalId(externalIds);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(match, result.get(500L));
    }

    @Test
    void findAllByExternalId_shouldReturnEmptyMap_whenNotMatchesFound(){
        when(matchRepository.findAllByExternalIdIn(any())).thenReturn(List.of());

        Map<Long, Match> result = matchService.findAllByExternalId(List.of(999L));

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // getMatches
    @Test
    void getMatches_shouldReturnMatchesResponse_whenMatchesExistWithProbability(){
        Team home = this.buildTeam(1L,"Arsenal");
        Team away = this.buildTeam(2L,"Chelsea");

        Match match = this.buildMatch(1L, 500L, home, away, 1);

        match.setProbability(this.buildProbability(match));

        when(matchRepository.findAllByCompetition_IdAndMatchDay(10L, 1)).thenReturn(List.of(match));

        MatchesResponse response = matchService.getMatches(10L, 1);

        assertNotNull(response);
        assertEquals(1, response.matches().size());

        MatchResponse matchResponse = response.matches().get(0);

        assertEquals("Arsenal", matchResponse.home().name());
        assertEquals("Chelsea", matchResponse.away().name());

        assertNotNull(matchResponse.probability());
    }

    @Test
    void getMatches_shouldReturnMatchesResponse_whenMatchHasNoProbability(){
        Team home = this.buildTeam(1L, "Arsenal");
        Team away = this.buildTeam(2L, "Chelsea");
        Match match = this.buildMatch(1L, 500L, home, away, 1);

        //sem probability
        when(matchRepository.findAllByCompetition_IdAndMatchDay(10L, 1)).thenReturn(List.of(match));

        MatchesResponse response = matchService.getMatches(10L, 1);

        assertNotNull(response);
        assertEquals(1, response.matches().size());

        assertNull(response.matches().get(0).probability());
    }

    @Test
    void getMatches_shouldThrowNotFound_whenNoMatchesExist(){

        when(matchRepository.findAllByCompetition_IdAndMatchDay(10L, 5)).thenReturn(List.of());

        assertThrows(CustomNotFoundException.class, () -> matchService.getMatches(10L, 5));

    }

    //saveAll
    @Test
    void saveAll_shouldCallRepositorySaveAll_withGivenMatches(){
        Team home = this.buildTeam(1L,"Arsenal");
        Team away = this.buildTeam(2L,"Chelsea");
        Match match = this.buildMatch(1L, 500L, home, away, 1);

        List<Match> matches = List.of(match);

        matchService.saveAll(matches);

        verify(matchRepository).saveAll(matches);
    }

}
