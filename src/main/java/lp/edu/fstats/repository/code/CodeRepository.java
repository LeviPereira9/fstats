package lp.edu.fstats.repository.code;

import lp.edu.fstats.model.code.Code;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeRepository extends JpaRepository<Code, Integer> {
    boolean existsByCode(String code);
}
