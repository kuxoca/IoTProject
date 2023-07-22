package ppzeff.tgm.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import lombok.extern.slf4j.Slf4j;
import ppzeff.tgm.Utilit.Mapper;
import ppzeff.tgm.dto.UserActionDto;
import ppzeff.tgm.listener.AbstractMessageListener;
import ppzeff.tgm.listener.factory.AbstractListenerFactory;
import ppzeff.tgm.repo.UserRepo;
import ppzeff.tgm.repo.UserRepoImpl;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class BotServiceImp implements BotService {
    private static BotService instance;
    private final TelegramBot bot;
    private final UserRepo userRepo = new UserRepoImpl();
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    private final Map<Long, List<AbstractMessageListener>> builtListener = Collections.synchronizedMap(new WeakHashMap<>());
    private final List<AbstractListenerFactory> listenerFactories = Collections.synchronizedList(new ArrayList<>());

    private BotServiceImp() {
        if (System.getenv("BOT_TOKEN") == null) {
            log.error("Не задана переменная окружения 'BOT_TOKEN'");
            System.exit(1);
        } else {
            log.info("BOT_TOKEN: {}:****", Arrays.stream(System.getenv("BOT_TOKEN").split(":")).findFirst().get());
        }

        bot = new TelegramBot(System.getenv("BOT_TOKEN"));
        bot.setUpdatesListener(updates -> {
            try {
                for (Update update : updates) {
                    executorService.submit(() -> processUpdate(update));
                }
            } catch (RuntimeException e) {
                log.error("ERROR", e);
                throw new RuntimeException();
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    public void registerFactories(AbstractListenerFactory... factories) {
        List<AbstractListenerFactory> factory = Arrays.stream(factories).toList();
        registerListenerFactory(factory);
        configBotCommand(factory);
        log.info("config bot finish");
    }


    private void processUpdate(Update update) {
        Message message = update.message();
        if (message == null) {
            return;
        }
        logAction(message);
        if (message.text() != null && (message.text().equals("/help") || message.text().equals("/start"))) {
            sendHelpMessage(message);
            return;
        }
        getListeners(message).stream().filter(l -> l.invocation(message)).forEach(l -> l.onMessage(message));
    }

    private void logAction(Message message) {
        log.info("Invoking from userId: '{}', command: '{}'", message.from().id(), message.text());
        userRepo.save(Mapper.toUserDetail(
                UserActionDto.builder()
                        .command(message.text())
                        .userId(message.from().id())
                        .chatId(message.chat().id())
                        .userName(message.from().username())
                        .build())
        );
    }

    private void sendHelpMessage(Message message) {
        var helpBuilder = new StringBuilder();
        helpBuilder.append("<b>Доступные каманды:</b>");
        helpBuilder.append("\n<b>onGet voice message</b>").append("\n<i>при получении голосового сообщения в ответ отправляется текстовая транскрипция</i>");

        listenerFactories.stream().filter(AbstractListenerFactory::flag).forEach(f -> {
            helpBuilder.append("\n").append("<b>").append(f.getCommand()).append("</b>").append("\n").append("<i>").append(f.getInfo()).append("</i>");
        });
        helpBuilder.append("\n").append("<b>/help</b>\n<i>показать это сообщение</i>");
        var msg = new SendMessage(message.chat().id(), helpBuilder.toString());
        msg.parseMode(ParseMode.HTML);
        bot.execute(msg);
    }

    private List<AbstractMessageListener> getListeners(Message message) {
        if (builtListener.containsKey(message.chat().id())) {
            return builtListener.get(message.chat().id());
        }

        var messageListeners = new ArrayList<AbstractMessageListener>();
        for (var factory : listenerFactories) {
            var handler = factory.build(message.chat().id());
            messageListeners.add(handler);
        }
        builtListener.put(message.chat().id(), messageListeners);
        return messageListeners;
    }

    private void configBotCommand(List<AbstractListenerFactory> factory) {
        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand("/start", "начать работу"));
        commands.add(new BotCommand("/help", "получить помощь по работе с ботом"));

        factory.stream().filter(AbstractListenerFactory::flag).forEach(f -> {
            commands.add(new BotCommand(f.getCommand(), f.getInfo()));
            log.info("command {}", f.getCommand());
        });


        boolean ok = bot.execute(new SetMyCommands(commands.toArray(new BotCommand[0]))).isOk();
        log.info("set bot command {}", ok);
    }

    private void registerListenerFactory(List<AbstractListenerFactory> factories) {
        factories.forEach(factory -> {
            factory.setBot(bot);
            listenerFactories.add(factory);
            log.info("add listener factory: '{}' - {}", factory.getCommand(), factory.getInfo());
        });
    }

    @Override
    public void deleteListener(AbstractListenerFactory factory) {
        try {
            listenerFactories.remove(factory);
        } catch (RuntimeException e) {
//
        }
    }

    public static BotService getInstance() {
        if (instance == null) {
            instance = new BotServiceImp();
        }
        return instance;
    }
}
