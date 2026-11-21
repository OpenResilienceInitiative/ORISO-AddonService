package org.oriso.addonservice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * Startup must not log the database connection URL.
 *
 * <p>Hibernate logs HHH10001005 "Database info" at INFO with the URL that the JDBC driver reports.
 * MariaDB Connector/J reports it with {@code user=...&password=...} appended, so on Dev the
 * plain-text database password ended up in the pod log. H2 does not append the password, so this
 * test checks for the block itself: if it is logged here, it is logged with the password on
 * MariaDB.
 */
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(OutputCaptureExtension.class)
// A property of its own gives this class a fresh context, so its startup runs inside the capture.
@TestPropertySource(properties = "addons.test.context=startup-log-secrets")
class StartupLogSecretsIntegrationTest {

  @Test
  void startupDoesNotLogTheDatabaseConnectionUrl(CapturedOutput output) {
    assertThat(output.getAll())
        .as("Hibernate must have started, or this test proves nothing")
        .contains("Initialized JPA EntityManagerFactory");
    assertThat(output.getAll()).doesNotContain("HHH10001005").doesNotContain("Database JDBC URL");
  }
}
