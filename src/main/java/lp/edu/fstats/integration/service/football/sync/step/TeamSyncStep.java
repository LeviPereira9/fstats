package lp.edu.fstats.integration.service.football.sync.step;

import lombok.RequiredArgsConstructor;
import lp.edu.fstats.integration.client.FootballApiClient;
import lp.edu.fstats.integration.dto.teams.CompetitionTeamExternalResponse;
import lp.edu.fstats.integration.dto.teams.CompetitionTeamsExternalResponse;
import lp.edu.fstats.model.competition.CompetitionTeam;
import lp.edu.fstats.model.team.Team;
import lp.edu.fstats.repository.competition.CompetitionTeamRepository;
import lp.edu.fstats.repository.team.TeamRepository;
import lp.edu.fstats.integration.service.football.sync.context.CompetitionSyncContext;
import lp.edu.fstats.integration.service.football.sync.context.TeamSyncContext;
import lp.edu.fstats.service.team.TeamService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TeamSyncStep {

    private final TeamService teamService;

    private final FootballApiClient footballApiClient;

    private final CompetitionTeamRepository competitionTeamRepository;
    private final TeamRepository teamRepository;

    public TeamSyncContext sync(CompetitionSyncContext csc){
        TeamSyncContext teamSyncContext = new TeamSyncContext();

        List<CompetitionTeam> teams = competitionTeamRepository.findAllByCompetitionId(csc.getId());

        if(teams.isEmpty()){

            CompetitionTeamsExternalResponse externalTeams = footballApiClient.getCurrentTeams(csc.getCode(), csc.getSeason());

            List<Long> externalTeamsIds = externalTeams.getExternalIds();

            Map<Long, Team> mapTeams = teamService.findAllByExternalId(externalTeamsIds);

            List<Team> teamsAlreadySaved = new ArrayList<>();
            List<Team> teamsToSave = new ArrayList<>();
            List<CompetitionTeam> competitionTeamsToSave = new ArrayList<>();

            for(CompetitionTeamExternalResponse team: externalTeams.teams()){
                boolean teamAlreadyExists = mapTeams.containsKey(team.id());

                if(teamAlreadyExists){
                    teamsAlreadySaved.add(mapTeams.get(team.id()));
                    continue;
                }


                teamsToSave.add(team.toModel());
            }

            List<Team> teamsSaved = teamRepository.saveAll(teamsToSave);

            for(Team team: teamsToSave){
                CompetitionTeam competitionTeam = new CompetitionTeam();
                competitionTeam.setTeam(team);
                competitionTeam.setCompetition(csc.getCompetition());

                competitionTeamsToSave.add(competitionTeam);
            }

            competitionTeamRepository.saveAll(competitionTeamsToSave);

            teamsSaved.addAll(teamsAlreadySaved);

            teamSyncContext.setTeams(teamsSaved);
        } else {
            teamSyncContext.setTeams(teams.stream().map(CompetitionTeam::getTeam).toList());
        }

        return teamSyncContext;
    }
}
