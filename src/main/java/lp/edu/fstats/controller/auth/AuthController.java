package lp.edu.fstats.controller.auth;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lp.edu.fstats.doc.annotations.auth.DocLoginUser;
import lp.edu.fstats.doc.annotations.auth.DocRegisterUser;
import lp.edu.fstats.dto.auth.AuthLogin;
import lp.edu.fstats.dto.auth.AuthRegister;
import lp.edu.fstats.dto.auth.AuthResponse;
import lp.edu.fstats.response.normal.Response;
import lp.edu.fstats.service.auth.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Autenticação",
        description = "Endpoints de autenticação, responsáveis pelo login e registro de usuários."
)
@RestController
@RequestMapping("/${api.prefix}/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @DocRegisterUser
    @PostMapping("/register")
    public ResponseEntity<Response<AuthResponse>> registerUser(@RequestBody @Valid AuthRegister request){

        AuthResponse data = authService.register(request);

        int code = HttpStatus.CREATED.value();

        Response<AuthResponse> response = Response.<AuthResponse>builder()
                .operation("Auth.Register")
                .message("Usuário cadastrado com sucesso.")
                .data(data)
                .code(code)
                .build();

        return ResponseEntity.status(code).body(response);
    }

    @DocLoginUser
    @PostMapping("/login")
    public ResponseEntity<Response<AuthResponse>> loginUser(@RequestBody @Valid AuthLogin request){
        AuthResponse data = authService.login(request);
        int code = HttpStatus.OK.value();

        Response<AuthResponse> response = Response.<AuthResponse>builder()
                .operation("Auth.Login")
                .code(code)
                .data(data)
                .message("Usuário logado com sucesso.")
                .build();

        return ResponseEntity.status(code).body(response);
    }

}
