package lp.edu.fstats.doc.annotations.competition;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Consultar médias da competição",
        description = "Retorna as médias estatísticas calculadas para uma competição."
)
@ApiResponse
public @interface DocGetAverages {}
