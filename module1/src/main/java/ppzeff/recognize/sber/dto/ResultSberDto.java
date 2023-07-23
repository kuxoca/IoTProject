package ppzeff.recognize.sber.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class ResultSberDto {

    @JsonProperty("result")
    private List<String> result;

    @JsonProperty("emotions")
    private List<EmotionsItemSberDto> emotions;

    @JsonProperty("status")
    private int status;
}