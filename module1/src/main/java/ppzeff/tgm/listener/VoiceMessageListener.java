package ppzeff.tgm.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Voice;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetFileResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Slf4j
public class VoiceMessageListener extends AbstractMessageListener {

    @Override
    public void onMessage(Message message) {
        var byteFromVoice = getByteFromVoice(message.voice());

        String recognizeText = String.valueOf(byteFromVoice.length);
        sendMessage(
                new SendMessage(message.chat().id(), recognizeText)
                        .replyToMessageId(message.messageId())
        );
    }

    public VoiceMessageListener(TelegramBot bot, long chatId) {
        super(bot, chatId);
    }

    @Override
    public boolean invocation(Message message) {
        return message.voice() != null;
    }

    private byte[] getByteFromVoice(Voice voice) {
        GetFile getFile = new GetFile(voice.fileId());
        GetFileResponse getFileResponse = getBot().execute(getFile);
        File file = getFileResponse.file();
        String fullPath = getBot().getFullFilePath(file);

        try (InputStream inputStream = new URL(fullPath).openStream()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
