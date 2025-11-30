package lp.edu.fstats.integration.service.football;

import lombok.RequiredArgsConstructor;
import lp.edu.fstats.integration.client.FootballApiClient;
import lp.edu.fstats.integration.dto.matches.MatchExternalResponse;
import lp.edu.fstats.integration.dto.matches.MatchesExternalResponse;
import lp.edu.fstats.model.match.Match;
import lp.edu.fstats.model.team.Team;
import lp.edu.fstats.repository.match.MatchRepository;
import lp.edu.fstats.repository.team.TeamRepository;
import lp.edu.fstats.service.match.MatchService;
import lp.edu.fstats.service.team.TeamService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FootballSyncService {

    private final FootballApiClient footballApiClient;
    private final MatchService matchService;
    private final TeamService teamService;
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;


    public void Manage(){
        MatchesExternalResponse externalMatches = footballApiClient.getCurrentMatches();

        List<Integer> externalTeamsIds = externalMatches.getTeamsExternalIds();

        List<Integer> externalMatchIds = externalMatches.getMatchesExternalIds();

        // Oq vai acontecer?
        // Verificamos se os times/partidas/pontuações existem.
        // Se existe, a gente pega pra atualizar
        // Se não existe, vai ser criado.
        Map<Integer, Team> mapTeams = teamService.findAllByExternalId(externalTeamsIds);
        Map<Integer, Match> mapMatches = matchService.findAllByExternalId(externalMatchIds);

        List<MatchExternalResponse> matches = externalMatches.matches();

        List<Team> teamsToSave = new ArrayList<>();
        List<Match> matchesToSave = new ArrayList<>();

        for(MatchExternalResponse match : matches){
            Match currentMatch = mapMatches.getOrDefault(
                    match.id(),
                    match.toModel());

            Team homeTeam = mapTeams.getOrDefault(
                    match.getHomeTeamExternalId(),
                    match.homeTeamToModel());

            Team awayTeam = mapTeams.getOrDefault(
                    match.getAwayTeamExternalId(),
                    match.awayTeamToModel());

            if(currentMatch.getHomeTeam() == null){
                currentMatch.setHomeTeam(homeTeam);
            }

            if(currentMatch.getAwayTeam() == null){
                currentMatch.setAwayTeam(awayTeam);
            }

            teamsToSave.addAll(List.of(homeTeam, awayTeam));
            matchesToSave.add(currentMatch);
        }

        teamRepository.saveAll(teamsToSave);
        matchRepository.saveAll(matchesToSave);
    }

}
