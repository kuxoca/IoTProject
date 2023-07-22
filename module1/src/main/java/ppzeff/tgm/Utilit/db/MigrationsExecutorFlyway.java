package ppzeff.tgm.Utilit.db;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.hibernate.cfg.Configuration;

@Slf4j
public class MigrationsExecutorFlyway {

    private final Flyway flyway;
    private static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";


    public MigrationsExecutorFlyway(String dbUrl, String dbUserName, String dbPassword) {
        flyway = Flyway.configure()
                .dataSource(dbUrl, dbUserName, dbPassword)
                .locations("classpath:/db/migration")
                .load();
    }

    public void executeMigrations() {
        log.info("db migration started...");
        flyway.migrate();
        log.info("db migration finished.");
    }

    public static void flywayMigration() {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);
        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();
    }
}
