package lp.edu.fstats.unit.user;

import lp.edu.fstats.dto.user.*;
import lp.edu.fstats.exception.custom.CustomBadRequestException;
import lp.edu.fstats.exception.custom.CustomForbiddenActionException;
import lp.edu.fstats.exception.custom.CustomNotFoundException;
import lp.edu.fstats.model.user.Role;
import lp.edu.fstats.model.user.User;
import lp.edu.fstats.repository.user.UserRepository;
import lp.edu.fstats.response.page.PageResponse;
import lp.edu.fstats.service.user.UserServiceImpl;
import lp.edu.fstats.service.verification.VerificationService;

import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VerificationService verificationService;

    @InjectMocks
    private UserServiceImpl userService;

    // Helpers
    private void mockAuthenticatedUser(User user) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private User buildUser(String username) {
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setEmail(username + "@email.com");
        user.setPassword(new BCryptPasswordEncoder().encode("senha123"));
        user.setRole(Role.USER);
        user.setVerified(true);
        return user;
    }

    private User buildAdmin(String username) {
        User user = buildUser(username);
        user.setRole(Role.ADMIN);
        return user;
    }

    // getUser
    @Test
    void getUser_shouldReturnUserResponse_whenSelfRequest() {
        User user = buildUser("joao");

        this.mockAuthenticatedUser(user);

        when(userRepository.findByUsername("joao")).thenReturn(Optional.of(user));

        UserResponse response = userService.getUser("joao");

        assertNotNull(response);
        assertEquals("joao", response.username());
        verify(userRepository).findByUsername("joao");
    }

    @Test
    void getUser_shouldReturnUserResponse_whenRequesterIsAdmin(){
        User admin = buildAdmin("admin");

        this.mockAuthenticatedUser(admin);

        User user = buildUser("joao");
        when(userRepository.findByUsername("joao")).thenReturn(Optional.of(user));

        UserResponse response = userService.getUser("joao");

        assertNotNull(response);
        assertEquals("joao", response.username());
        verify(userRepository).findByUsername("joao");
    }

    @Test
    void getUser_shouldThrowForbidden_whenNotSelfOrAdmin(){
        User user = buildUser("pedro");

        this.mockAuthenticatedUser(user);

        assertThrows(CustomForbiddenActionException.class,
                () -> userService.getUser("joao"));

        verifyNoInteractions(userRepository);
    }

    @Test
    void getUser_shouldThrowNotFound_whenUserDoesNotExist(){
        User user = buildUser("joao");

        this.mockAuthenticatedUser(user);

        when(userRepository.findByUsername("joao")).thenReturn(Optional.empty());

        assertThrows(CustomNotFoundException.class,
                () -> userService.getUser("joao"));
    }

    // getUserShort

    @Test
    void getUserShort_shouldReturnUserShortResponse_whenUserExists(){
        User user = buildUser("joao");

        when(userRepository.findByUsername("joao")).thenReturn(Optional.of(user));

        UserShortResponse response = userService.getUserShort("joao");

        assertNotNull(response);
        assertEquals("joao", response.username());
        verify(userRepository).findByUsername("joao");
    }

    @Test
    void getUserShort_shouldThrowNotFound_whenUserDoesNotExist(){
        when(userRepository.findByUsername("joao")).thenReturn(Optional.empty());

        assertThrows(CustomNotFoundException.class,
                ()-> userService.getUserShort("joao"));
    }

    // getUsersBySearch
    @Test
    void getUsersBySearch_shouldReturnPageResponse_whenUsersFound(){
        User user = buildUser("joao");
        Page<User> page = new PageImpl<>(
                List.of(user),
                PageRequest.of(0, 10), 1);

        when(userRepository.findUsersBySearch(
                eq("joao"),
                any(PageRequest.class)))
                .thenReturn(page);

        PageResponse<UserShortResponse> response = userService.getUsersBySearch("joao", 0);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals("joao", response.getContent().get(0).username());
    }

    @Test
    void getUsersBySearch_shouldReturnEmptyPage_whenNoUsersFound(){
        Page<User> emptyPage = new PageImpl<>(
                List.of(),
                PageRequest.of(0, 10),
                1
        );

        when(userRepository.findUsersBySearch(
                eq("ninguem"),
                any(PageRequest.class)))
                .thenReturn(emptyPage);

        PageResponse<UserShortResponse> response = userService.getUsersBySearch("ninguem", 0);

        assertNotNull(response);
        assertTrue(response.getContent().isEmpty());
    }

    // updateUser

    @Test
    void updateUser_shouldReturnUpdatedUserResponse_whenSelfRequest(){
        User user = buildUser("joao");
        this.mockAuthenticatedUser(user);

        when(userRepository.findByUsername("joao")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                "nova-foto.jpg",
                LocalDate.of(1999, 1, 1)
        );

        UserResponse response = userService.updateUser("joao", request);

        assertNotNull(response);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_shouldReturnUpdatedUserResponse_whenRequesterIsAdmin(){
        User admin = buildAdmin("admin");
        this.mockAuthenticatedUser(admin);

        User user = buildUser("joao");

        when(userRepository.findByUsername("joao")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                "nova-foto.jpg",
                LocalDate.of(1999, 1, 1)
        );

        UserResponse response = userService.updateUser("joao", request);

        assertNotNull(response);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_shouldThrowForbidden_whenNotSelfOrAdmin(){
        User user = buildUser("pedro");
        this.mockAuthenticatedUser(user);

        UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                "nova-foto.jpg",
                LocalDate.of(1999, 1, 1)
        );

        assertThrows(CustomForbiddenActionException.class,
                () -> userService.updateUser("joao", request));

        verifyNoInteractions(userRepository);
    }

    @Test
    void updateUser_shouldThrowNotFound_whenUserDoesNotExist(){
        User user = buildUser("joao");
        this.mockAuthenticatedUser(user);

        when(userRepository.findByUsername("joao")).thenReturn(Optional.empty());

        UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                "nova-foto.jpg",
                LocalDate.of(1999, 1, 1)
        );

        assertThrows(CustomNotFoundException.class,
                ()-> userService.updateUser("joao", request));
    }

    // softDeleteUser

    @Test
    void softDeleteUser_shouldMarkUserAsDeleted_whenSelfRequest(){
        User user = buildUser("joao");
        this.mockAuthenticatedUser(user);

        when(userRepository.findByUsername("joao")).thenReturn(Optional.of(user));

        userService.softDeleteUser("joao");

        assertTrue(user.isDeleted());
        verify(userRepository).save(user);
    }

    @Test
    void softDeleteUser_shouldMarkUserAsDeleted_whenRequesterIsAdmin(){
        User admin = buildAdmin("admin");
        this.mockAuthenticatedUser(admin);

        User user = buildUser("joao");

        when(userRepository.findByUsername("joao")).thenReturn(Optional.of(user));

        userService.softDeleteUser("joao");

        assertTrue(user.isDeleted());
        verify(userRepository).save(user);
    }

    @Test
    void softDeleteUser_shouldThrowForbidden_whenNotSelfOrAdmin(){
        User user = buildUser("pedro");
        this.mockAuthenticatedUser(user);

        assertThrows(CustomForbiddenActionException.class,
                ()-> userService.softDeleteUser("joao"));

        verifyNoInteractions(userRepository);
    }

    @Test
    void softDeleteUser_shouldThrowNotFound_whenUserDoesNotExist(){
        User user = buildUser("joao");
        this.mockAuthenticatedUser(user);

        when(userRepository.findByUsername("joao")).thenReturn(Optional.empty());

        assertThrows(CustomNotFoundException.class,
                ()-> userService.softDeleteUser("joao"));
    }

    // updatePassword

    @Test
    void updatePassword_shouldUpdatePassword_whenRequestIsValid(){
        User user = buildUser("joao");

        this.mockAuthenticatedUser(user);

        when(userRepository.findByUsername("joao")).thenReturn(Optional.of(user));

        UserPasswordUpdateRequest request = new UserPasswordUpdateRequest(
                "senha123",
                "novaSenha123",
                "novaSenha123"
        );

        userService.updatePassword("joao", request);

        verify(userRepository).save(user);
        verify(verificationService).sendPasswordChangedNotification(user.getEmail());
    }

    @Test
    void updatePassword_shouldThrowForbidden_whenNotSelfRequest(){
        User user = buildUser("pedro");
        this.mockAuthenticatedUser(user);

        UserPasswordUpdateRequest request = new UserPasswordUpdateRequest(
                "senha123",
                "novaSenha123",
                "novaSenha123"
        );

        assertThrows(CustomForbiddenActionException.class,
                ()-> userService.updatePassword("joao", request));

        verifyNoInteractions(userRepository);
    }

    @Test
    void updatePassword_shouldThrowBadRequest_whenCurrentPasswordIsInvalid(){
        User user = buildUser("joao");
        this.mockAuthenticatedUser(user);

        when(userRepository.findByUsername("joao")).thenReturn(Optional.of(user));

        UserPasswordUpdateRequest request = new UserPasswordUpdateRequest(
                "senhaErrada",
                "novaSenha123",
                "novaSenha123"
        );

        assertThrows(CustomBadRequestException.class,
                ()-> userService.updatePassword("joao", request));

        verify(userRepository, never()).save(user);
    }

    @Test
    void updatePassword_shouldThrowBadRequest_whenNewPasswordsDontMatch(){
        User user = buildUser("joao");
        this.mockAuthenticatedUser(user);

        when(userRepository.findByUsername("joao")).thenReturn(Optional.of(user));

        UserPasswordUpdateRequest request = new UserPasswordUpdateRequest(
                "senha123",
                "novaSenha123",
                "novaSenha456"
        );

        assertThrows(CustomBadRequestException.class,
                ()-> userService.updatePassword("joao", request));

        verify(userRepository, never()).save(user);
    }

    @Test
    void updatePassword_shouldThrowBadRequest_whenNewPasswordIsSameAsOld(){
        User user = buildUser("joao");
        this.mockAuthenticatedUser(user);

        when(userRepository.findByUsername("joao")).thenReturn(Optional.of(user));

        UserPasswordUpdateRequest request = new UserPasswordUpdateRequest(
                "senha123",
                "senha123",
                "senha123"
        );

        assertThrows(CustomBadRequestException.class,
                ()-> userService.updatePassword("joao", request));

        verify(userRepository, never()).save(user);
    }

    // requestEmailChange

    @Test
    void requestEmailChange_shouldSendConfirmation_whenUserIsVerified(){
        User user = buildUser("joao");
        this.mockAuthenticatedUser(user);

        when(userRepository.findByUsername("joao")).thenReturn(Optional.of(user));

        UserEmailUpdateRequest request = new UserEmailUpdateRequest("novo@email.com");

        userService.requestEmailChange("joao", request);

        verify(verificationService).sendEmailChangeConfirmation(user, "novo@email.com");
    }

    @Test
    void requestEmailChange_shouldThrowForbidden_whenEmailNotVerified(){
        User user = buildUser("joao");
        user.setVerified(false);

        this.mockAuthenticatedUser(user);

        when(userRepository.findByUsername("joao")).thenReturn(Optional.of(user));

        UserEmailUpdateRequest request = new UserEmailUpdateRequest("novo@email.com");

        assertThrows(CustomForbiddenActionException.class,
                ()-> userService.requestEmailChange("joao", request));

        verify(verificationService, never()).sendEmailChangeConfirmation(any(), any());
    }

    @Test
    void requestEmailChange_shouldThrowNotFound_whenUserDoesNotExist(){
        User user = buildUser("joao");

        this.mockAuthenticatedUser(user);

        when(userRepository.findByUsername("joao")).thenReturn(Optional.empty());

        UserEmailUpdateRequest request = new UserEmailUpdateRequest("novo@email.com");

        assertThrows(CustomNotFoundException.class,
                ()-> userService.requestEmailChange("joao", request));

        verify(verificationService, never()).sendEmailChangeConfirmation(any(), any());
    }
}
