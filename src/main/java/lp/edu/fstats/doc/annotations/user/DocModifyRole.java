package lp.edu.fstats.doc.annotations.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Alterar role do usuário",
        description = "Altera a role associada a um usuário."
)
@ApiResponse
public @interface DocModifyRole {}
