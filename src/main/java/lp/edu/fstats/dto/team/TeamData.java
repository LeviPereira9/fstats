package lp.edu.fstats.dto.team;

public record TeamData(//Home //Away
        Long teamId,
        Integer matchDay,
        Integer goalsFor,
        Integer goalsAgainst
) {

    public boolean lessThan(Integer otherMatchDay){
        return matchDay < otherMatchDay;
    }

    public boolean isOn(Long teamId, Integer matchDay){
        return this.teamId.equals(teamId) && this.lessThan(matchDay);
    }

}
