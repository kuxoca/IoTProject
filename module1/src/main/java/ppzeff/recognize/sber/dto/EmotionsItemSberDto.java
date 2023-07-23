package ppzeff.recognize.sber.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmotionsItemSberDto {

	@JsonProperty("negative")
	private Object negative;

	@JsonProperty("neutral")
	private Object neutral;

	@JsonProperty("positive")
	private Object positive;
}