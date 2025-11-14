package lp.edu.fstats.service.auth;

import lp.edu.fstats.dto.auth.AuthLogin;
import lp.edu.fstats.dto.auth.AuthRegister;
import lp.edu.fstats.dto.auth.AuthResponse;

public interface AuthService {
    AuthResponse register(AuthRegister request);

    AuthResponse login(AuthLogin request);
}
