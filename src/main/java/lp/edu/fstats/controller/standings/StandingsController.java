package lp.edu.fstats.controller.standings;

import lombok.RequiredArgsConstructor;
import lp.edu.fstats.dto.standings.StandingsResponse;
import lp.edu.fstats.response.normal.Response;
import lp.edu.fstats.service.standings.StandingsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/${api.prefix}/competition/{code}/standings")
@RequiredArgsConstructor
public class StandingsController {

    private final StandingsService standingsService;

    @GetMapping("/{competitionId}")
    public ResponseEntity<Response<StandingsResponse>> getStandings(
            @PathVariable String code,
            @PathVariable Long competitionId
    ){
        StandingsResponse data = standingsService.getStandings(code, competitionId);

        Response<StandingsResponse> response = Response.<StandingsResponse>builder()
                .operation("")
                .code(HttpStatus.OK.value())
                .message("Classificações encontradas com sucesso.")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }

}
