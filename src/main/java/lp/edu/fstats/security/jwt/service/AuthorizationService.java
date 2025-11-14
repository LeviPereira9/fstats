package lp.edu.fstats.security.jwt.service;

import lombok.RequiredArgsConstructor;
import lp.edu.fstats.exception.custom.CustomNotFoundException;
import lp.edu.fstats.model.user.User;
import lp.edu.fstats.repository.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public User loadUserByUsername(String login) {
        return userRepository.findByUsernameOrEmail(login)
                .orElseThrow(CustomNotFoundException::user);
    }

}
