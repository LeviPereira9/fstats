package lp.edu.fstats.dto.team;

import lp.edu.fstats.model.match.Match;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public record MatchesData(
        List<TeamData> homeTeam,
        List<TeamData> awayTeam
) {
    public BigDecimal calculateMatchLambda(Long homeTeamId, Long awayTeamId, Integer matchDay){
        int homeTeamScoresAtHome = 0;
        int homeTeamConcernedAtHome = 0;

        int awayTeamScoresAtAway = 0;
        int awayTeamConcernedAtAway = 0;

        BigDecimal homeTeamAvgScoredAtHome;
        BigDecimal homeTeamAvgConcernedAtHome;

        BigDecimal awayTeamAvgScoredAtAway;
        BigDecimal awayTeamAvgConcernedAtAway;

        BigDecimal lambdaHomeTeam;
        BigDecimal lambdaAwayTeam;

        int homeTeamCount = 0;
        int awayTeamCount = 0;

        for(TeamData teamData :  homeTeam) {
            if(teamData.isOn(homeTeamId, matchDay)){
                homeTeamScoresAtHome += teamData.goalsFor();
                homeTeamConcernedAtHome += teamData.goalsAgainst();
                homeTeamCount++;
            }

        }

        for(TeamData teamData :  awayTeam) {
            if(teamData.isOn(awayTeamId, matchDay)){
                awayTeamScoresAtAway += teamData.goalsFor();
                awayTeamConcernedAtAway += teamData.goalsAgainst();
                awayTeamCount++;
            }
        }

        homeTeamAvgScoredAtHome = BigDecimal.valueOf(homeTeamScoresAtHome)
                .divide(BigDecimal.valueOf(homeTeamCount), 4, RoundingMode.HALF_UP);


        homeTeamAvgConcernedAtHome = BigDecimal.valueOf(homeTeamConcernedAtHome)
                .divide(BigDecimal.valueOf(homeTeamCount), 4, RoundingMode.HALF_UP);


        awayTeamAvgScoredAtAway = BigDecimal.valueOf(awayTeamScoresAtAway)
                .divide(BigDecimal.valueOf(awayTeamCount), 4, RoundingMode.HALF_UP);

        awayTeamAvgConcernedAtAway = BigDecimal.valueOf(awayTeamConcernedAtAway)
                .divide(BigDecimal.valueOf(awayTeamCount), 4, RoundingMode.HALF_UP);

        lambdaHomeTeam = (homeTeamAvgScoredAtHome.add(awayTeamAvgConcernedAtAway))
                .divide(BigDecimal.valueOf(2), 4, RoundingMode.HALF_UP);

        lambdaAwayTeam = (awayTeamAvgScoredAtAway.add(homeTeamAvgConcernedAtHome))
                .divide(BigDecimal.valueOf(2), 4, RoundingMode.HALF_UP);

        return lambdaHomeTeam.add(lambdaAwayTeam);
    }

    public static MatchesData toData(List<Match> matches){
        List<TeamData> homeTeam = new ArrayList<>();
        List<TeamData> awayTeam = new ArrayList<>();

        for(Match match : matches){
            homeTeam.add(new TeamData(
                    match.getHomeTeam().getId(),
                    match.getMatchDay(),
                    match.getHomeGoals(),
                    match.getAwayGoals()
            ));

            awayTeam.add(new TeamData(
                    match.getAwayTeam().getId(),
                    match.getMatchDay(),
                    match.getAwayGoals(),
                    match.getHomeGoals()
            ));
        }

        return new MatchesData(homeTeam, awayTeam);
    }
}
