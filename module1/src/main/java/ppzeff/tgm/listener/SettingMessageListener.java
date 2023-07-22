package ppzeff.tgm.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SettingMessageListener extends AbstractMessageListener {
    String command = "/setting";

    @Override
    public void onMessage(Message message) {
//        log.info("Invoking {}", message.text());
        sendMessage(
                new SendMessage(message.chat().id(), "Выбирете вендора услуг:")
                        .replyMarkup(new ReplyKeyboardMarkup(
                                        new KeyboardButton("/задатьВендора 1" ),
                                        new KeyboardButton("/задатьВендора 2" )
                                )
                                .addRow(new KeyboardButton("отмена"))
                                        .selective(true)
                                        .resizeKeyboard(true)
                                        .oneTimeKeyboard(true)
                        )
        );
    }

    public SettingMessageListener(TelegramBot bot, long chatId) {
        super(bot, chatId);
    }

    @Override
    public boolean invocation(Message message) {
        var text = message.text();
        return text.length() == command.length() && text.startsWith(command);
    }
}
