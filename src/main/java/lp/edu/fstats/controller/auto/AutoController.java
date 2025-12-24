package lp.edu.fstats.controller.auto;

import lombok.RequiredArgsConstructor;
import lp.edu.fstats.integration.service.football.sync.ExternalSyncOrchestrator;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("dev")
@RestController
@RequestMapping("/${api.prefix}/auto")
@RequiredArgsConstructor
public class AutoController {

    private final ExternalSyncOrchestrator externalSyncOrchestrator;

    @PostMapping("/{code}")
    public void startSync(@PathVariable String code) {
        externalSyncOrchestrator.sync(code);
    }
}
