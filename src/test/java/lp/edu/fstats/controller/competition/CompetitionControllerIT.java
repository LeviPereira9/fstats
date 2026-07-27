package lp.edu.fstats.controller.competition;

import lp.edu.fstats.base.IntegrationTestBase;
import lp.edu.fstats.factory.entity.AveragesTestFactory;
import lp.edu.fstats.factory.entity.MatchTestFactory;
import lp.edu.fstats.factory.entity.StandingsTestFactory;
import lp.edu.fstats.factory.entity.TeamTestFactory;
import lp.edu.fstats.model.avarages.Averages;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.model.match.Match;
import lp.edu.fstats.model.standings.Standings;
import lp.edu.fstats.model.team.Team;
import lp.edu.fstats.model.user.User;
import lp.edu.fstats.repository.averages.AveragesRepository;
import lp.edu.fstats.repository.competition.CompetitionRepository;
import lp.edu.fstats.repository.match.MatchRepository;
import lp.edu.fstats.repository.standings.StandingsRepository;
import lp.edu.fstats.repository.team.TeamRepository;
import lp.edu.fstats.support.helper.AuthTestHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CompetitionControllerIT extends IntegrationTestBase {

    @Autowired
    private AuthTestHelper authTestHelper;

    @MockitoSpyBean
    private CompetitionRepository competitionRepository;

    @Autowired
    private TeamRepository teamRepository;

    @MockitoSpyBean
    private MatchRepository matchRepository;

    @MockitoSpyBean
    private AveragesRepository averagesRepository;

    @MockitoSpyBean
    private StandingsRepository standingsRepository;

    public Competition createDefaultCompetition(String code){
        return this.createCompetition(code, 2013L);
    }

    //======================== helpers ========================
    public Competition createCompetition(String code, Long externalId){
        Competition competition = new Competition();
        competition.setName("Premier League");
        competition.setCode(code);
        competition.setType("LEAGUE");
        competition.setEmblem("https://teste.com.br/img.png");
        competition.setExternalId(externalId);
        competition.setCount(10);
        competition.setStartDate(LocalDate.now().minusMonths(3));
        competition.setEndDate(LocalDate.now().plusMonths(3));
        competition.setStoredMatchDay(5);
        competition.setApiCurrentMatchDay(4);
        competition.setLastCompletedMatchDay(3);
        competition.setActive(true);

        return competitionRepository.save(competition);
    }

    public Team createTeam(String name, Long externalId){
        Team team = new Team();
        team.setExternalId(externalId);
        team.setName(name);
        team.setShortName(name);
        team.setTla(name.substring(0, 3).toUpperCase());
        team.setCrest("crest.png");

        return teamRepository.save(team);
    }

    private void createMatch(Team home, Team away, Integer matchDay, Competition competition){
        Match match = new Match();
        match.setExternalId(1L);
        match.setHomeTeam(home);
        match.setAwayTeam(away);
        match.setHomeGoals(2);
        match.setAwayGoals(1);
        match.setStatus("FINISHED");
        match.setMatchDay(matchDay);
        match.setCompetition(competition);
        match.setUtcDate(LocalDateTime.of(2024, 5, 1, 16, 0));

        matchRepository.save(match);
    }

    private void createAverages(Team team, Competition competition){
        Averages averages = new Averages();

        averages.setTeam(team);
        averages.setCompetition(competition);

        averages.setAvgGoalsForHome(BigDecimal.valueOf(1.5));
        averages.setAvgGoalsAgainstHome(BigDecimal.valueOf(0.8));
        averages.setAvgGoalsForAway(BigDecimal.valueOf(1.1));
        averages.setAvgGoalsAgainstAway(BigDecimal.valueOf(1.2));

        averagesRepository.save(averages);
    }

    public void createStandings(Competition competition, Team team){
        Standings standings = new Standings();

        standings.setCompetition(competition);
        standings.setTeam(team);
        standings.setType("TOTAL");
        standings.setPosition(1);
        standings.setPlayedGames(10);
        standings.setForm("WWDLW");
        standings.setWon(6);
        standings.setDraw(2);
        standings.setLost(2);
        standings.setPoints(20);
        standings.setGoalsFor(18);
        standings.setGoalsAgainst(10);
        standings.setGoalDifference(8);

        standingsRepository.save(standings);
    }


    //======================== getCompetition ========================
    @Test
    void getCompetition_shouldReturnForbiddenAction_whenNoCookieIsProvided(){
        given()
        .when()
                .get("/competition/{code}", "PL")
        .then()
                .statusCode(401)
                .body("operation", equalTo("Error.ForbiddenAction"))
                .body("message", equalTo("Usuário não autenticado."));
    }

    @Test
    void getCompetition_shouldReturnNotFound_whenCodeDoesNotExist(){
        User user = authTestHelper.createDefaultUser("senha12345");

        given()
                .cookie(authTestHelper.authCookie(user))
        .when()
                .get("/competition/{code}", "999")
        .then()
                .statusCode(404)
                .body("operation", equalTo("Error.NotFound"))
                .body("message", equalTo("Competição não encontrada. Solicite um novo envio."));
    }

    @Test
    void getCompetition_shouldReturnOk_whenCodeExists(){
        User user = authTestHelper.createDefaultUser("senha12345");

        this.createDefaultCompetition("PL");

        given()
                .cookie(authTestHelper.authCookie(user))
        .when()
                .get("/competition/{code}", "PL")
        .then()
                .statusCode(200)
                .body("operation", equalTo("Competition.GetCompetition"))
                .body("message", equalTo("Competição encontrada com sucesso."))
                .body("data.code", equalTo("PL"));
    }

    @Test
    void getCompetition_shouldReturnCachedValue_whenCalledMultipleTimes(){
        User user = authTestHelper.createDefaultUser("senha12345");

        this.createDefaultCompetition("PL");

        //1 chamada
        given()
                .cookie(authTestHelper.authCookie(user))
        .when()
                .get("/competition/{code}", "PL")
        .then()
                .statusCode(200)
                .body("data.code", equalTo("PL"));

        //2 e 3 deve vir pelo cache.
        //1 chamada
        given()
                .cookie(authTestHelper.authCookie(user))
                .when()
                .get("/competition/{code}", "PL")
                .then()
                .statusCode(200)
                .body("data.code", equalTo("PL"));

        //1 chamada
        given()
                .cookie(authTestHelper.authCookie(user))
                .when()
                .get("/competition/{code}", "PL")
                .then()
                .statusCode(200)
                .body("data.code", equalTo("PL"));

        verify(competitionRepository, times(1)).findByCode("PL");
    }

    @Test
    void getCompetition_shouldUseSeparateCacheEntries_whenCodesAreDifferent(){
        User user = authTestHelper.createDefaultUser("senha12345");

        this.createDefaultCompetition("PL");
        this.createCompetition("BSA", 2050L);

        given()
                .cookie(authTestHelper.authCookie(user))
        .when()
                .get("/competition/{code}", "PL")
        .then()
                .statusCode(200);

        given()
                .cookie(authTestHelper.authCookie(user))
        .when()
                .get("/competition/{code}", "BSA")
        .then()
                .statusCode(200);

        verify(competitionRepository, times(1)).findByCode("PL");
        verify(competitionRepository, times(1)).findByCode("BSA");
    }

    @Test
    void getCompetition_shouldReturnTooManyRequests_whenRateLimitIsExceeded(){
        User user = authTestHelper.createDefaultUser("senha12345");

        this.createDefaultCompetition("PL");

        for(int i = 0; i < 10; i++){
            given()
                    .cookie(authTestHelper.authCookie(user))
            .when()
                    .get("/competition/{code}", "PL")
            .then()
                    .statusCode(200);
        }

        given()
                .cookie(authTestHelper.authCookie(user))
        .when()
                .get("/competition/{code}", "PL")
        .then()
                .statusCode(429)
                .body("operation", equalTo("Error.RateLimitExceeded"));

    }

    //======================== getMatches ========================
    @Test
    void getMatches_shouldReturnForbiddenAction_whenNoCookieIsProvided(){
        given()
        .when()
                .get("/competition/{competitionId}/matches", 1L)
        .then()
                .statusCode(401)
                .body("operation", equalTo("Error.ForbiddenAction"))
                .body("message", equalTo("Usuário não autenticado."));
    }

    @Test
    void getMatches_shouldReturnNotFound_whenNoMatchesExistForMatchday(){
        User user = authTestHelper.createDefaultUser("senha12345");

        Competition competition = this.createDefaultCompetition("PL");

        given()
                .cookie(authTestHelper.authCookie(user))
                .queryParam("matchday", 3)
        .when()
                .get("/competition/{competitionId}/matches", competition.getId())
        .then()
                .statusCode(404)
                .body("operation", equalTo("Error.NotFound"))
                .body("message", equalTo("Nenhuma partida foi encontrada. Solicite um novo envio."));
    }

    @Test
    void getMatches_shouldReturnMatchesData_whenMatchesExist(){
        User user = authTestHelper.createDefaultUser("senha12345");

        Competition competition = this.createDefaultCompetition("PL");
        Team home = this.createTeam("Arsenal", 1L);
        Team away = this.createTeam("Chelsea", 2L);


        this.createMatch(
                home,
                away,
                5,
                competition
        );

        given()
                .cookie(authTestHelper.authCookie(user))
                .queryParam("matchday", 5)
        .when()
                .get("/competition/{competitionId}/matches", competition.getId())
        .then()
                .statusCode(200)
                .body("operation", equalTo("Competition.Matches.GetMatches"))
                .body("message", equalTo("Partidas encontradas com sucesso."))
                .body("data.matches.size()", equalTo(1));
    }

    @Test
    void getMatches_shouldReturnCachedValue_whenCalledMultipleTimes(){
        User user = authTestHelper.createDefaultUser("senha12345");

        Competition competition = this.createDefaultCompetition("PL");
        Team home = this.createTeam("Arsenal", 1L);
        Team away = this.createTeam("Chelsea", 2L);


        this.createMatch(
                home,
                away,
                5,
                competition
        );

        //1
        given()
                .cookie(authTestHelper.authCookie(user))
                .queryParam("matchday", 5)
        .when()
                .get("/competition/{competitionId}/matches", competition.getId())
        .then()
                .statusCode(200);
        //2
        given()
                .cookie(authTestHelper.authCookie(user))
                .queryParam("matchday", 5)
        .when()
                .get("/competition/{competitionId}/matches", competition.getId())
        .then()
                .statusCode(200);

        verify(matchRepository, times(1)).findAllByCompetition_IdAndMatchDay(competition.getId(), 5);
    }

    @Test
    void getMatches_shouldReturnTooManyRequests_whenRateLimitIsExceeded(){
        User user = authTestHelper.createDefaultUser("senha12345");

        Competition competition = this.createDefaultCompetition("PL");
        Team home = this.createTeam("Arsenal", 1L);
        Team away = this.createTeam("Chelsea", 2L);

        this.createMatch(
                home,
                away,
                1,
                competition
        );

        for(int i = 0; i < 15; i++){
            given()
                    .cookie(authTestHelper.authCookie(user))
                    .queryParam("matchday", 1)
            .when()
                    .get("/competition/{competitionId}/matches", competition.getId())
            .then()
                    .statusCode(200);
        }

        given()
                .cookie(authTestHelper.authCookie(user))
        .when()
                .get("/competition/{competitionId}/matches", competition.getId())
        .then()
                .statusCode(429)
                .body("operation", equalTo("Error.RateLimitExceeded"));
    }

    //======================== getAverages ========================
    @Test
    void getAverages_shouldReturnForbiddenAction_whenNoCookieIsProvided(){
        given()
        .when()
                .get("/competition/{competitionIdd}/averages", 1L)
        .then()
                .statusCode(401)
                .body("operation", equalTo("Error.ForbiddenAction") );
    }

    @Test
    void getAverages_shouldReturnAveragesData_whenAveragesExist(){
        User user = authTestHelper.createDefaultUser("senha12345");

        Competition competition = this.createDefaultCompetition("PL");
        Team team = this.createTeam("Arsenal", 1L);
        this.createAverages(team, competition);

        given()
                .cookie(authTestHelper.authCookie(user))
        .when()
                .get("/competition/{competitionId}/averages", competition.getId())
        .then()
                .statusCode(200)
                .body("operation", equalTo("Competition.Averages.GetAverages"))
                .body("message", equalTo("Médias encontradas com sucesso."))
                .body("data.averages[0].teamName", equalTo("Arsenal"));
    }

    @Test
    void getAverages_shouldReturnCachedValue_whenCalledMultipleTimes(){
        User user = authTestHelper.createDefaultUser("senha12345");

        Competition competition = this.createDefaultCompetition("PL");
        Team team = this.createTeam("Arsenal", 1L);

        this.createAverages(team, competition);

        //1
        given()
                .cookie(authTestHelper.authCookie(user))
        .when()
                .get("/competition/{competitionId}/averages", competition.getId())
        .then()
                .statusCode(200);

        //2
        given()
                .cookie(authTestHelper.authCookie(user))
        .when()
                .get("/competition/{competitionId}/averages", competition.getId())
        .then()
                .statusCode(200);

        verify(averagesRepository, times(1)).findAllByCompetition_Id(competition.getId());
    }

    @Test
    void getAverages_shouldReturnNotFound_whenNoAveragesExist() {
        User user = authTestHelper.createDefaultUser("Senha123!");

        Competition competition = this.createDefaultCompetition("PL");

        given()
                .cookie(authTestHelper.authCookie(user))
        .when()
                .get("/competition/{competitionId}/averages", competition.getId())
        .then()
                .statusCode(404)
                .body("operation", equalTo("Error.NotFound"))
                .body("message", equalTo("Médias não encontradas. Solicite um novo envio."));

        verify(averagesRepository, times(1)).findAllByCompetition_Id(competition.getId());
    }

    @Test
    void getAverages_shouldReturnTooManyRequests_whenRateLimitIsExceeded(){
        User user = authTestHelper.createDefaultUser("senha12345");

        Competition competition = this.createDefaultCompetition("PL");
        Team team = this.createTeam("Arsenal", 1L);

        this.createAverages(team, competition);

        for(int i = 0; i < 10; i++){
            given()
                    .cookie(authTestHelper.authCookie(user))
            .when()
                    .get("/competition/{competitionId}/averages", competition.getId())
            .then()
                    .statusCode(200);
        }

        given()
                .cookie(authTestHelper.authCookie(user))
        .when()
                .get("/competition/{competitionId}/averages", competition.getId())
        .then()
                .statusCode(429)
                .body("operation", equalTo("Error.RateLimitExceeded"));

    }

    //======================== getStandings ========================
    @Test
    void getStandings_shouldReturnForbiddenAction_whenNoCookieIsProvided(){
        given()
        .when()
                .get("/competition/{competitionId}/standings", 1L)
        .then()
                .statusCode(401)
                .body("operation", equalTo("Error.ForbiddenAction"));
    }

    @Test
    void getStandings_shouldReturnNotFound_whenNoStandingsExist(){
        User user = authTestHelper.createDefaultUser("senha12345");

        Competition competition = this.createDefaultCompetition("PL");

        given()
                .cookie(authTestHelper.authCookie(user))
        .when()
                .get("/competition/{competitionId}/standings", competition.getId())
        .then()
                .statusCode(404)
                .body("operation", equalTo("Error.NotFound"))
                .body("message", equalTo("Classificação não encontrada. Solicite um novo envio."));
    }

    @Test
    void getStandings_shouldReturnStandingsData_whenStandingsExist(){
        User user = authTestHelper.createDefaultUser("senha12345");

        Competition competition = this.createDefaultCompetition("PL");

        Team team = this.createTeam("Arsenal", 1L);

        this.createStandings(competition, team);

        given()
                .cookie(authTestHelper.authCookie(user))
        .when()
                .get("/competition/{competitionId}/standings", competition.getId())
        .then()
                .statusCode(200)
                .body("operation", equalTo("Competition.Standings.GetStandings"))
                .body("message", equalTo("Classificações encontradas com sucesso."));
    }

    @Test
    void getStandings_shouldReturnCachedValue_whenCalledMultipleTimes(){
        User user = authTestHelper.createDefaultUser("senha12345");

        Competition competition = this.createDefaultCompetition("PL");
        Team team = this.createTeam("Arsenal", 1L);

        this.createStandings(competition, team);

        given()
                .cookie(authTestHelper.authCookie(user))
        .when()
                .get("/competition/{competitionId}/standings", competition.getId())
        .then()
                .statusCode(200);

        given()
                .cookie(authTestHelper.authCookie(user))
        .when()
                .get("/competition/{competitionId}/standings", competition.getId())
        .then()
                .statusCode(200);

        verify(standingsRepository, times(1)).findAllByCompetition_Id(competition.getId());
    }

    @Test
    void getStandings_shouldReturnTooManyRequests_whenRateLimitIsExceeded(){
        User user = authTestHelper.createDefaultUser("senha12345");

        Competition competition = this.createDefaultCompetition("PL");

        Team team = this.createTeam("Arsenal", 1L);

        this.createStandings(competition, team);

        for(int i = 0; i < 10; i++){
            given()
                    .cookie(authTestHelper.authCookie(user))
            .when()
                    .get("/competition/{competitionId}/standings", competition.getId())
            .then()
                    .statusCode(200);

        }

        given()
                .cookie(authTestHelper.authCookie(user))
        .when()
                .get("/competition/{competitionId}/standings", competition.getId())
        .then()
                .statusCode(429)
                .body("operation", equalTo("Error.RateLimitExceeded"));

    }

}
