package lp.edu.fstats.service.auto;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.edu.fstats.dto.probability.PoissonProbabilityData;
import lp.edu.fstats.dto.team.MatchesData;
import lp.edu.fstats.integration.client.FootballApiClient;
import lp.edu.fstats.integration.dto.competition.CompetitionExternalResponse;
import lp.edu.fstats.integration.dto.matches.MatchesExternalResponse;
import lp.edu.fstats.integration.dto.matches.match.MatchExternalResponse;
import lp.edu.fstats.integration.dto.standings.StandingsExternalResponse;
import lp.edu.fstats.integration.dto.standings.TableExternalResponse;
import lp.edu.fstats.integration.dto.standings.TablesExternalResponse;
import lp.edu.fstats.integration.dto.teams.CompetitionTeamExternalResponse;
import lp.edu.fstats.integration.dto.teams.CompetitionTeamsExternalResponse;
import lp.edu.fstats.model.avarages.Averages;
import lp.edu.fstats.model.code.Code;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.model.competition.CompetitionTeam;
import lp.edu.fstats.model.match.Match;
import lp.edu.fstats.model.probability.Probability;
import lp.edu.fstats.model.standings.Standings;
import lp.edu.fstats.model.team.Team;
import lp.edu.fstats.repository.averages.AveragesRepository;
import lp.edu.fstats.repository.code.CodeRepository;
import lp.edu.fstats.repository.competition.CompetitionRepository;
import lp.edu.fstats.repository.competition.CompetitionTeamRepository;
import lp.edu.fstats.repository.match.MatchRepository;
import lp.edu.fstats.repository.probability.ProbabilityRepository;
import lp.edu.fstats.repository.standings.StandingsRepository;
import lp.edu.fstats.repository.team.TeamRepository;
import lp.edu.fstats.service.averages.AveragesService;
import lp.edu.fstats.service.competition.CompetitionService;
import lp.edu.fstats.service.match.MatchService;
import lp.edu.fstats.service.poisson.PoissonService;
import lp.edu.fstats.service.probability.ProbabilityService;
import lp.edu.fstats.service.standings.StandingsService;
import lp.edu.fstats.service.team.TeamService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AutoService {

    private final int MAX_REQUESTS = 10;
    private final Year SEASON = Year.now();
    private final CompetitionRepository competitionRepository;
    private final FootballApiClient footballApiClient;

    private final MatchService matchService;
    private final TeamService teamService;
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final CompetitionService competitionService;
    private final StandingsRepository standingsRepository;
    private final StandingsService standingsService;
    private final ProbabilityService probabilityService;
    private final AveragesService averagesService;
    private final AveragesRepository averagesRepository;
    private final CompetitionTeamRepository competitionTeamRepository;
    private final ProbabilityRepository probabilityRepository;
    private final PoissonService poissonService;
    private final CodeRepository codeRepository;

    //@Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void sync(){
        List<Code> codes = codeRepository.findAll();
        AtomicInteger requests = new AtomicInteger(0);

        for(Code code : codes){
            try{
                manageEverything(code.getCode(), requests);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void manageEverything(String code, AtomicInteger requests){
        checkRateLimit(requests);

        Competition competition = this.getCompetition(code, requests);

        if(competition == null){
            return;
        }

        List<Team> teams = this.getTeams(competition, requests);

        this.syncMatches(competition, requests, teams, true);

        this.syncTables(competition, requests);

        this.manageProbability(competition, null, null);
    }


    private void checkRateLimit(AtomicInteger requests){
        if(requests.get() >= MAX_REQUESTS) {
            try {
                Thread.sleep(60000);
                requests.set(0);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private Competition getCompetition(String code, AtomicInteger requests){

        this.checkRateLimit(requests);
        requests.incrementAndGet();

        Competition competition;

        Competition savedCompetition = competitionRepository.findByCodeAndStatus(code)
                .orElse(null);

        CompetitionExternalResponse externalCompetition = footballApiClient.getCurrentCompetition(code);

        if(savedCompetition == null){
            boolean isFinished = competitionRepository.existsByExternalId(externalCompetition.id());

            if(isFinished){
                return null;
            }

            competition = externalCompetition.toModel();

        } else {
            competition = externalCompetition.update(savedCompetition);
        }

        return competitionRepository.save(competition);
    }

    public List<Team> getTeams(Competition competition, AtomicInteger requests) {
        List<CompetitionTeam> teams = competitionTeamRepository.findAllByCompetitionId(competition.getId());

        if(teams.isEmpty()){
            CompetitionTeamsExternalResponse externalTeams = footballApiClient.getCurrentTeams(competition.getCode(), SEASON);

            this.checkRateLimit(requests);
            requests.incrementAndGet();

            List<Long> externalTeamsIds = externalTeams.getExternalIds();

            Map<Long, Team> mapTeams = teamService.findAllByExternalId(externalTeamsIds);

            List<Team> teamsToSave = new ArrayList<>();
            List<CompetitionTeam> competitionTeamsToSave = new ArrayList<>();

            for(CompetitionTeamExternalResponse team: externalTeams.teams()){
                boolean teamAlreadyExists = mapTeams.containsKey(team.id());

                if(teamAlreadyExists) continue;


                teamsToSave.add(team.toModel());
            }

            List<Team> teamsSaved = teamRepository.saveAll(teamsToSave);

            for(Team team: teamsToSave){
                CompetitionTeam competitionTeam = new CompetitionTeam();
                competitionTeam.setTeam(team);
                competitionTeam.setCompetition(competition);

                competitionTeamsToSave.add(competitionTeam);
            }

            competitionTeamRepository.saveAll(competitionTeamsToSave);

            return teamsSaved;
        } else {

            return teams.stream().map(CompetitionTeam::getTeam).toList();
        }
    }

    private void syncMatches(Competition competition, AtomicInteger requests, List<Team> teams, boolean firstCall) {
        int matchDay;

        this.checkRateLimit(requests);
        requests.incrementAndGet();

        if(firstCall){
            matchDay = competition.getLastFinishedMatchDay() + 1;
        } else {
            matchDay = competition.getCurrentMatchDay();
        }

        MatchesExternalResponse externalMatches = footballApiClient
                .getCurrentMatches(competition.getCode(), SEASON, matchDay);


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

        matchRepository.saveAll(matchesToSave);
        competitionService.saveCompetition(competition);

        if (competition.isAheadByTwoMatchDays() && requests.get() <= MAX_REQUESTS) {
            competition.incrementMatchDay();
            syncMatches(competition, requests, teams, false);
        }
    }

    private Match getMatch(Match currentMatch, MatchExternalResponse externalMatch) {

        if(currentMatch == null) return externalMatch.toModel();

        return externalMatch.update(currentMatch);
    }

    private void syncTables(Competition competition, AtomicInteger requests) {

        this.checkRateLimit(requests);
        requests.incrementAndGet();

        StandingsExternalResponse externalStandings = footballApiClient.getCurrentTotalStandings(competition.getCode(), SEASON);

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

    public void manageProbability(
            Competition competition,
            Integer recursiveMaxMatchDay,
            List<Match> recursiveMatches
    ){

        if(competition.getExternalCurrentMatchDay() < 3){
            return;
        }

        Integer maxMatchDay = recursiveMaxMatchDay == null ? probabilityRepository.findMaxMatchday(competition.getId()) : recursiveMaxMatchDay;

        Integer startCount = maxMatchDay == null ? 3 : maxMatchDay + 1;

        if(startCount > competition.getExternalCurrentMatchDay()){
            return;
        }

        List<Match> matches = recursiveMatches == null ? matchRepository.findAllByCompetition_Id(competition.getId()) : recursiveMatches;

        Map<Integer, List<Match>> mapMatchesByMatchDay = matches.stream()
                .collect(Collectors.groupingBy(Match::getMatchDay));

        // Oq eu preciso: Média de Gols dentro e fora de casa de cada time.
        // Para pegar para a próxima partida eu tenho que ter um limitador de até
        // X rodada.
        // Então eu preciso disso: RODADA:TimeId
        // Acredito que deve ter outras formas, mas eu vou criar 2 variáveis que vão carregar
        // Respectivamente os dentro e fora de casa.
        // Matches têm o ID do Time e Nr_Rodada, perfeito então:
        MatchesData matchesData = MatchesData.toData(matches); // <- Com esse aqui a gente tem acesso direto aos time casa/visitante.

        List<Probability> probabilitiesToSave = new ArrayList<>();

        for(Match match : mapMatchesByMatchDay.get(startCount)){

            BigDecimal matchLambda = matchesData.calculateMatchLambda(
                    match.getHomeTeam().getId(),
                    match.getAwayTeam().getId(),
                    startCount
            );

            PoissonProbabilityData poissonProbability = poissonService.calculate(matchLambda);

            Probability probability = new Probability();

            probability.setMatch(match);
            probability.setCompetition(competition);

            probability.setProbabilityOver05(poissonProbability.over05());
            probability.setProbabilityOver15(poissonProbability.over15());
            probability.setProbabilityOver25(poissonProbability.over25());

            probability.setMatchDay(match.getMatchDay());

            probabilitiesToSave.add(probability);
        }

        if(startCount <= competition.getExternalCurrentMatchDay()){
            manageProbability(
                    competition,
                    startCount,
                    matches
            );
        }

        probabilityRepository.saveAll(probabilitiesToSave);
    }


}
