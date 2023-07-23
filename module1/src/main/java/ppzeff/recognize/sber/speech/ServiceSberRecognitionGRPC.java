package ppzeff.recognize.sber.speech;

import com.google.protobuf.ByteString;
import io.grpc.Channel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.netty.shaded.io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import ppzeff.protogenerate.recognition.Recognition;
import ppzeff.protogenerate.recognition.SmartSpeechGrpc;
import ppzeff.recognize.sber.RecognizeService;
import ppzeff.recognize.sber.autsber.ServiceAccessToken;
import ppzeff.recognize.sber.config.BearerTokenSber;

import javax.net.ssl.SSLException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public class ServiceSberRecognitionGRPC implements RecognizeService {
    private final ServiceAccessToken serviceAccessToken;
    private final Recognition.RecognitionRequest configRequest;
    private final SslContext sslCtx;
    public static final String SMARTSPEECH = "smartspeech.sber.ru";
    private CountDownLatch latch;


    public ServiceSberRecognitionGRPC(ServiceAccessToken serviceAccessToken) throws SSLException {
        this.serviceAccessToken = serviceAccessToken;

        sslCtx = GrpcSslContexts.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
        configRequest = Recognition.RecognitionRequest.newBuilder()
                .setOptions(
                        Recognition.RecognitionOptions.newBuilder()
                                .setAudioEncodingValue(
                                        Recognition.RecognitionOptions.AudioEncoding.OPUS_VALUE)
                                .build())
                .build();
    }

    @Override
    public void recognize(byte[] bytes, Consumer<String> callback) {
        recognize(bytes, "RU-ru", callback);
    }

    @Override
    public void recognize(byte[] bytes, String lang, Consumer<String> callback) {
        latch = new CountDownLatch(1);
        var channel = NettyChannelBuilder.forTarget(SMARTSPEECH).sslContext(sslCtx).build();
        try {
            sendRequest(bytes, channel, callback);
            latch.await(2L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            channel.shutdown();
            log.trace("channel.shutdown()");
        }
    }

    private StreamObserver<Recognition.RecognitionResponse> getResponseObserver(Consumer<String> callback) {
        log.info("create streamObserver");
        final CountDownLatch finishLatch = new CountDownLatch(1);

        var streamObserver = new StreamObserver<Recognition.RecognitionResponse>() {
            @Override
            public void onNext(Recognition.RecognitionResponse value) {
                log.trace("StreamObserver: onNext");
                List<Recognition.Hypothesis> resultsList = value.getResultsList();
                for (Recognition.Hypothesis hypothesis : resultsList) {
//                    log.info(hypothesis.getNormalizedText());
                    callback.accept(hypothesis.getNormalizedText());
                }
            }

            @Override
            public void onError(Throwable t) {
                log.trace("StreamObserver: onError");
                log.error(t.getMessage());
                finishLatch.countDown();
            }

            @Override
            public void onCompleted() {
                finishLatch.countDown();
                log.trace("StreamObserver: onCompleted");
            }
        };
        return streamObserver;
    }

    private void sendRequest(byte[] bytes, Channel channel, Consumer<String> display) {
        try {
            var accessToken = serviceAccessToken.getAccessToken();
            var bearerToken = new BearerTokenSber(accessToken);
            var stub = SmartSpeechGrpc.newStub(channel).withCallCredentials(bearerToken);
            var requestStreamObserver = stub.recognize(getResponseObserver(display));

            requestStreamObserver.onNext(configRequest);
            requestStreamObserver.onNext(
                    Recognition.RecognitionRequest.newBuilder()
                            .setAudioChunk(ByteString.readFrom(new ByteArrayInputStream(bytes)))
                            .build()
            );
            requestStreamObserver.onCompleted();
        } catch (RuntimeException e) {
//
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}