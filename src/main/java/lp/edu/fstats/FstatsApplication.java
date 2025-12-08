package lp.edu.fstats;

import lp.edu.fstats.integration.client.FootballApiClient;
import lp.edu.fstats.integration.service.football.FootballSyncService;
import lp.edu.fstats.service.auto.AutoService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Year;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@EnableScheduling
public class FstatsApplication {

    public static void main(String[] args) {
        /*ApplicationContext context = */SpringApplication.run(FstatsApplication.class, args);

        //FootballSyncService syncService = context.getBean(FootballSyncService.class);
        //AutoService autoService = context.getBean(AutoService.class);
        /*FootballApiClient apiClient = context.getBean(FootballApiClient.class);

        apiClient.getCurrentTeams("PL", Year.now());*/
        //AtomicInteger atomicInteger = new AtomicInteger(0);

        //autoService.manageEverything("SA", atomicInteger);

        //syncService.Manage(1);
        //syncService.manageCompetitionMatches("PL", 1, null);
        //syncService.manageStandings("PL", 1);
        //syncService.fuckingManageProbability();
    }

}
