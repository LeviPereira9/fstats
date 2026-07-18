package lp.edu.fstats.repository.verification;

import lp.edu.fstats.model.verification.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByUser_UsernameAndToken(String username, String token);
}
