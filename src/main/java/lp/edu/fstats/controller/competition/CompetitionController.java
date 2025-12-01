package lp.edu.fstats.controller.competition;

import lombok.RequiredArgsConstructor;
import lp.edu.fstats.dto.competition.CompetitionResponse;
import lp.edu.fstats.response.normal.Response;
import lp.edu.fstats.service.competition.CompetitionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/${api.prefix}/competition")
@RequiredArgsConstructor
public class CompetitionController {
    private final CompetitionService competitionService;

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
}
