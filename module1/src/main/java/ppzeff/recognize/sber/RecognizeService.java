package ppzeff.recognize.sber;

import java.util.function.Consumer;

public interface RecognizeService {
    void recognize(byte[] bytes, Consumer<String> callback) ;

    void recognize(byte[] bytes, String lang, Consumer<String> callback) throws InterruptedException;
    void shutdown() throws InterruptedException;
}
