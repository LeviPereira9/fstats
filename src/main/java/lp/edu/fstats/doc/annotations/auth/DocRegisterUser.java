package lp.edu.fstats.doc.annotations.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Registrar usuário",
        description = "Cria uma nova conta de usuário e retorna um token JWT para autenticação."
)
/*@ApiResponse(
        responseCode = "201",
        description = "Usuário registrado com sucesso."
)*/
public @interface DocRegisterUser {}
