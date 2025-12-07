package lp.edu.fstats.service.probability;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.edu.fstats.dto.probability.PoissonProbabilityData;
import lp.edu.fstats.dto.team.MatchesData;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.model.match.Match;
import lp.edu.fstats.model.probability.Probability;
import lp.edu.fstats.repository.competition.CompetitionRepository;
import lp.edu.fstats.repository.match.MatchRepository;
import lp.edu.fstats.repository.probability.ProbabilityRepository;
import lp.edu.fstats.service.poisson.PoissonService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProbabilityServiceImpl implements ProbabilityService {
    private final MatchRepository matchRepository;
    private final ProbabilityRepository probabilityRepository;
    private final CompetitionRepository competitionRepository;
    private final PoissonService poissonService;


    //TODO: Melhorar nomeação
    //TODO: Não salvar a cada recursividade
    @Transactional
    public void manageProbability(
            Competition recursiveCompetition,
            Integer recursiveMaxMatchDay,
            List<Match> recursiveMatches
            ){

        Competition competition;

        if(recursiveCompetition == null){
            competition = competitionRepository.findById(34L)
                    .orElse(null);
        } else {
            competition = recursiveCompetition;
        }

        if(competition == null || competition.getCurrentMatchDay() < 4){
            return;
        }

        Integer maxMatchDay = recursiveMaxMatchDay == null ? probabilityRepository.findMaxMatchday(competition.getId()) : recursiveMaxMatchDay;

        Integer startCount = maxMatchDay == null ? 3 : maxMatchDay + 1;

        if(startCount > competition.getLastFinishedMatchDay()){
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

        if(startCount + 1 < competition.getCurrentMatchDay()){
            manageProbability(
                    competition,
                    startCount + 1,
                    matches
            );
        }

        probabilityRepository.saveAll(probabilitiesToSave);
    }

}
