package lp.edu.fstats;

import lp.edu.fstats.integration.service.football.FootballSyncService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class FstatsApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(FstatsApplication.class, args);

        //FootballSyncService syncService = context.getBean(FootballSyncService.class);

        //syncService.Manage();

    }

}
