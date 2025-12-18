package lp.edu.fstats.doc.annotations.competition;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Consultar classificação da competição",
        description = "Retorna a classificação atual da competição."
)
@ApiResponse
public @interface DocGetStandings {}
