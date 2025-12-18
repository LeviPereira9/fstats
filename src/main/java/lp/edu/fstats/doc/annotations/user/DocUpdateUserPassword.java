package lp.edu.fstats.doc.annotations.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Atualizar senha do usuário",
        description = "Atualiza a senha de um usuário."
)
@ApiResponse
public @interface DocUpdateUserPassword {}
