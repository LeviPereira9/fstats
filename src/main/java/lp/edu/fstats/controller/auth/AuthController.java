package lp.edu.fstats.controller.auth;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lp.edu.fstats.config.redis.ratelimit.snippets.RateLimit;
import lp.edu.fstats.doc.annotations.auth.DocLoginUser;
import lp.edu.fstats.doc.annotations.auth.DocRegisterUser;
import lp.edu.fstats.dto.auth.AuthLogin;
import lp.edu.fstats.dto.auth.AuthRegister;
import lp.edu.fstats.dto.auth.AuthResponse;
import lp.edu.fstats.dto.user.UserShortResponse;
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
    @RateLimit
    @PostMapping("/register")
    public ResponseEntity<Response<Void>> registerUser(@RequestBody @Valid AuthRegister request){

        AuthResponse data = authService.register(request);

        ResponseCookie cookie = this.setCookie(data.toString(), 15);

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
    @RateLimit
    @PostMapping("/login")
    public ResponseEntity<Response<Void>> loginUser(@RequestBody @Valid AuthLogin request){
        AuthResponse data = authService.login(request);
        int code = HttpStatus.OK.value();

        ResponseCookie cookie = this.setCookie(data.token(), 15);

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

    @PostMapping("/logout")
    public ResponseEntity<Response<Void>> logout(){

        int code = HttpStatus.OK.value();

        ResponseCookie cookie = this.setCookie("", 0);

        Response<Void> response = Response.<Void>builder()
                .operation("Auth.Logout")
                .code(code)
                .message("Usuário deslogado com sucesso.")
                .build();

        return ResponseEntity
                .status(code)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<Response<UserShortResponse>> me(){
        int code = HttpStatus.OK.value();

        UserShortResponse data = authService.me();

        Response<UserShortResponse> response = Response.<UserShortResponse>builder()
                .operation("Auth.Me")
                .code(code)
                .message("Usuário encontrado com sucesso.")
                .data(data)
                .build();

        return ResponseEntity
                .status(code)
                .body(response);
    }

    private ResponseCookie setCookie(String token, int days){


        return ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(Duration.ofDays(days))
                .build();
    }

}
