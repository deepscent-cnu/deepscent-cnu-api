package deepscent_cnu.deepscent_cnu_api.fragrance.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record FanStateRequest(

    @Min(value = 1, message = "기기 번호는 1, 2, 3 중 하나여야 합니다.")
    @Max(value = 3, message = "기기 번호는 1, 2, 3 중 하나여야 합니다.")
    Integer deviceNumber,

    @Min(value = 1, message = "슬롯 번호는 1~4 중 하나여야 합니다.")
    @Max(value = 4, message = "슬롯 번호는 1~4 중 하나여야 합니다.")
    Integer fanNumber,

    @Min(value = 0, message = "발향 세기는 0~3 중 하나여야 합니다.")
    @Max(value = 3, message = "발향 세기는 0~3 중 하나여야 합니다.")
    Integer fanSpeed
) {

}
