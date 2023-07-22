package ppzeff.tgm.listener.factory;

import com.pengrad.telegrambot.TelegramBot;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ppzeff.tgm.listener.AbstractMessageListener;

@Getter
@Setter
@Slf4j
public abstract class AbstractListenerFactory {
    private TelegramBot bot;

    public abstract AbstractMessageListener build(long chatId);

    public abstract String getInfo();

    public abstract String getCommand();

    public abstract boolean flag();

}
