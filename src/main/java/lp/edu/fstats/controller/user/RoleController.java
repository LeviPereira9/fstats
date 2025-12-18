package lp.edu.fstats.controller.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lp.edu.fstats.doc.annotations.user.DocGetAllRoles;
import lp.edu.fstats.dto.user.RoleResponse;
import lp.edu.fstats.response.normal.Response;
import lp.edu.fstats.service.user.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Roles",
        description = "Endpoints para consulta de roles do sistema."
)

@RestController
@RequestMapping("/${api.prefix}/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @DocGetAllRoles
    @GetMapping
    public ResponseEntity<Response<RoleResponse>> getAllRoles(){
        RoleResponse data = roleService.getAllRoles();

        Response<RoleResponse> response = Response
                .<RoleResponse>builder()
                .operation("Role.GetAll")
                .code(HttpStatus.OK.value())
                .data(data)
                .build();

        return ResponseEntity.ok().body(response);
    }

}
