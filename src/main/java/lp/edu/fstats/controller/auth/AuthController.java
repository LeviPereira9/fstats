package lp.edu.fstats.controller.auth;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lp.edu.fstats.doc.annotations.auth.DocLoginUser;
import lp.edu.fstats.doc.annotations.auth.DocRegisterUser;
import lp.edu.fstats.dto.auth.AuthLogin;
import lp.edu.fstats.dto.auth.AuthRegister;
import lp.edu.fstats.dto.auth.AuthResponse;
import lp.edu.fstats.response.normal.Response;
import lp.edu.fstats.service.auth.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

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
    public ResponseEntity<Response<Void>> registerUser(@RequestBody @Valid AuthRegister request){

        AuthResponse data = authService.register(request);

        ResponseCookie cookie = ResponseCookie.from("access_token", data.token())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(Duration.ofDays(7))
                .build();

        int code = HttpStatus.CREATED.value();

        Response<Void> response = Response.<Void>builder()
                .operation("Auth.Register")
                .message("Usuário cadastrado com sucesso.")
                .code(code)
                .build();

        return ResponseEntity
                .status(code)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    @DocLoginUser
    @PostMapping("/login")
    public ResponseEntity<Response<Void>> loginUser(@RequestBody @Valid AuthLogin request){
        AuthResponse data = authService.login(request);
        int code = HttpStatus.OK.value();

        ResponseCookie cookie = ResponseCookie.from("access_token", data.token())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(Duration.ofDays(7))
                .build();

        Response<Void> response = Response.<Void>builder()
                .operation("Auth.Login")
                .code(code)
                .message("Usuário logado com sucesso.")
                .build();

        return ResponseEntity
                .status(code)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

}
