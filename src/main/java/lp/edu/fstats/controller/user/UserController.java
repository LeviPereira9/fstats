package lp.edu.fstats.controller.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lp.edu.fstats.doc.annotations.user.*;
import lp.edu.fstats.dto.user.*;
import lp.edu.fstats.response.normal.Response;
import lp.edu.fstats.response.page.PageResponse;
import lp.edu.fstats.service.user.RoleService;
import lp.edu.fstats.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Usuários",
        description = "Endpoints para consulta, atualização e gerenciamento de usuários."
)
@RestController
@RequestMapping("/${api.prefix}/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final RoleService roleService;

    @DocGetUser
    @GetMapping("/{username}/details")
    public ResponseEntity<Response<UserResponse>> getUser(@PathVariable String username) {

        UserResponse data = userService.getUser(username);

        Response<UserResponse> response = Response.<UserResponse>builder()
                .operation("User.GetByUsername")
                .code(HttpStatus.OK.value())
                .message("Usuário encontrado com sucesso.")
                .data(data)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @DocGetShortUser
    @GetMapping("/{username}")
    public ResponseEntity<Response<UserShortResponse>> getShortUser(@PathVariable String username) {
        UserShortResponse data = userService.getUserShort(username);

        Response<UserShortResponse> response = Response.<UserShortResponse>builder()
                .operation("User.GetShortInfoByUsername")
                .code(HttpStatus.OK.value())
                .message("Usuário encontrado com sucesso.")
                .data(data)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @DocGetUsersBySearch
    @GetMapping("/search")
    public ResponseEntity<PageResponse<UserShortResponse>> getUsersBySearch(
            @RequestParam String search,
            @RequestParam(defaultValue = "0") int page
    ){
        PageResponse<UserShortResponse> data = userService.getUsersBySearch(search, page);

        return ResponseEntity.ok().body(data);
    }

    @DocUpdateUser
    @PutMapping("/{username}")
    public ResponseEntity<Response<UserResponse>> updateUser(
            @PathVariable String username,
            @RequestBody @Valid UserProfileUpdateRequest request
    ){

        UserResponse data = userService.updateUser(username, request);

        Response<UserResponse> response = Response.<UserResponse>builder()
                .operation("User.UpdateProfile")
                .code(HttpStatus.OK.value())
                .message("Usuário atualizado com sucesso.")
                .data(data)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @DocUpdateUserPassword
    @PatchMapping("/{username}/password")
    public ResponseEntity<Response<Void>> updateUserPassword(
            @PathVariable String username,
            @RequestBody @Valid UserPasswordUpdateRequest request
    ){
        userService.updatePassword(username, request);

        Response<Void> response = Response.<Void>builder()
                .operation("User.UpdatePassword")
                .code(HttpStatus.OK.value())
                .message("Senha atualizada com sucesso.")
                .build();

        return ResponseEntity.ok().body(response);
    }

    @DocSoftDeleteUser
    @DeleteMapping("/{username}")
    public ResponseEntity<Response<Void>> softDeleteUser(@PathVariable String username) {
        userService.softDeleteUser(username);

        int code = HttpStatus.OK.value();

        Response<Void> response = Response.<Void>builder()
                .operation("User.SoftDeleteUser")
                .code(code)
                .message("Usuário deletado com sucesso.")
                .build();

        return ResponseEntity.status(code).body(response);
    }

    @DocEmailChange
    @PostMapping("/{username}/email")
    public ResponseEntity<Response<Void>> emailChange(@PathVariable String username, @RequestBody UserEmailUpdateRequest request) {
        userService.requestEmailChange(username, request);

        Response<Void> response = Response.<Void>builder()
                .operation("User.RequestEmailChange")
                .code(HttpStatus.OK.value())
                .message("Solicitação de alteração de e-mail realizada com sucesso.")
                .build();

        return ResponseEntity.ok(response);

    }

    @DocModifyRole
    @PutMapping("/{username}/role")
    public ResponseEntity<Response<Void>> modifyRole(
            @PathVariable String username,
            @RequestParam String role){

        roleService.addRole(username, role);

        Response<Void> response = Response
                .<Void>builder()
                .operation("User.ModifyRole")
                .code(HttpStatus.OK.value())
                .message("Cargo do usuário alterado com sucesso.")
                .build();

        return ResponseEntity.ok(response);
    }
}
