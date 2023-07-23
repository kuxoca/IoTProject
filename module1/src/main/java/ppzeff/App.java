package ppzeff;


import lombok.extern.slf4j.Slf4j;
import ppzeff.recognize.sber.RecognizeService;
import ppzeff.recognize.sber.autsber.service.ServiceAccessTokenSber;
import ppzeff.recognize.sber.speech.ServiceSberRecognitionGRPC;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class App {
    private static final ExecutorService executorService = Executors.newFixedThreadPool(3);

    public static void main(String[] args) throws IOException {
        byte[] bytes = Files.readAllBytes(Path.of("audio_2023-07-22_18-21-29.ogg"));

        RecognizeService recognizeService = new ServiceSberRecognitionGRPC(ServiceAccessTokenSber.getInstance());
        for (int i = 0; i <= 5; i++) {
            int finalI = i;
            executorService.submit(() -> recognizeService.recognize(bytes, s -> log.info("{}, {}", s, finalI)));

        }
    }
}
