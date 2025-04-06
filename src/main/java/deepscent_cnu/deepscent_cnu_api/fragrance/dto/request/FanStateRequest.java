package deepscent_cnu.deepscent_cnu_api.fragrance.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FanStateRequest(
    @JsonProperty("fan_number")
    int fanNumber,
    @JsonProperty("fan_speed")
    int fanSpeed
) {

}
