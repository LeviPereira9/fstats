package lp.edu.fstats.integration.dto.matches;


import lp.edu.fstats.model.team.Team;

public record TeamExternalResponse(
        Integer id,
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

}