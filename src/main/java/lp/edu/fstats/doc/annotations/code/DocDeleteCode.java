package lp.edu.fstats.doc.annotations.code;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Desativar acompanhamento de competição",
        description = "Remove a tag de uma competição previamente registrada, interrompendo a atualização de seus dados e partidas."
)

@ApiResponse
public @interface DocDeleteCode {}
