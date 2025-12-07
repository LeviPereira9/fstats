package lp.edu.fstats.controller.competition;

import lombok.RequiredArgsConstructor;
import lp.edu.fstats.dto.averages.AveragesResponse;
import lp.edu.fstats.dto.competition.CompetitionResponse;
import lp.edu.fstats.dto.match.MatchesResponse;
import lp.edu.fstats.dto.standings.StandingsResponse;
import lp.edu.fstats.response.normal.Response;
import lp.edu.fstats.service.averages.AveragesService;
import lp.edu.fstats.service.competition.CompetitionService;
import lp.edu.fstats.service.match.MatchService;
import lp.edu.fstats.service.standings.StandingsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/${api.prefix}/competition")
@RequiredArgsConstructor
public class CompetitionController {
    private final CompetitionService competitionService;
    private final MatchService matchService;
    private final StandingsService standingsService;
    private final AveragesService averagesService;

    @GetMapping("/{code}")
    public ResponseEntity<Response<CompetitionResponse>> getCompetition(
            @PathVariable String code
    ){
        CompetitionResponse data = competitionService.getCompetition(code);

        Response<CompetitionResponse> response = Response
                .<CompetitionResponse>builder()
                .operation("Competition.GetCompetition")
                .code(HttpStatus.OK.value())
                .data(data)
                .message("Competição encontrada com sucesso.")
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{competitionId}/matches")
    public ResponseEntity<Response<MatchesResponse>> getMatches(
            @PathVariable Long competitionId,
            @RequestParam(defaultValue = "1") Integer matchday
    ){
        MatchesResponse data = matchService.getMatches(competitionId, matchday);

        Response<MatchesResponse> response = Response.<MatchesResponse>builder()
                .operation("Competition.Matches.GetMatches")
                .code(HttpStatus.OK.value())
                .data(data)
                .message("Partidas encontradas com sucesso.")
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{competitionId}/averages")
    public ResponseEntity<Response<AveragesResponse>> getAverages(
            @PathVariable Long competitionId
    ){
        AveragesResponse data = averagesService.findAllByCompetition(competitionId);

        Response<AveragesResponse> response = Response.<AveragesResponse>builder()
                .operation("Competition.Averages.GetAverages")
                .code(HttpStatus.OK.value())
                .data(data)
                .message("Médias encontradas com sucesso.")
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{competitionId}/standings")
    public ResponseEntity<Response<StandingsResponse>> getStandings(
            @PathVariable Long competitionId
    ){
        StandingsResponse data = standingsService.getStandings(competitionId);

        Response<StandingsResponse> response = Response.<StandingsResponse>builder()
                .operation("")
                .code(HttpStatus.OK.value())
                .message("Classificações encontradas com sucesso.")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }


}
