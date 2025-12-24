package lp.edu.fstats;

import lp.edu.fstats.integration.service.football.sync.ExternalSyncOrchestrator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FstatsApplication {

    public static void main(String[] args) {
       /*ApplicationContext context =*/ SpringApplication.run(FstatsApplication.class, args);

        //ExternalSyncOrchestrator syncOrchestrator = context.getBean(ExternalSyncOrchestrator.class);

        //syncOrchestrator.syncAll();

    }

}
