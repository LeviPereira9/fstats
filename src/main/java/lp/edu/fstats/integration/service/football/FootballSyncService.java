package lp.edu.fstats.integration.service.football;

import lombok.RequiredArgsConstructor;
import lp.edu.fstats.integration.client.FootballApiClient;
import lp.edu.fstats.integration.dto.matches.match.MatchExternalResponse;
import lp.edu.fstats.integration.dto.matches.MatchesExternalResponse;
import lp.edu.fstats.integration.dto.matches.match.TeamExternalResponse;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.model.match.Match;
import lp.edu.fstats.model.team.Team;
import lp.edu.fstats.repository.competition.CompetitionRepository;
import lp.edu.fstats.repository.match.MatchRepository;
import lp.edu.fstats.repository.team.TeamRepository;
import lp.edu.fstats.service.match.MatchService;
import lp.edu.fstats.service.team.TeamService;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FootballSyncService {

    private final String STATUS = "Em andamento";
    private final FootballApiClient footballApiClient;
    private final MatchService matchService;
    private final TeamService teamService;
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final CompetitionRepository competitionRepository;

    public void Manage(){

        Competition competition = competitionRepository.findByCodeAndStatus("PL", STATUS)
                .orElse(null);

        String SEASON;
        Integer MATCHDAY;

        if(competition == null){
            SEASON = Year.now().toString();
            MATCHDAY = 1;
        } else {
            SEASON = "" + competition.getStartDate().getYear();
            MATCHDAY = competition.getCurrentMatchDay();
        }

        MatchesExternalResponse externalMatches = footballApiClient.getCurrentMatches(SEASON, MATCHDAY);

        if(competition == null){
            competition = externalMatches.competitionToModel();
        }

        boolean matchDayFinished = externalMatches.allMatchesFinished();

        if(matchDayFinished){
            competition.incrementMatchDay();
        }

        List<Long> externalTeamsIds = externalMatches.getTeamsExternalIds();

        List<Long> externalMatchIds = externalMatches.getMatchesExternalIds();

        // Oq vai acontecer?
        // Verificamos se os times/partidas/pontuações existem.
        // Se existe, a gente pega pra atualizar
        // Se não existe, vai ser criado.
        Map<Long, Team> mapTeams = teamService.findAllByExternalId(externalTeamsIds);
        Map<Long, Match> mapMatches = matchService.findAllByExternalId(externalMatchIds);

        List<MatchExternalResponse> matches = externalMatches.matches();

        List<Team> teamsToSave = new ArrayList<>();
        List<Match> matchesToSave = new ArrayList<>();

        for(MatchExternalResponse match : matches){
            Match currentMatch = this.getMatch(
                    mapMatches.get(match.id()),
                    match);

            Team homeTeam = this.getTeam(
                    mapTeams.get(match.getHomeTeamExternalId()),
                    match.homeTeam());

            Team awayTeam = this.getTeam(
                    mapTeams.get(match.getAwayTeamExternalId()),
                    match.awayTeam());

            if(currentMatch.getHomeTeam() == null){
                currentMatch.setHomeTeam(homeTeam);
            }

            if(currentMatch.getAwayTeam() == null){
                currentMatch.setAwayTeam(awayTeam);
            }

            if(currentMatch.getCompetition() == null){
                currentMatch.setCompetition(competition);
            }

            teamsToSave.addAll(List.of(homeTeam, awayTeam));
            matchesToSave.add(currentMatch);
        }

        competitionRepository.save(competition);
        teamRepository.saveAll(teamsToSave);
        matchRepository.saveAll(matchesToSave);
    }

    private Team getTeam(Team currentTeam, TeamExternalResponse externalTeam) {
        if(currentTeam == null) return externalTeam.toModel();

        return externalTeam.update(currentTeam);
    }

    private Match getMatch(Match currentMatch, MatchExternalResponse externalMatch) {

        if(currentMatch == null) return externalMatch.toModel();

        return externalMatch.update(currentMatch);
    }

}
