package ppzeff.tgm.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class AbstractMessageListener {
    final TelegramBot bot;
    final long chatId;

    abstract public void onMessage(Message message);

    protected AbstractMessageListener(TelegramBot bot, long chatId) {
        this.bot = bot;
        this.chatId = chatId;
    }

    boolean sendMessage(String message) {
        var newMessage = new SendMessage(chatId, message).replyMarkup(new ReplyKeyboardRemove());
        return bot.execute(newMessage).isOk();
    }

    boolean sendMessage(BaseRequest request) {
        return bot.execute(request).isOk();
    }

    abstract public boolean invocation(Message message);

}
