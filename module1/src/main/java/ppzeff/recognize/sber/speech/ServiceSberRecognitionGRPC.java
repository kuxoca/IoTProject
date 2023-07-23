package ppzeff.recognize.sber.speech;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
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
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public class ServiceSberRecognitionGRPC implements RecognizeService {
    private final ServiceAccessToken serviceAccessToken;
    private final Recognition.RecognitionRequest configRequest;
    public static final String SMARTSPEECH = "smartspeech.sber.ru";
    private final ManagedChannel channel;

    public ServiceSberRecognitionGRPC(ServiceAccessToken serviceAccessToken) throws SSLException {
        this.serviceAccessToken = serviceAccessToken;

        SslContext sslCtx = GrpcSslContexts.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
        configRequest = Recognition.RecognitionRequest.newBuilder()
                .setOptions(
                        Recognition.RecognitionOptions.newBuilder()
                                .setAudioEncodingValue(
                                        Recognition.RecognitionOptions.AudioEncoding.OPUS_VALUE)
                                .build())
                .build();
        channel = NettyChannelBuilder.forTarget(SMARTSPEECH).sslContext(sslCtx).build();
    }

    @Override
    public void recognize(byte[] bytes, Consumer<String> callback) {
        try {
            recognize(bytes, "RU-ru", callback);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void recognize(byte[] bytes, String lang, Consumer<String> callback) throws InterruptedException {

        var stub = createStub();

        final CountDownLatch finishLatch = new CountDownLatch(1);
        StreamObserver<Recognition.RecognitionResponse> streamObserver = new StreamObserver<>() {

            @Override
            public void onNext(Recognition.RecognitionResponse value) {
                log.trace("StreamObserver: onNext");
                List<Recognition.Hypothesis> resultsList = value.getResultsList();
                for (Recognition.Hypothesis hypothesis : resultsList) {
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

        Recognition.RecognitionRequest request = Recognition.RecognitionRequest.newBuilder()
                .setAudioChunk(ByteString.copyFrom(bytes))
                .build();


        var requestStreamObserver = stub.recognize(streamObserver);
        requestStreamObserver.onNext(configRequest);
        requestStreamObserver.onNext(request);
        requestStreamObserver.onCompleted();
        finishLatch.await();
    }

    @Override
    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    private SmartSpeechGrpc.SmartSpeechStub createStub() {
        var accessToken = serviceAccessToken.getAccessToken();
        var bearerToken = new BearerTokenSber(accessToken);
        return SmartSpeechGrpc.newStub(channel).withCallCredentials(bearerToken);
    }

}