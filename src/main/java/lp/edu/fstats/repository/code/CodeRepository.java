package lp.edu.fstats.repository.code;

import lp.edu.fstats.model.code.Code;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodeRepository extends JpaRepository<Code, Integer> {
    boolean existsByCode(String code);
}
