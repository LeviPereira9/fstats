package lp.edu.fstats.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.edu.fstats.dto.user.*;
import lp.edu.fstats.exception.custom.CustomBadRequestException;
import lp.edu.fstats.exception.custom.CustomForbiddenActionException;
import lp.edu.fstats.exception.custom.CustomNotFoundException;
import lp.edu.fstats.model.user.User;
import lp.edu.fstats.repository.user.UserRepository;
import lp.edu.fstats.response.page.PageResponse;
import lp.edu.fstats.service.verification.VerificationService;
import lp.edu.fstats.util.AuthUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final VerificationService verificationService;

    @Override
    public UserResponse getUser(String username) {
        if(!AuthUtil.isSelfOrAdmin(username)){
            throw CustomForbiddenActionException.notAuthorized();
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(CustomNotFoundException::user);

        return new UserResponse(user);
    }

    @Override
    public UserShortResponse getUserShort(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(CustomNotFoundException::user);

        return new UserShortResponse(user);
    }

    @Override
    public PageResponse<UserShortResponse> getUsersBySearch(String search, int page) {
        PageRequest pageRequest = PageRequest.of(
                page,
                10
        );

        Page<User> pageFound = userRepository.findUsersBySearch(search, pageRequest);

        PageResponse<User> response = new PageResponse<>(pageFound);

        return response.map(UserShortResponse::new);
    }

    @Transactional
    @Override
    public UserResponse updateUser(String username, UserProfileUpdateRequest request) {
        if(!AuthUtil.isSelfOrAdmin(username)){
            throw CustomForbiddenActionException.notAuthorized();
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(CustomNotFoundException::user);

        request.updateUser(user);

        return new UserResponse(userRepository.save(user));
    }

    @Transactional
    @Override
    public void softDeleteUser(String username) {
        if(!AuthUtil.isSelfOrAdmin(username)){
            throw CustomForbiddenActionException.notAuthorized();
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(CustomNotFoundException::user);

        user.softDelete();

        userRepository.save(user);
    }

    @Transactional
    @Override
    public void updatePassword(String username, UserPasswordUpdateRequest request) {
        if(!AuthUtil.isSelfRequest(username)){
            throw CustomForbiddenActionException.notAuthorized();
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(CustomNotFoundException::user);

        String newPasswordHash = this.validatePasswordRequest(request, user);

        user.changePassword(newPasswordHash);

        userRepository.save(user);

        verificationService.sendPasswordChangedNotification(user.getEmail());
    }

    private String validatePasswordRequest(UserPasswordUpdateRequest request, User user) {

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean currentPasswordIsValid = passwordEncoder.matches(
                request.currentPassword(), user.getPassword());

        if(!currentPasswordIsValid){
            throw CustomBadRequestException.invalidCurrentPassword();
        }

        if(!request.newPasswordsMatch()){
            throw CustomBadRequestException.passwordDontMatch();
        }

        if(request.newPasswordIsEqualsToOld()){
            throw CustomBadRequestException.passwordDidntChange();
        }

        return passwordEncoder.encode(request.newPassword());
    }

    @Override
    public void requestEmailChange(String username, UserEmailUpdateRequest request) {
        AuthUtil.isSelfRequest(username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(CustomNotFoundException::user);

        if(!user.isVerified()){
            throw CustomForbiddenActionException.emailNotVerified();
        }

        verificationService.sendEmailChangeConfirmation(user, request.newEmail());
    }
}
