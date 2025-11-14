package lp.edu.fstats.service.user;

import lombok.RequiredArgsConstructor;
import lp.edu.fstats.dto.user.RoleResponse;
import lp.edu.fstats.exception.custom.CustomForbiddenActionException;
import lp.edu.fstats.exception.custom.CustomNotFoundException;
import lp.edu.fstats.model.user.Role;
import lp.edu.fstats.model.user.User;
import lp.edu.fstats.repository.user.UserRepository;
import lp.edu.fstats.util.AuthUtil;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final UserRepository userRepository;

    @Override
    public RoleResponse getAllRoles() {
        return new RoleResponse(
                Arrays.stream(Role.values())
                        .map(Role::getName)
                        .toList());
    }

    @Override
    public void addRole(String username, String targetRole) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(CustomNotFoundException::user);

        Role role = Role.getRoleByName(targetRole);

        User requester = AuthUtil.getRequester();

        if(!requester.getRole().canModify(role)){
            throw CustomForbiddenActionException.notAuthorized();
        }

        user.setRole(role);
        user.update();

        userRepository.save(user);
    }
}
