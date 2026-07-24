package lp.edu.fstats.support;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@Component
@RequiredArgsConstructor
@ActiveProfiles("integration")
public class TestDatabaseCleaner {

    private final JdbcTemplate jdbcTemplate;

    public void truncateAll(){
        List<String> tables = jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables WHERE table_schema = DATABASE()",
                String.class
        );

        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0 ");

        tables.stream()
                .filter(table -> !table.equals("flyway_schema_history"))
                .forEach(table ->
                        jdbcTemplate.execute("TRUNCATE TABLE " + table)
                );

        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1 ");
    }

}
