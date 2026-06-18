package lp.edu.fstats.service;

import lp.edu.fstats.dto.user.RoleResponse;
import static lp.edu.fstats.factory.UserTestFactory.*;

import static org.junit.jupiter.api.Assertions.*;

import lp.edu.fstats.exception.custom.CustomForbiddenActionException;
import lp.edu.fstats.exception.custom.CustomNotFoundException;
import lp.edu.fstats.model.user.Role;
import lp.edu.fstats.model.user.User;
import lp.edu.fstats.repository.user.UserRepository;
import lp.edu.fstats.service.user.RoleServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    //helpers
    @AfterEach
    void clearSecurityContext(){
        SecurityContextHolder.clearContext();
    }

    // getAllRoles
    @Test
    void getAllRoles_shouldReturnAllRoles(){
        RoleResponse response = roleService.getAllRoles();

        assertNotNull(response);
        assertEquals(Role.values().length, response.roles().size());
        assertTrue(response.roles().containsAll(
                Arrays.stream(
                        Role.values())
                        .map(Role::getName)
                        .toList()
        ));

        verifyNoMoreInteractions(userRepository);
    }

    // addRole

    @Test
    void addRole_shouldUpdateRole_whenRequesterHasHigherHierarchy(){
        User admin = buildAdmin("admin");
        User user = buildUser("joao");

        mockAuthenticatedUser(admin);

        when(userRepository.findByUsername("joao"))
                .thenReturn(Optional.of(user));

        roleService.addRole("joao", "MOD");

        assertEquals(Role.MOD, user.getRole());

        verify(userRepository).save(user);
    }

    @Test
    void addRole_shouldThrowNotFound_whenUserDoesNotExist(){
        User admin = buildAdmin("admin");
        mockAuthenticatedUser(admin);

        when(userRepository.findByUsername("joao"))
                .thenReturn(Optional.empty());

        assertThrows(CustomNotFoundException.class,
                ()-> roleService.addRole("joao", "MOD"));

        verify(userRepository, never()).save(any());
    }

    @Test
    void addRole_shouldThrownNotFound_whenRoleDOesNotExist(){
        User admin = buildAdmin("admin");
        User user = buildUser("joao");
        mockAuthenticatedUser(admin);

        when(userRepository.findByUsername("joao"))
                .thenReturn(Optional.of(user));

        assertThrows(CustomNotFoundException.class,
                ()-> roleService.addRole("joao", "ROLE_INEXISTENTE"));

        verify(userRepository, never()).save(any());
    }

    @Test
    void addRole_shouldThrowForbidden_whenRequesterHasSameHierarchy(){
        User admin = buildAdmin("admin");
        User user = buildUser("joao");
        mockAuthenticatedUser(admin);

        when(userRepository.findByUsername("joao"))
                .thenReturn(Optional.of(user));

        assertThrows(CustomForbiddenActionException.class,
                ()-> roleService.addRole("joao", "ADMIN"));

        verify(userRepository, never()).save(any());
    }

    @Test
    void addRole_shouldThrowForbidden_whenRequesterHasLowerHierarchy(){
        User admin = buildAdmin("admin");
        User user = buildUser("joao");
        mockAuthenticatedUser(admin);

        when(userRepository.findByUsername("joao"))
                .thenReturn(Optional.of(user));

        assertThrows(CustomForbiddenActionException.class,
                ()-> roleService.addRole("joao", "SUPER_ADMIN"));

        verify(userRepository, never()).save(any());
    }

    @Test
    void addRole_shouldAllowSuperAdmin_toAssignAnyRole(){
        User superAdmin = buildAdmin("admin");
        superAdmin.setRole(Role.SUPER_ADMIN);
        User user = buildUser("joao");
        mockAuthenticatedUser(superAdmin);

        when(userRepository.findByUsername("joao"))
                .thenReturn(Optional.of(user));

        roleService.addRole("joao", "ADMIN");

        assertEquals(Role.ADMIN, user.getRole());
        verify(userRepository).save(user);
    }
}
