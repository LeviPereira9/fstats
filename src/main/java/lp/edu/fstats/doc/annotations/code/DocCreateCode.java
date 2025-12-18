package lp.edu.fstats.doc.annotations.code;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Ativar acompanhamento de competição",
        description = "Ativa o acompanhamento de uma competição da API externa a partir de sua tag, iniciando a integração de partidas e dados relacionados."
)

@ApiResponse
public @interface DocCreateCode {}
