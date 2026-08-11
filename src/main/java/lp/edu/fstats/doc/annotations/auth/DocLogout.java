package lp.edu.fstats.doc.annotations.auth;

import io.swagger.v3.oas.annotations.Operation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Deslogar usuário",
        description = "Encerra a sessão do usuário autenticado removendo o cookie que contém o JWT de autenticação."
)
public @interface DocLogout {}
