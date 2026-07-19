package lp.edu.fstats.base;

import io.restassured.RestAssured;
import lp.edu.fstats.integration.client.BrevoApiClient;
import lp.edu.fstats.integration.client.FootballApiClient;
import lp.edu.fstats.service.email.EmailService;
import lp.edu.fstats.support.TestDatabaseCleaner;
import lp.edu.fstats.support.TestRedisCleaner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
public abstract class IntegrationTestBase extends ContainerBase {

    @MockitoBean
    protected FootballApiClient footballApiClient;

    @MockitoBean
    protected BrevoApiClient brevoApiClient;

    @MockitoBean
    protected EmailService emailService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestDatabaseCleaner databaseCleaner;

    @Autowired
    private TestRedisCleaner redisCleaner;

    @BeforeEach
    void setUpRestAssured(){
        RestAssured.port = port;
        RestAssured.basePath = "/api/v1";
    }

    @AfterEach
    void cleanUp(){
        databaseCleaner.truncateAll();
        redisCleaner.clearAll();
    }

}
