package lp.edu.fstats.repository.user;

import lp.edu.fstats.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
        SELECT u FROM User u WHERE u.username = :login OR u.email = :login
""")
    Optional<User> findByUsernameOrEmail(String login);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    @Query("""
    SELECT U FROM User U
     WHERE U.deleted = FALSE AND U.username LIKE CONCAT('%', :search, '%')
""")
    Page<User> findUsersBySearch(String search, Pageable pageRequest);
}
