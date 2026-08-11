package lp.edu.fstats.doc.annotations.auth;


import io.swagger.v3.oas.annotations.Operation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Buscar informações do usuário autenticado",
        description = "Retorna as informações do usuário identificado pelo JWT armazenado no cookie de autenticação. É necessário estar autenticado para acessar este endpoint."
)
public @interface DocMe {}
