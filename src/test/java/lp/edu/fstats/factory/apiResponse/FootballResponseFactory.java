package lp.edu.fstats.factory.apiResponse;

import lp.edu.fstats.integration.dto.competition.CompetitionExternalResponse;
import lp.edu.fstats.integration.dto.competition.CurrentSeasonExternalResponse;
import lp.edu.fstats.integration.dto.matches.MatchesExternalResponse;
import lp.edu.fstats.integration.dto.matches.match.*;
import lp.edu.fstats.integration.dto.standings.StandingsExternalResponse;
import lp.edu.fstats.integration.dto.standings.TableExternalResponse;
import lp.edu.fstats.integration.dto.standings.TablesExternalResponse;
import lp.edu.fstats.integration.dto.teams.CompetitionTeamExternalResponse;
import lp.edu.fstats.integration.dto.teams.CompetitionTeamsExternalResponse;
import lp.edu.fstats.integration.service.football.sync.context.CompetitionSyncContext;
import lp.edu.fstats.model.competition.Competition;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class FootballResponseFactory {

    // season
    public static CurrentSeasonExternalResponse buildExternalSeasonResponse(Long id, int currentMatchDay){
        return new CurrentSeasonExternalResponse(
                id,
                LocalDate.of(2024, 8, 1),
                LocalDate.of(2025, 5, 1),
                currentMatchDay
        );
    }

    // Competition
    public static CompetitionExternalResponse buildExternalCompetitionResponse(Long seasonId, int currentMatchDay){
        return new CompetitionExternalResponse(
                1L,
                "Premier League",
                "PL",
                "LEAGUE",
                "emblem.png",
                buildExternalSeasonResponse(seasonId, currentMatchDay)
        );
    }

    public static CompetitionSyncContext buildCsc(Competition competition){
        CompetitionSyncContext csc = new CompetitionSyncContext();

        csc.setCompetition(competition);

        return csc;
    }

    public static CompetitionTeamExternalResponse buildExternalTeam(Long externalId, String name){

        return new CompetitionTeamExternalResponse(
                externalId, name, name, name.substring(0, 3).toUpperCase(),
                "crest.png"
        );
    }

    public static CompetitionTeamsExternalResponse buildExternalTeams(List<CompetitionTeamExternalResponse> teams){
        return new CompetitionTeamsExternalResponse(teams);
    }

    // Standings
    public static TableExternalResponse buildTableEntry(
            Long teamExternalId,
            String teamName,
            Integer position,
            Integer points,
            Integer goalsFor,
            Integer goalsAgainst,
            Integer playedGames){
        TeamExternalResponse teamExternal = new TeamExternalResponse(
                teamExternalId, teamName, teamName, teamName.substring(0, 3).toUpperCase(), "crest.png"
        );

        return new TableExternalResponse(
                position,
                teamExternal,
                playedGames,
                "WWDLW",
                6,
                2,
                2,
                points,
                goalsFor,
                goalsAgainst,
                goalsFor - goalsAgainst
        );
    }

    public static StandingsExternalResponse buildExternalStandings(List<TableExternalResponse> table){
        TablesExternalResponse totalTable = new TablesExternalResponse("TOTAL", table);

        return new StandingsExternalResponse(List.of(totalTable));
    }

    public static MatchExternalResponse buildExternalMatch(Long id, String status, Long homeId, Long awayId){

        TeamExternalResponse home = new TeamExternalResponse(homeId, "Arsenal", "ARS", "ARS", "crest.png");

        TeamExternalResponse away = new TeamExternalResponse(awayId, "Chelsea", "CHE", "CHE", "crest.png");

        ScoreExternalResponse score = new ScoreExternalResponse("HOME_TEAM", new TimeExternalResponse(2, 1));

        SeasonExternalResponse season = new SeasonExternalResponse(
                LocalDate.of(2024, 8, 1),
                LocalDate.of(2025, 5, 1),
                38
        );

        return new MatchExternalResponse(id, LocalDateTime.now(), status, 1, "REGULAR_SEASON", home, away, score, season);
    }

    public static MatchesExternalResponse buildExternalMatches(List<MatchExternalResponse> matches){
        return new MatchesExternalResponse(matches);
    }

    public static MatchesExternalResponse emptyMatches(){
        return new MatchesExternalResponse(List.of());
    }


}
