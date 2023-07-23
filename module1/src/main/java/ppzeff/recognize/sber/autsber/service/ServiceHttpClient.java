package ppzeff.recognize.sber.autsber.service;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Slf4j
public class ServiceHttpClient {
    private static ServiceHttpClient instance;
    private static HttpClient client;

    public static synchronized HttpClient getClient() {
        if (instance == null) {
            synchronized (ServiceHttpClient.class) {
                if (instance == null) {
                    instance = new ServiceHttpClient();
                }
            }
        }
        return instance.getHttpClient();
    }

    private ServiceHttpClient() {
        log.info("create HttpClient");
        client = HttpClient.newBuilder()
                .sslContext(getSSLContext())
                .build();
    }

    private HttpClient getHttpClient() {
        return client;
    }


    private SSLContext getSSLContext() {
        SSLContext ctx;
        try {
            ctx = SSLContext.getInstance("TLS");
            ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
            SSLContext.setDefault(ctx);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        }
        return ctx;

    }

    private static class DefaultTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
}

