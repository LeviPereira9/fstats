package lp.edu.fstats.factory.entity;

import lp.edu.fstats.model.competition.Competition;

import java.time.LocalDate;

public class CompetitionTestFactory {

    public static Competition buildCompetition(String code){
        Competition c = new Competition();
        c.setId(1L);
        c.setCode(code);
        c.setName("Premier League");
        c.setType("LEAGUE");
        c.setEmblem("emblem.png");
        c.setStartDate(LocalDate.of(2024, 8, 1));
        c.setEndDate(LocalDate.of(2025, 5, 1));

        return c;
    }

}
