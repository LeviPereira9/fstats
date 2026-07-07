package lp.edu.fstats.factory.entity;

import lp.edu.fstats.model.team.Team;

public class TeamTestFactory {

    public static Team buildTeam(Long id, Long externalId, String name){
        Team team = new Team();
        team.setId(id);
        team.setExternalId(externalId);
        team.setName(name);
        team.setShortName(name);
        team.setTla(name.substring(0, 3).toUpperCase());
        team.setCrest("crest.png");

        return team;
    }

}
