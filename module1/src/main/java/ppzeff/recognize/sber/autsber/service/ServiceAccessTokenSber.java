package ppzeff.recognize.sber.autsber.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ppzeff.recognize.sber.autsber.ServiceAccessToken;
import ppzeff.recognize.sber.autsber.entity.ResponseAccessToken;


import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class ServiceAccessTokenSber implements ServiceAccessToken {
    private static ServiceAccessTokenSber instance;

    private ResponseAccessToken accessToken;
    private final HttpClient client;
    private final ObjectMapper objectMapper;
    private final Long refreshPeriod = 25 * 60_000L;
    private final String BASIC_TYPE = "Basic";
    private final String SBER_URL_Access_Token = System.getenv("SBER_URL_Access_Token");
    private final String CLIENT_ID_SBER_Access_Token = System.getenv("CLIENT_ID_SBER_Access_Token");
    private final String CLIENT_Secret_SBER_Access_Token = System.getenv("CLIENT_Secret_SBER_Access_Token");

    public static synchronized ServiceAccessTokenSber getInstance() {
        if (instance == null) {
            synchronized (ServiceAccessTokenSber.class) {
                if (instance == null) {
                    instance = new ServiceAccessTokenSber(
                            ServiceHttpClient.getClient(),
                            ServiceObjectMapper.getObjectMapper());
                }
            }
        }
        return instance;
    }

    public ServiceAccessTokenSber(HttpClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
        refreshAccessToken();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                refreshAccessToken();
            }
        }, refreshPeriod, refreshPeriod);
    }

    @Override
    public String getAccessToken() {
        return String.valueOf(accessToken.getAccessToken());
    }

    private HttpRequest createRequest(String clientId, String clientSecret, UUID uuid) {
        String originalInput = String.format("%s:%s", clientId, clientSecret);
        String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
        Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put("scope", "SALUTE_SPEECH_PERS");
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(SBER_URL_Access_Token)).
                header("Authorization", String.format("%s %s", BASIC_TYPE, encodedString))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("rquid", uuid.toString())
                .POST(HttpRequest.BodyPublishers.ofString(getFormDataAsString(urlParameters)))
                .build();
        return request;
    }

    private void refreshAccessToken() {
        log.info("sending Http request to sber: refreshAccessToken");

        HttpRequest request = createRequest(
                CLIENT_ID_SBER_Access_Token,
                CLIENT_Secret_SBER_Access_Token,
                UUID.randomUUID());

        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            switch (response.statusCode()) {
                case 400 ->
                        log.error("{}", "400: Bad Request. Неправильный или некорректный запрос\ndata from server: " + response.body());
                case 401 -> log.error("{}", "401: Unauthorized. Не авторизован");
                case 500 -> log.error("{}", "500: Internal Server Error. Внутренняя ошибка сервера");
                case 200 -> {
                    log.trace("{}", "200: Ok. Ок");
                    accessToken = objectMapper.readValue(response.body(), ResponseAccessToken.class);
                }
                default -> log.info("{}: {}", response.statusCode(), response.body());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getFormDataAsString(Map<String, String> formData) {
        StringBuilder formBodyBuilder = new StringBuilder();
        for (Map.Entry<String, String> singleEntry : formData.entrySet()) {
            if (formBodyBuilder.length() > 0) {
                formBodyBuilder.append("&");
            }
            formBodyBuilder.append(URLEncoder.encode(singleEntry.getKey(), StandardCharsets.UTF_8));
            formBodyBuilder.append("=");
            formBodyBuilder.append(URLEncoder.encode(singleEntry.getValue(), StandardCharsets.UTF_8));
        }
        return formBodyBuilder.toString();
    }
}
