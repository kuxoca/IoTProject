package ppzeff.tgm.listener.factory;


import ppzeff.tgm.listener.AbstractMessageListener;
import ppzeff.tgm.listener.SettingMessageListener;

public class SettingListenerFactory extends AbstractListenerFactory{
    @Override
    public AbstractMessageListener build(long chatId) {
        return new SettingMessageListener(getBot(), chatId);
    }

    @Override
    public String getInfo() {
        return "настройка параметров работы телеграмм бота";
    }

    @Override
    public String getCommand() {
        return "/setting";
    }

    @Override
    public boolean flag() {
        return true;
    }
}
