package lp.edu.fstats.util;

import lp.edu.fstats.exception.custom.CustomForbiddenActionException;
import lp.edu.fstats.model.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {
    public static final String[] PUBLIC_ENDPOINTS = {
            "/api/v1/auth/**",
            "/api/v1/verify/**"
    };

    public static User getRequester(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !authentication.isAuthenticated()){
            throw CustomForbiddenActionException.notAuthenticated();
        }

        Object principal = authentication.getPrincipal();

        if(principal instanceof User user){
            return user;
        }

        throw CustomForbiddenActionException.notAuthenticated();
    }

    public static boolean isSelfOrAdmin(String username) {

        User requester = getRequester();

        boolean isSelfRequest = requester.getUsername().equals(username);
        boolean isAdmin = requester.getRole().hasElevatedPrivileges();

        return isSelfRequest || isAdmin;

    }

    public static boolean isSelfRequest(String username) {
        User requester = getRequester();

        return requester.getUsername().equals(username);
    }
}
