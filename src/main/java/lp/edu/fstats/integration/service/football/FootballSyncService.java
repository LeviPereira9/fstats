package lp.edu.fstats.integration.service.football;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.edu.fstats.integration.client.FootballApiClient;
import lp.edu.fstats.integration.dto.competition.CompetitionExternalResponse;
import lp.edu.fstats.integration.dto.matches.match.MatchExternalResponse;
import lp.edu.fstats.integration.dto.matches.MatchesExternalResponse;
import lp.edu.fstats.integration.dto.matches.match.TeamExternalResponse;
import lp.edu.fstats.integration.dto.standings.StandingsExternalResponse;
import lp.edu.fstats.integration.dto.standings.TableExternalResponse;
import lp.edu.fstats.integration.dto.standings.TablesExternalResponse;
import lp.edu.fstats.model.avarages.Averages;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.model.match.Match;
import lp.edu.fstats.model.standings.Standings;
import lp.edu.fstats.model.team.Team;
import lp.edu.fstats.repository.averages.AveragesRepository;
import lp.edu.fstats.repository.competition.CompetitionRepository;
import lp.edu.fstats.repository.match.MatchRepository;
import lp.edu.fstats.repository.standings.StandingsRepository;
import lp.edu.fstats.repository.team.TeamRepository;
import lp.edu.fstats.service.averages.AveragesService;
import lp.edu.fstats.service.match.MatchService;
import lp.edu.fstats.service.probability.ProbabilityServiceImpl;
import lp.edu.fstats.service.standings.StandingsService;
import lp.edu.fstats.service.team.TeamService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FootballSyncService {

    private final String STATUS = "Em andamento";
    private final Integer MAX_REQUESTS = 10;
    private final Year SEASON = Year.now();

    private final FootballApiClient footballApiClient;
    private final MatchService matchService;
    private final TeamService teamService;
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final CompetitionRepository competitionRepository;
    private final StandingsRepository standingsRepository;
    private final StandingsService standingsService;
    private final ProbabilityServiceImpl probabilityServiceImpl;
    private final AveragesService averagesService;
    private final AveragesRepository averagesRepository;

    @Transactional
    public void manageCompetitionMatches(String code, int requests, Competition recursiveCompetition){

        boolean firstCall = recursiveCompetition == null;
        Integer MATCHDAY;
        Competition competition;

        // TODO: Finalizar uma competition.

        if(requests > MAX_REQUESTS){
            return;// Em tese é pra esperar 1 minuto.
        }

        // Ta, oq tem que acontecer.
        // Se for recursivo, ele vai pular pq ja foi feito a coisinha
        // Se não for, temos que buscar no banco de dados e fazer uma requisição para atualizar.
        // Se não tiver no bd, a req cria se tiver atualiza.
        if(recursiveCompetition == null){

            Competition savedCompetition = competitionRepository.findByCodeAndStatus(code)
                    .orElse(null);

            CompetitionExternalResponse externalCompetition = footballApiClient.getCurrentCompetition(code);

            competition = savedCompetition == null ? externalCompetition.toModel() : externalCompetition.update(savedCompetition);

            requests++;

        } else {
            competition = recursiveCompetition;
        }

        //Oq queremos realmente aqui?
        // MatchDay deve começar da ultima rodada que foi finalizada, pq ai ela pode atualizar e vida que segue.
        // Mas também queremos ter algumas partidas que nem começaram ainda já registradas.
        // Então estamos em um dilema.
        // Pois tem que ser:
        // Se estiver com as 3 no futuro então > LastFinished.
        // Se não > currentMatchDay

        if(firstCall){
            MATCHDAY = competition.getLastFinishedMatchDay() + 1;
        } else {
            MATCHDAY = competition.getCurrentMatchDay();
        }

        MatchesExternalResponse externalMatches = footballApiClient
                .getCurrentMatches(code, SEASON, MATCHDAY);

        requests++;

        if(externalMatches.matches().isEmpty()){
            // TODO: Adicionar mais um DTO para pegar a SEASON das matches, se tiver WINNER significa que já acabou.
            return; // Não têm mais matchs
        }

        boolean matchDayFinished = externalMatches.allMatchesFinished();

        if(matchDayFinished){
            competition.incrementLastFinishedMatchDay();
        }

        List<Long> externalTeamsIds = externalMatches.getTeamsExternalIds();

        List<Long> externalMatchIds = externalMatches.getMatchesExternalIds();

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

        competition = competitionRepository.save(competition);
        teamRepository.saveAll(teamsToSave);
        matchRepository.saveAll(matchesToSave);

        // Vamos lá, também quero salvar algumas rodadas a frente sabe? que vai acontecer.
        // Então ExternalCurrentMatchDay + 2, quero sempre estar +2 rodadas já salvas, sabe?


        if (MATCHDAY < competition.getExternalCurrentMatchDay() + 2 && requests <= MAX_REQUESTS) {
            competition.incrementMatchDay();
            manageCompetitionMatches(code, requests, competition);
        }

    }

    private Team getTeam(Team currentTeam, TeamExternalResponse externalTeam) {
        if(currentTeam == null) return externalTeam.toModel();

        return currentTeam;
    }

    private Match getMatch(Match currentMatch, MatchExternalResponse externalMatch) {

        if(currentMatch == null) return externalMatch.toModel();

        return externalMatch.update(currentMatch);
    }

    @Transactional
    public void manageStandings(String code, Integer requests){

        //TODO: Verificar se realmente precisa atualizar

        Competition competition = competitionRepository.findByCodeAndStatus(code)
                .orElse(null);

        if(competition == null){
            return;
        }

        StandingsExternalResponse externalStandings = footballApiClient.getCurrentTotalStandings(code, SEASON);

        requests++;

        Map<Long, Standings> currentStandingsMappedByTeamId = standingsService.findAllByCompetitionId(competition.getId());

        Map<String, TablesExternalResponse> mapTables = externalStandings.getTables();

        TablesExternalResponse fullTable = mapTables.get("TOTAL");



        if(fullTable == null){
            return;
        }

        List<Long> externalTeamsIds = fullTable.getTeamExternalIds();

        Map<Long, Team> mapTeams = teamService.findAllByExternalId(externalTeamsIds);

        List<Standings> standingsToSave = new ArrayList<>();



        for(TableExternalResponse table: fullTable.table()){
            boolean alreadyExists = currentStandingsMappedByTeamId.containsKey(table.getTeamExternalId());
            Standings standings;

            if(alreadyExists){
                standings = currentStandingsMappedByTeamId.get(table.getTeamExternalId());
                table.update(standings);
            } else {
                standings = table.toModel(mapTeams.get(table.getTeamExternalId()), competition);
            }

            standingsToSave.add(standings);
        }

        Map<Long, Averages> currentAveragesMappedByTeamId = averagesService.findAllByCompetitionId(competition.getId());

        List<Averages> averagesToSave = new ArrayList<>();

        TablesExternalResponse homeTable = mapTables.get("HOME");
        TablesExternalResponse awayTable = mapTables.get("AWAY");

        Map<Long, TableExternalResponse> mapHomeTableByTeam = homeTable.mapScoresByTeam();
        Map<Long, TableExternalResponse> mapAwayTableByTeam = awayTable.mapScoresByTeam();

        for (Team team : mapTeams.values()) {

            Long externalTeamId = team.getExternalId();

            boolean hasHomeScore = mapHomeTableByTeam.containsKey(externalTeamId);
            boolean hasAwayScore = mapAwayTableByTeam.containsKey(externalTeamId);

            boolean teamAlreadyHasAverages = currentAveragesMappedByTeamId.containsKey(team.getId());

            if (!hasHomeScore && !hasAwayScore) {
                continue;
            }

            TableExternalResponse home = mapHomeTableByTeam.get(externalTeamId);
            TableExternalResponse away = mapAwayTableByTeam.get(externalTeamId);

            Averages averages = currentAveragesMappedByTeamId
                    .getOrDefault(team.getId(), new Averages());

            if(!teamAlreadyHasAverages){
                averages.setTeam(team);
                averages.setCompetition(competition);
            }

            if (home != null) {
                averages.setAvgGoalsForHome(
                        calculateAverage(home.goalsFor(), home.playedGames())
                );

                averages.setAvgGoalsAgainstHome(
                        calculateAverage(home.goalsAgainst(), home.playedGames())
                );
            }

            if (away != null) {
                averages.setAvgGoalsForAway(
                        calculateAverage(away.goalsFor(), away.playedGames())
                );

                averages.setAvgGoalsAgainstAway(
                        calculateAverage(away.goalsAgainst(), away.playedGames())
                );
            }

            averagesToSave.add(averages);
        }

        averagesRepository.saveAll(averagesToSave);
        standingsRepository.saveAll(standingsToSave);
    }

    private BigDecimal calculateAverage(Integer goals, Integer games) {
        if (games == null || games == 0) return BigDecimal.ZERO;

        return BigDecimal.valueOf(goals)
                .divide(BigDecimal.valueOf(games), 2, RoundingMode.HALF_UP);
    }


    public void fuckingManageProbability(){
        probabilityServiceImpl.manageProbability(null, null, null);
    }

}
