package ppzeff.recognize.sber.autsber.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceObjectMapper {
    private final ObjectMapper objectMapper;
    private static ServiceObjectMapper instance;

    public static synchronized ObjectMapper getObjectMapper() {
        if (instance == null) {
            synchronized (ServiceObjectMapper.class) {
                if (instance == null) {
                    instance = new ServiceObjectMapper();
                }
            }
        }
        return instance.objectMapper;
    }

    private ServiceObjectMapper() {
        log.info("create ObjectMapper");
        objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}

