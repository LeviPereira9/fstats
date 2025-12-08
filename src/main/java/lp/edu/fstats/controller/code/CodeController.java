package lp.edu.fstats.controller.code;

import lombok.RequiredArgsConstructor;
import lp.edu.fstats.dto.code.CodeRequest;
import lp.edu.fstats.dto.code.CodeResponse;
import lp.edu.fstats.dto.code.CodesResponse;
import lp.edu.fstats.response.normal.Response;
import lp.edu.fstats.service.code.CodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/${api.prefix}/competition/code")
@RequiredArgsConstructor
public class CodeController {

    private final CodeService codeService;

    @GetMapping
    public ResponseEntity<Response<CodesResponse>> getAllCodes(){
        CodesResponse data = codeService.getAllCodes();

        Response<CodesResponse> response = Response.<CodesResponse>builder()
                .operation("a")
                .code(HttpStatus.OK.value())
                .data(data)
                .message("Competições ativas encontradas com sucesso.")
                .build();

        return ResponseEntity.ok(response);
    }


    @PostMapping
    public ResponseEntity<Response<CodeResponse>> createCode(
            @RequestBody CodeRequest request
            ){
        CodeResponse data = codeService.createCode(request);
        int code = HttpStatus.CREATED.value();

        Response<CodeResponse> response = Response.<CodeResponse>builder()
                .operation("b")
                .code(code)
                .data(data)
                .message("Competição adicionada com sucesso.")
                .build();

        return ResponseEntity.status(code).body(response);
    }

    @DeleteMapping("/{competitionId}")
    public ResponseEntity<Response<Void>> deleteCode(
            @PathVariable Integer competitionId
    ){
        codeService.deleteCode(competitionId);

        Response<Void> response = Response.<Void>builder()
                .operation("d")
                .code(HttpStatus.OK.value())
                .message("Competição desativada com sucesso.")
                .build();

        return ResponseEntity.ok(response);
    }
}
