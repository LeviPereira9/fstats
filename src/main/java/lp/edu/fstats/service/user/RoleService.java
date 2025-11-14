package lp.edu.fstats.service.user;

import lp.edu.fstats.dto.user.RoleResponse;

public interface RoleService {
    RoleResponse getAllRoles();

    void addRole(String username, String role);
}
