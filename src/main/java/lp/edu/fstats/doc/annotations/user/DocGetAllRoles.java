package lp.edu.fstats.doc.annotations.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Consultar cargos",
        description = "Retorna todos os cargos que existem no sistema."
)
@ApiResponse
public @interface DocGetAllRoles {}
