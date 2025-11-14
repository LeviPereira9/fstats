package lp.edu.fstats.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lp.edu.fstats.dto.user.*;
import lp.edu.fstats.response.normal.Response;
import lp.edu.fstats.response.page.PageResponse;
import lp.edu.fstats.service.user.RoleService;
import lp.edu.fstats.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/${api.prefix}/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final RoleService roleService;

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

    @GetMapping("/search")
    public ResponseEntity<PageResponse<UserShortResponse>> getUsersBySearch(
            @RequestParam String search,
            @RequestParam(defaultValue = "0") int page
    ){
        PageResponse<UserShortResponse> data = userService.getUsersBySearch(search, page);

        return ResponseEntity.ok().body(data);
    }

    @PutMapping("/{username}")
    public ResponseEntity<Response<UserResponse>> updateUser(
            @PathVariable String username,
            @RequestBody @Valid UserProfileUpdateRequest request
    ){

        UserResponse data = userService.updateUser(username, request);

        Response<UserResponse> response = Response.<UserResponse>builder()
                .operation("User.updateProfile")
                .code(HttpStatus.OK.value())
                .message("Usuário atualizado com sucesso.")
                .data(data)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{username}/password")
    public ResponseEntity<Response<Void>> updateUserPassword(
            @PathVariable String username,
            @RequestBody @Valid UserPasswordUpdateRequest request
    ){
        userService.updatePassword(username, request);

        Response<Void> response = Response.<Void>builder()
                .operation("User.updatePassword")
                .code(HttpStatus.OK.value())
                .message("Senha atualizada com sucesso.")
                .build();

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Response<Void>> softDeleteUser(@PathVariable String username) {
        userService.softDeleteUser(username);

        int code = HttpStatus.OK.value();

        Response<Void> response = Response.<Void>builder()
                .operation("User.softDeleteUser")
                .code(code)
                .message("Usuário deletado com sucesso.")
                .build();

        return ResponseEntity.status(code).body(response);
    }

    @PostMapping("/{username}/email")
    public ResponseEntity<Response<Void>> emailChange(@PathVariable String username, @RequestBody UserEmailUpdateRequest request) {
        userService.requestEmailChange(username, request);

        Response<Void> response = Response.<Void>builder()
                .operation("User.softDeleteUser")
                .code(HttpStatus.OK.value())
                .message("Usuário deletado com sucesso.")
                .build();

        return ResponseEntity.ok(response);

    }

    @PostMapping("/{username}/role")
    public ResponseEntity<Response<Void>> modifyRole(
            @PathVariable String username,
            @RequestParam String role){

        roleService.addRole(username, role);

        Response<Void> response = Response
                .<Void>builder()
                .operation("User.modifyRole")
                .code(HttpStatus.OK.value())
                .message("Cargo do usuário alterado com sucesso.")
                .build();

        return ResponseEntity.ok(response);
    }
}
