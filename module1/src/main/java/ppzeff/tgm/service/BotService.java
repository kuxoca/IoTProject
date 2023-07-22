package ppzeff.tgm.service;

import ppzeff.tgm.listener.factory.AbstractListenerFactory;

public interface BotService {
    void registerFactories(AbstractListenerFactory... factories);

    void deleteListener(AbstractListenerFactory factory);
}
