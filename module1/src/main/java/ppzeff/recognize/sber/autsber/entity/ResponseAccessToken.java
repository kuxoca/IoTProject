package ppzeff.recognize.sber.autsber.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ResponseAccessToken {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_at")
    private long expiresAt;

}
