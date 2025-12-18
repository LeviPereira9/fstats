package lp.edu.fstats.doc.annotations.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Solicitar alteração de e-mail",
        description = "Solicita a alteração do endereço de e-mail de um usuário."
)
@ApiResponse
public @interface DocEmailChange {}
