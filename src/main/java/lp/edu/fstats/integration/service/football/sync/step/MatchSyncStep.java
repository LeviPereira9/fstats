package lp.edu.fstats.integration.service.football.sync.step;

import lombok.RequiredArgsConstructor;
import lp.edu.fstats.integration.client.FootballApiClient;
import lp.edu.fstats.integration.dto.matches.MatchesExternalResponse;
import lp.edu.fstats.integration.dto.matches.match.MatchExternalResponse;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.model.match.Match;
import lp.edu.fstats.model.team.Team;
import lp.edu.fstats.service.competition.CompetitionService;
import lp.edu.fstats.service.match.MatchService;
import lp.edu.fstats.integration.service.football.sync.context.CompetitionSyncContext;
import lp.edu.fstats.integration.service.football.sync.context.TeamSyncContext;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchSyncStep {

    private final FootballApiClient footballApiClient;

    private final CompetitionService competitionService;
    private final MatchService matchService;

    public void sync(CompetitionSyncContext csc, TeamSyncContext tsc){

        this.sync(csc.getCompetition(), tsc.getTeams(), csc.getSeason(), true);
    }

    private void sync(Competition competition, List<Team> teams, Year season, boolean firstCall){

        int matchDay;

        if(firstCall){
            matchDay = competition.getLastFinishedMatchDay() + 1;
        } else {
            matchDay = competition.getCurrentMatchDay();
        }

        MatchesExternalResponse externalMatches = footballApiClient
                .getCurrentMatches(competition.getCode(), season, matchDay);


        if(externalMatches.matches().isEmpty()){

            competition.decrementMatchDay();

            if(competition.isFinished()){
                competition.setStatus("Finalizada");
            }

            competitionService.saveCompetition(competition);
            return; // Não têm mais matchs
        }

        boolean matchDayFinished = externalMatches.allMatchesFinished();

        if(matchDayFinished){
            competition.incrementLastFinishedMatchDay();
        }

        List<Long> externalMatchIds = externalMatches.getMatchesExternalIds();

        Map<Long, Team> mapTeams = teams.stream().collect(Collectors.toMap(
                Team::getExternalId,
                Function.identity()
        ));

        Map<Long, Match> mapMatches = matchService.findAllByExternalId(externalMatchIds);

        List<MatchExternalResponse> matches = externalMatches.matches();

        List<Match> matchesToSave = new ArrayList<>();

        for(MatchExternalResponse match : matches){

            Match currentMatch = this.getMatch(
                    mapMatches.get(match.id()),
                    match);

            Team homeTeam = mapTeams.get(match.getHomeTeamExternalId());

            Team awayTeam = mapTeams.get(match.getAwayTeamExternalId());

            if(currentMatch.getHomeTeam() == null){
                currentMatch.setHomeTeam(homeTeam);
            }

            if(currentMatch.getAwayTeam() == null){
                currentMatch.setAwayTeam(awayTeam);
            }

            if(currentMatch.getCompetition() == null){
                currentMatch.setCompetition(competition);
            }

            matchesToSave.add(currentMatch);
        }

        matchService.saveAll(matchesToSave);
        competitionService.saveCompetition(competition);

        if (competition.isAheadByTwoMatchDays()) {
            competition.incrementMatchDay();
            this.sync(competition, teams, season, false);
        }
    }

    private Match getMatch(Match currentMatch, MatchExternalResponse externalMatch) {

        if(currentMatch == null) return externalMatch.toModel();

        return externalMatch.update(currentMatch);
    }
}
