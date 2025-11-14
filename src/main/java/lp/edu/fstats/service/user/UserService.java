package lp.edu.fstats.service.user;

import lp.edu.fstats.dto.user.*;
import lp.edu.fstats.response.page.PageResponse;

public interface UserService {

    UserResponse getUser(String username);

    UserShortResponse getUserShort(String username);

    PageResponse<UserShortResponse> getUsersBySearch(String search, int page);

    UserResponse updateUser(String username, UserProfileUpdateRequest request);

    void softDeleteUser(String username);

    void updatePassword(String username, UserPasswordUpdateRequest request);

    void requestEmailChange(String username, UserEmailUpdateRequest request);

}
