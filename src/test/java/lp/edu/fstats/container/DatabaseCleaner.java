package lp.edu.fstats.container;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseCleaner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void clean(){
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=0");

        List<String> tables = jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables WHERE table_schema = DATABASE()", String.class
        );

        tables.forEach(table -> jdbcTemplate.execute("TRUNCATE TABLE " + table));

        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=1");
    }

}
