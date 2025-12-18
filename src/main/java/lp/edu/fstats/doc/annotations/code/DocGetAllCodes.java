package lp.edu.fstats.doc.annotations.code;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Listar competições acompanhadas",
        description = "Retorna todas as competições da API externa atualmente registradas para acompanhamento no sistema."
)

@ApiResponse
public @interface DocGetAllCodes {}
