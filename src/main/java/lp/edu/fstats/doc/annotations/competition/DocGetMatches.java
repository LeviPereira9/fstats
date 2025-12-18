package lp.edu.fstats.doc.annotations.competition;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Consultar partidas da competição",
        description = "Retorna as partidas e as probabilidades de gol de uma competição, permitindo filtrar pela rodada."
)
@ApiResponse
public @interface DocGetMatches {}
