package lp.edu.fstats.integration.dto.teams;

import lp.edu.fstats.model.team.Team;

public record CompetitionTeamExternalResponse(
        Long id,
        String name,
        String shortName,
        String tla,
        String crest

) {

    public Team toModel(){
        Team team = new Team();
        team.setExternalId(id);
        team.setName(name);
        team.setShortName(shortName);
        team.setTla(tla);
        team.setCrest(crest);

        return team;
    }

    public void update(Team target){

        target.setName(name);
        target.setShortName(shortName);
        target.setTla(tla);
        target.setCrest(crest);
    }

}
