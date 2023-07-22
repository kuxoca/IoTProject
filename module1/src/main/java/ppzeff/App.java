package ppzeff;


import lombok.extern.slf4j.Slf4j;
import ppzeff.tgm.Utilit.db.MigrationsExecutorFlyway;
import ppzeff.tgm.listener.factory.SettingListenerFactory;
import ppzeff.tgm.listener.factory.VoiceListenerFactory;
import ppzeff.tgm.service.BotService;
import ppzeff.tgm.service.BotServiceImp;

@Slf4j
public class App {
    public static void main(String[] args) {

        MigrationsExecutorFlyway.flywayMigration();

        BotService botServiceImp;
        try {
            botServiceImp = BotServiceImp.getInstance();
            botServiceImp.registerFactories(
                    new VoiceListenerFactory(),
                    new SettingListenerFactory()
            );
            log.info("Wait message... https://t.me/DCSRM2_bot");
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }
}
