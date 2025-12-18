package lp.edu.fstats.doc.annotations.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Buscar usuários",
        description = "Retorna uma lista paginada de usuários com base no seu nome de usuário."
)
@ApiResponse
public @interface DocGetUsersBySearch {}
