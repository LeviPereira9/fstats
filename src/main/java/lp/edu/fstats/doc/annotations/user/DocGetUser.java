package lp.edu.fstats.doc.annotations.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Consultar usuário",
        description = "Retorna os dados detalhados de um usuário a partir de seu nome de usuário."
)
@ApiResponse
public @interface DocGetUser {}
