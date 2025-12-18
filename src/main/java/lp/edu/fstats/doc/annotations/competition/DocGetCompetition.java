package lp.edu.fstats.doc.annotations.competition;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Consultar competição",
        description = "Retorna os dados detalhados da competição mais recente identificada pelo código informado."
)

@ApiResponse
public @interface DocGetCompetition {}
